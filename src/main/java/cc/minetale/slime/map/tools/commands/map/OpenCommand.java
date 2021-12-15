package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime map open", NamedTextColor.GRAY)));
    }

    private void openMap(CommandSender sender, CommandContext context) {
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
        var result = handle.setStatus(true);

        if(result.getModifiedCount() > 0) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Successfully opened \"" + MapUtil.getFullId(handle) + "\".", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Couldn't open \"" + MapUtil.getFullId(handle) + "\". Are you sure it's not open already?\n" +
                            "You can check with \"/slime map status\".", NamedTextColor.RED)));
        }
    }

}
