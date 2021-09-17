package cc.minetale.slime;

import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.core.GameLobby;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.InstanceManager;

public final class Slime extends Extension {

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    @Setter private static GameExtension activeGame; //TODO Make the setter its own method and safely switch games

    @Override public void initialize() {
        GameLobby.PARENT_INSTANCE = INSTANCE_MANAGER.createInstanceContainer();
    }

    @Override public void terminate() { }

}
