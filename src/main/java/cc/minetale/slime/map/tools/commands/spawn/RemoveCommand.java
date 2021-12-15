package cc.minetale.slime.map.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.map.tools.commands.SpawnCommand.SPAWN_AUTO_ARG;

public final class RemoveCommand extends Command {

    public RemoveCommand() {
        super("create");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::createSpawnPoint, SPAWN_AUTO_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime spawn create <id> [position] [rotation]", NamedTextColor.GRAY)));
    }

    public void createSpawnPoint(CommandSender sender, CommandContext context) {
        var id = context.get(SPAWN_AUTO_ARG);

        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        if(MapUtil.isSpawnIdAvailable(handle, id)) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Spawn with this ID doesn't exist.", NamedTextColor.RED)));
            return;
        }

        handle.removeSpawn(id);

        sender.sendMessage(MC.notificationMessage("Map", Component.text()
                .append(Component.text("Successfully removed a spawn with ID \"" + id + "\" at \"" + MapUtil.getFullId(handle) + ".",
                                NamedTextColor.GREEN),
                        Component.newline(),
                        Component.text("- Remember to save the map with \"/slime map save\" to keep the change.",
                                NamedTextColor.YELLOW))
                .build()));
    }

}
