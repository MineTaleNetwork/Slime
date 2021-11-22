package cc.minetale.slime;

import cc.minetale.slime.commands.SlimeCommand;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.map.tools.ToolManager;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extensions.Extension;
import net.minestom.server.extensions.ExtensionManager;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.network.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slime extends Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slime.class);

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    public static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();

    public static final ExtensionManager EXTENSION_MANAGER = MinecraftServer.getExtensionManager();

    public static final ToolManager TOOL_MANAGER = new ToolManager();

    public static final SlimeCommand MAIN_CMD = new SlimeCommand();

    //TODO Make the setter its own method and safely switch games
    //TODO Figure out a reasonable way to shorten any calls to this, now for example you have to do Slime.getActiveGame().getMaxGames()
    @Getter @Setter private static GameExtension activeGame;

    @Override public void initialize() {
        COMMAND_MANAGER.register(MAIN_CMD);

        if(EXTENSION_MANAGER.hasExtension("buildingtools")) {
            LOGGER.info("Building Tools has been detected. Loading Slime as tooling.");
            TOOL_MANAGER.initialize();
        }
    }

    @Override public void terminate() { }

}
