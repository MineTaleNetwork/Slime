package cc.minetale.slime.map.tools.commands.spawn;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
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
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime spawn create <id> [position] [rotation]",
                MC.CC.GRAY.getTextColor())));
    }

    public void createSpawnPoint(CommandSender sender, CommandContext context) {
        var id = context.get(SPAWN_AUTO_ARG);

        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", MC.CC.RED.getTextColor())));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        if(MapUtil.isSpawnIdAvailable(handle, id)) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Spawn with this ID doesn't exist.", MC.CC.RED.getTextColor())));
            return;
        }

        handle.removeSpawnPoint(id);

        sender.sendMessage(MC.Chat.notificationMessage("Map",
                Component.text("Successfully removed a spawn with ID \"" + id + "\" at \"" + MapUtil.getFullId(handle) + ".",
                                MC.CC.GREEN.getTextColor())
                        .append(Component.newline())
                        .append(Component.text(
                                "- Remember to save the map with \"/slime map save\" to keep the change.",
                                MC.CC.YELLOW.getTextColor()))));
    }

}
