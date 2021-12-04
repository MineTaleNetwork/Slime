package cc.minetale.slime.map.tools.commands.spawn;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.spawn.SpawnPoint;
import cc.minetale.slime.utils.MapUtil;
import cc.minetale.slime.utils.MiscUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.Pos;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.map.tools.commands.MapCommand.MAP_ARG;
import static cc.minetale.slime.map.tools.commands.SpawnCommand.*;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super("create");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::createSpawnPoint, MAP_ARG);
        addSyntax(this::createSpawnPoint, MAP_ARG, POSITION_ARG);
        addSyntax(this::createSpawnPoint, MAP_ARG, POSITION_ARG, YAW_ARG, PITCH_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime spawn create <id> [position] [rotation]",
                MC.CC.GRAY.getTextColor())));
    }

    public void createSpawnPoint(CommandSender sender, CommandContext context) {
        var id = context.get(SPAWN_ARG);

        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        Pos pos = MiscUtil.getOptionalPosition(context, builder, POSITION_ARG, YAW_ARG, PITCH_ARG);

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", MC.CC.RED.getTextColor())));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        if(!MapUtil.isSpawnIdAvailable(handle, id)) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Spawn with this ID already exists.\n" +
                            "You can either remove it with \"/slime spawn remove\" or edit it with other commands.", MC.CC.RED.getTextColor())));
            return;
        }

        var spawn = new SpawnPoint(id, pos, null);
        handle.addSpawnPoint(spawn);

        sender.sendMessage(MC.Chat.notificationMessage("Map",
                Component.text("Successfully created a spawn with ID \"" + id + "\" for \"" + MapUtil.getFullId(handle) + "\"." +
                                "At " + MiscUtil.toString(pos), MC.CC.GREEN.getTextColor())
                        .append(Component.newline())
                        .append(Component.text(
                                "- This spawn doesn't have any owners and because of this is available for any team." +
                                        "You can change that with \"/slime spawn owner\" commands.\n" +
                                        "- It is currently not in the database and inaccessible by players," +
                                        "but you can change that with \"/slime map open\" after saving.",
                                MC.CC.YELLOW.getTextColor()))));
    }

}
