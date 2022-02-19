package cc.minetale.slime;

import cc.minetale.slime.commands.SlimeCommand;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.item.ItemManager;
import cc.minetale.slime.tools.ToolManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extensions.Extension;
import net.minestom.server.extensions.ExtensionManager;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.timer.SchedulerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Slime extends Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slime.class);

    public static final ExtensionManager EXTENSION_MANAGER = MinecraftServer.getExtensionManager();
    public static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    public static final SchedulerManager SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager();

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();
    public static final TeamManager TEAM_MANAGER = MinecraftServer.getTeamManager();
    public static final TagManager TAG_MANAGER = MinecraftServer.getTagManager();

    public static final ToolManager TOOL_MANAGER = new ToolManager();
    public static final ItemManager ITEM_MANAGER = new ItemManager();

    private static final Map<String, GameInfo> registeredGames = Collections.synchronizedMap(new HashMap<>());

    public static final SlimeCommand MAIN_CMD = new SlimeCommand();

    @Override
    public LoadStatus initialize() {
        COMMAND_MANAGER.register(MAIN_CMD);

        if(EXTENSION_MANAGER.hasExtension("buildingtools")) {
            LOGGER.info("Building Tools has been detected. Loading Slime as tooling.");
            TOOL_MANAGER.initialize();
        }

        return LoadStatus.SUCCESS;
    }


    public static GameInfo getRegisteredGame(String id) {
        return registeredGames.get(id);
    }

    public static boolean registerGame(@NotNull GameInfo game) {
        return registeredGames.putIfAbsent(game.getId(), game) == null;
    }

    public static boolean unregisterGame(String id) {
        return registeredGames.remove(id) != null;
    }

    public static boolean unregisterGame(@NotNull GameInfo game) {
        return unregisterGame(game.getId());
    }

    public static @NotNull @UnmodifiableView Collection<GameInfo> getRegisteredGames() {
        return Collections.unmodifiableCollection(registeredGames.values());
    }

    @Override public void terminate() { }

}
