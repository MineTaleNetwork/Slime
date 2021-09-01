package cc.minetale.slime.core;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameLobby<G extends Game<G,P,S>, P extends GamePlayer<P,S,G>, S extends GameState<S,P,G>> {

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final InstanceContainer PARENT_INSTANCE = INSTANCE_MANAGER.createInstanceContainer();

    @Getter private final Instance instance;

    protected Map<G, Set<P>> waiters = Collections.synchronizedMap(new HashMap<>());

    public GameLobby() {
        this.instance = INSTANCE_MANAGER.createSharedInstance(PARENT_INSTANCE);
    }

    public boolean addPlayer(@Nullable G game, P player) {
        if(player.getLobby() != null) { return false; }

        if(game == null) {
            var ent = findAvailableGame();
            if(ent == null) { return false; }

            ent.getValue().add(player);
        }

        var waiters = this.waiters.get(game);
        return waiters.add(player);
    }

    public boolean removePlayer(P player) {
        if(player.getLobby() != this || player.getGame() == null) { return false; }

        var waiters = this.waiters.get(player.getGame());
        return waiters.remove(player);
    }

    public void addGame(G game) {
        this.waiters.put(game, new HashSet<>());
    }

    public void removeGame(G game) {
        this.waiters.remove(game);
    }

    private @Nullable Map.Entry<G, Set<P>> findAvailableGame() {
        for(var ent : this.waiters.entrySet()) {
            var game = ent.getKey();

            if(!game.canFitPlayer()) { continue; }

            return ent;
        }

        return null;
    }

}
