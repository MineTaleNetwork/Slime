package cc.minetale.slime;

import cc.minetale.slime.core.GameExtension;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.network.ConnectionManager;

public final class Slime extends Extension {

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();

    //TODO Make the setter its own method and safely switch games
    //TODO Figure out a reasonable way to shorten any calls to this, now for example you have to do Slime.getActiveGame().getMaxGames()
    @Getter @Setter private static GameExtension activeGame;

    @Override public void initialize() { }

    @Override public void terminate() { }

}
