package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class InfoCommand extends Command {

    public InfoCommand() {
        super("info");

        setDefaultExecutor(this::defaultExecutor);
    }

    //TODO Make a bit more clean
    private void defaultExecutor(CommandSender sender, CommandContext context) {
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

        var playArea = handle.getPlayArea();
        sender.sendMessage(MC.Chat.notificationMessage("Map",
                Component.text("Displaying information for \"" + handle.getGamemode() + ":" + handle.getId() +"\":")
                    .append(Component.newline())
                    .append(Component.text("ID: ", MC.CC.GRAY.getTextColor())
                            .append(Component.text(handle.getId(), MC.CC.WHITE.getTextColor())))
                    .append(Component.newline())
                    .append(Component.text("Gamemode: ", MC.CC.GRAY.getTextColor())
                            .append(Component.text(handle.getGamemode(), MC.CC.WHITE.getTextColor())))
                    .append(Component.newline())
                    .append(Component.text("Name: ", MC.CC.GRAY.getTextColor())
                            .append(Component.text(handle.getName(), MC.CC.WHITE.getTextColor())))
                    .append(Component.newline())
                    .append(Component.text("IsOpen: ", MC.CC.GRAY.getTextColor())
                            .append(Component.text(handle.isOpen(), (handle.isOpen() ? MC.CC.GREEN : MC.CC.RED).getTextColor())))
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(Component.text("Dimension: ", MC.CC.GRAY.getTextColor())
                            .append(Component.text(handle.getDimensionID().asString(), MC.CC.WHITE.getTextColor())))
                    .append(Component.newline())
                    .append(Component.text("PlayArea: ", MC.CC.GRAY.getTextColor())
                            .append(Component.text(playArea.getLengthX() + "x" + playArea.getLengthY() + "x" + playArea.getLengthZ(), MC.CC.WHITE.getTextColor())))
                )
        );
    }

}
