package cc.minetale.slime.core;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * As to avoid confusion. There is only one instance of {@linkplain GameLobby}, <br>
 * but players are separated "logically" into their respective games.
 */
public class GameLobby {

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final InstanceContainer PARENT_INSTANCE = INSTANCE_MANAGER.createInstanceContainer();

    @Getter private final Instance instance;
    @Getter private final Game game;

    protected Set<GamePlayer> waiters = Collections.synchronizedSet(new HashSet<>());

    public GameLobby(Game game) {
        this.instance = INSTANCE_MANAGER.createSharedInstance(PARENT_INSTANCE);
        this.game = game;
    }

    public boolean addPlayer(GamePlayer player) {
        if(player.getLobby() != null) { return false; }
        player.setLobby(this);
        return this.waiters.add(player);
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getLobby() != this) { return false; }
        player.setLobby(null);
        return this.waiters.remove(player);
    }

}
