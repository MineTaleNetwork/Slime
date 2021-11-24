package cc.minetale.slime.map.tools;

import cc.minetale.slime.Slime;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.tools.commands.GameCommand;
import cc.minetale.slime.map.tools.commands.MapCommand;
import cc.minetale.slime.utils.MapUtil;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.minestom.server.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToolManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolManager.class);

    @Getter private boolean isEnabled;

    private Set<GameExtension> availableGames;

    /** Active temporary maps on the server, ones that are loaded, have their own instance and are accessible. */
    private Set<TempMap> activeMaps;

    public void initialize() {
        if(this.isEnabled) {
            LOGGER.warn("ToolManager has been already initialized.");
            return;
        }

        //Commands
        var mainCmd = Slime.MAIN_CMD;
        mainCmd.addSubcommand(new MapCommand());
        mainCmd.addSubcommand(new GameCommand());

        this.availableGames = Collections.synchronizedSet(new HashSet<>());
        this.activeMaps = Collections.synchronizedSet(new HashSet<>());

        //Mark as initialized
        this.isEnabled = true;
    }

    public Set<GameExtension> getAvailableGames() {
        return Collections.unmodifiableSet(this.availableGames);
    }

    public Optional<GameExtension> getGame(String id) {
        return this.availableGames.stream()
                .filter(other -> other.getId().equals(id))
                .findFirst();
    }

    public boolean addGame(GameExtension extension) {
        if(isGameAvailable(extension)) { return false; }
        this.availableGames.add(extension);
        return true;
    }

    public boolean removeGame(GameExtension extension) {
        return this.availableGames.remove(extension);
    }

    public boolean isGameAvailable(String id) {
        return this.availableGames.stream()
                .anyMatch(other -> Objects.equals(other.getId(), id));
    }

    private boolean isGameAvailable(GameExtension extension) {
        return isGameAvailable(extension.getId());
    }

    public Set<TempMap> getActiveMaps() {
        return Collections.unmodifiableSet(this.activeMaps);
    }

    public Optional<TempMap> getMap(String gamemode, String id) {
        return this.activeMaps.stream()
                .filter(map -> {
                    var handle = map.getHandle();
                    return Objects.equals(handle.getId(), id) && Objects.equals(handle.getGamemode(), gamemode);
                }).findFirst();
    }

    public Optional<TempMap> getMap(String id) {
        return getMap(Slime.getActiveGame().getId(), id);
    }

    public Optional<TempMap> getMapByInstance(Instance instance) {
        return this.activeMaps.stream()
                .filter(map -> map.getInstance().equals(instance)).findFirst();
    }

    /**
     * Tries to add a map as a new {@link TempMap} or returns null if an already existing {@link TempMap} if one is found. <br>
     * Usually used for offline/not yet saved, new maps.
     */
    public boolean addMap(TempMap map) {
        return this.activeMaps.add(map);
    }

    /**
     * Tries to load an existing map as a new {@link TempMap} or returns an already existing {@link TempMap} if one is found.
     */
    public TempMap loadMap(String gamemode, String id) {
        var otherMap = getMap(gamemode, id);
        if(otherMap.isPresent()) { return otherMap.get(); }

        var oGame = getGame(gamemode);
        if(oGame.isEmpty()) { return null; }
        var game = oGame.get();

        var map = GameMap.fromBoth(gamemode, id, game.getMapProvider());
        var tempMap = TempMap.ofMap(map, true);

        this.activeMaps.add(tempMap);
        return tempMap;
    }

    public boolean removeMap(TempMap map) {
        return this.activeMaps.remove(map);
    }

    public CompletableFuture<Boolean> saveMap(TempMap map, boolean includeOptions, boolean includeBlocks) {
        var handle = map.getHandle();
        var result = new AtomicBoolean(true);

        if(includeOptions) {
            result.set(GameMap.getCollection().replaceOne(handle.getFilter(), handle.toDocument(), new ReplaceOptions().upsert(true)).getModifiedCount() > 0);
            if(!includeBlocks)
                return CompletableFuture.completedFuture(result.get());
        }

        if(includeBlocks) {
            return map.saveBlocks()
                    .thenCompose(success -> {
                        //TODO Success is false even if it was successful
                        return CompletableFuture.completedFuture(success && result.get());
                    });
        }

        return CompletableFuture.completedFuture(result.get());
    }

    public boolean mapExists(String gamemode, String id, boolean temp, boolean database) {
        if(temp && this.activeMaps.stream()
                .anyMatch(map -> {
                    var handle = map.getHandle();
                    return Objects.equals(handle.getId(), id) && Objects.equals(handle.getGamemode(), gamemode);
                })) {

            return true;
        }

        if(database) { return MapUtil.isInDatabase(id, gamemode); }

        return false;
    }

    public List<TempMap> getActiveMapsForGame(GameExtension game) {
        return this.activeMaps.stream()
                .filter(map -> map.getGame() == game || Objects.equals(map.getGame().getId(), game.getId()))
                .toList();
    }

}
