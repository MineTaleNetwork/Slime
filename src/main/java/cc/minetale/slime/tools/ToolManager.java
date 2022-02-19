package cc.minetale.slime.tools;

import cc.minetale.buildingtools.Builder;
import cc.minetale.slime.Slime;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.*;
import cc.minetale.slime.tools.commands.*;
import cc.minetale.slime.utils.MapUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.scoreboard.Sidebar;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToolManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToolManager.class);

    @Getter private boolean isEnabled;

    /** Active temporary maps on the server, ones that are loaded, have their own instance and are accessible. */
    private Set<TempMap> activeGameMaps;
    private Set<TempMap> activeLobbyMaps;

    public void initialize() {
        if(this.isEnabled) {
            LOGGER.warn("ToolManager has been already initialized.");
            return;
        }

        //Commands
        var mainCmd = Slime.MAIN_CMD;
        mainCmd.addSubcommand(new GameCommand());

        mainCmd.addSubcommand(new MapCommand());
        mainCmd.addSubcommand(new LobbyCommand());

        mainCmd.addSubcommand(new SpawnCommand());

        mainCmd.addSubcommand(new DebugCommand());

        //Events
        MinecraftServer.getGlobalEventHandler()
                .addChild(EventNode.all("toolManager")
                        .addListener(PlayerSpawnEvent.class, event -> {
                            if(!event.isFirstSpawn()) { return; }

                            var builder = Builder.fromPlayer(event.getPlayer());

                            var sidebar = builder.getSidebar();
                            sidebar.createLine(new Sidebar.ScoreboardLine("4", Component.text()
                                    .append(Component.text("Map: ", NamedTextColor.GOLD, TextDecoration.BOLD),
                                            Component.text("N/A", NamedTextColor.GRAY))
                                    .build(), 4));
                            sidebar.createLine(new Sidebar.ScoreboardLine("3", Component.text()
                                    .append(Component.text("Type: ", NamedTextColor.GOLD, TextDecoration.BOLD),
                                            Component.text("N/A", NamedTextColor.GRAY))
                                    .build(), 3));
                        }));

        this.activeGameMaps = Collections.synchronizedSet(new HashSet<>());
        this.activeLobbyMaps = Collections.synchronizedSet(new HashSet<>());

        //Mark as initialized
        this.isEnabled = true;
    }

    public Set<TempMap> getActiveGameMaps() {
        return Collections.unmodifiableSet(this.activeGameMaps);
    }

    public Set<TempMap> getActiveLobbyMaps() {
        return Collections.unmodifiableSet(this.activeLobbyMaps);
    }

    public TempMap getMap(AbstractMap.Type type, String gamemode, String id) {
        return getActiveMapsOf(type).stream()
                .filter(map -> {
                    var handle = map.getHandle();
                    return Objects.equals(handle.getId(), id) && Objects.equals(handle.getGamemode(), gamemode);
                })
                .findFirst()
                .orElse(null);
    }

    public TempMap getMapByInstance(AbstractMap.Type type, Instance instance) {
        return getActiveMapsOf(type).stream()
                .filter(map -> map.getInstance().equals(instance))
                .findFirst()
                .orElse(null);
    }

    /** If you know what type the map is, use {@linkplain ToolManager#getMapByInstance(AbstractMap.Type, Instance)}. */
    public TempMap getMapByInstance(Instance instance) {
        for(AbstractMap.Type type : AbstractMap.Type.values()) {
            var map = getMapByInstance(type, instance);
            if(map != null) { return map; }
        }

        return null;
    }

    /**
     * Tries to add a map as a new {@link TempMap} or returns null if an already existing {@link TempMap} if one is found. <br>
     * Usually used for offline/not yet saved, new maps.
     */
    public boolean addMap(TempMap map) {
        var handle = map.getHandle();
        if(handle instanceof GameMap) {
            return this.activeGameMaps.add(map);
        } else if(handle instanceof LobbyMap) {
            return this.activeLobbyMaps.add(map);
        }
        return false;
    }

    /**
     * Tries to load an existing map as a new {@link TempMap} or returns an already existing {@link TempMap} if one is found.
     */
    public TempMap loadMap(AbstractMap.Type type, String gamemode, String id) {
        var otherMap = getMap(type, gamemode, id);
        if(otherMap != null) { return otherMap; }

        var game = Slime.getRegisteredGame(gamemode);
        if(game == null) { return null; }

        MapResolver<AbstractMap> resolver = type.getResolver(game);
        MapProvider<AbstractMap> provider = type.getProvider(game);
        var map = resolver.fromBoth(gamemode, id, provider);

        var tempMap = TempMap.ofMap(map, true);

        this.activeGameMaps.add(tempMap);
        return tempMap;
    }

    public boolean removeMap(TempMap map) {
        return this.activeGameMaps.remove(map) || this.activeLobbyMaps.remove(map);
    }

    public CompletableFuture<Boolean> saveMap(TempMap map, boolean includeOptions, boolean includeBlocks) {
        var handle = map.getHandle();
        var result = new AtomicBoolean(true);

        if(includeOptions) {
            MongoCollection<Document> collection;
            if(handle instanceof GameMap) {
                collection = GameMap.getCollection();
            } else if(handle instanceof LobbyMap) {
                collection = LobbyMap.getCollection();
            } else {
                return CompletableFuture.completedFuture(false);
            }

            result.set(collection.replaceOne(handle.getFilter(), handle.toDocument(), new ReplaceOptions().upsert(true)).getModifiedCount() > 0);

            if(!includeBlocks)
                return CompletableFuture.completedFuture(result.get());
        }

        if(includeBlocks) {
            return map.saveBlocks()
                    //TODO Success is false even if it was successful
                    .thenCompose(success -> CompletableFuture.completedFuture(success && result.get()));
        }

        return CompletableFuture.completedFuture(result.get());
    }

    public boolean mapExists(AbstractMap.Type type, String gamemode, String id, boolean temp, boolean database) {
        if(temp && getActiveMapsOf(type).stream()
                .anyMatch(map -> {
                    var handle = map.getHandle();
                    return Objects.equals(handle.getId(), id) && Objects.equals(handle.getGamemode(), gamemode);
                })) {

            return true;
        }

        if(database) { MapUtil.isMapInDatabase(type, gamemode, id); }

        return false;
    }

    public List<TempMap> getActiveMapsForGame(AbstractMap.Type type, GameInfo game) {
        return getActiveMapsOf(type).stream()
                .filter(map -> map.getGame() == game || Objects.equals(map.getGame().getId(), game.getId()))
                .toList();
    }

    public Set<TempMap> getActiveMapsOf(AbstractMap.Type type) {
        if(type == AbstractMap.Type.GAME) {
            return this.activeGameMaps;
        } else if(type == AbstractMap.Type.LOBBY) {
            return this.activeLobbyMaps;
        } else {
            return Collections.emptySet();
        }
    }

}
