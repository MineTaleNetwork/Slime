package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class OpenCommand extends Command {

    public OpenCommand() {
        super("open");

//        setCondition(CommandUtil.getRankCondition("Admin"));

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::openMap);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map open", MC.CC.GRAY.getTextColor())));
    }

    private void openMap(CommandSender sender, CommandContext context) {
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
        var result = handle.setStatus(true);

        if(result.getModifiedCount() > 0) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Successfully opened \"" + handle.getGamemode() + ":" + handle.getId() + "\".", MC.CC.GREEN.getTextColor())));
        } else {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Couldn't open \"" + handle.getGamemode() + ":" + handle.getId() + "\". Are you sure it's not open already?\n" +
                            "You can check with \"/slime map status\".", MC.CC.RED.getTextColor())));
        }
    }

}
