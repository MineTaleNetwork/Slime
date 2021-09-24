package cc.minetale.slime;

import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.core.GameLobby;
import cc.minetale.slime.core.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.network.ConnectionManager;

public final class Slime extends Extension {

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();

    @Getter @Setter private static GameExtension activeGame; //TODO Make the setter its own method and safely switch games

    @Override public void initialize() {
        GameLobby.PARENT_INSTANCE = INSTANCE_MANAGER.createInstanceContainer();
        CONNECTION_MANAGER.setPlayerProvider(GamePlayer::new);
    }

    @Override public void terminate() { }

}
