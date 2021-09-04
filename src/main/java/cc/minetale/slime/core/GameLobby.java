package cc.minetale.slime.core;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameLobby {

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final InstanceContainer PARENT_INSTANCE = INSTANCE_MANAGER.createInstanceContainer();

    @Getter private final Instance instance;

    protected Map<Game, Set<GamePlayer>> waiters = Collections.synchronizedMap(new HashMap<>());

    public GameLobby() {
        this.instance = INSTANCE_MANAGER.createSharedInstance(PARENT_INSTANCE);
    }

    public boolean addPlayer(@Nullable Game game, GamePlayer player) {
        if(player.getLobby() != null) { return false; }

        if(game == null) {
            var ent = findAvailableGame();
            if(ent == null) { return false; }

            ent.getValue().add(player);
        }

        var waiters = this.waiters.get(game);
        return waiters.add(player);
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getLobby() != this || player.getGame() == null) { return false; }

        var waiters = this.waiters.get(player.getGame());
        return waiters.remove(player);
    }

    public void addGame(Game game) {
        this.waiters.put(game, new HashSet<>());
    }

    public void removeGame(Game game) {
        this.waiters.remove(game);
    }

    private @Nullable Map.Entry<Game, Set<GamePlayer>> findAvailableGame() {
        for(var ent : this.waiters.entrySet()) {
            var game = ent.getKey();

            if(!game.canFitPlayer()) { continue; }

            return ent;
        }

        return null;
    }

}
