package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.flame.util.CommandUtil;
import cc.minetale.slime.map.tools.commands.MapCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class CloseCommand extends Command {

    public CloseCommand() {
        super("close");

        setCondition(CommandUtil.getRankCondition("Admin"));

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::closeMap);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map close", MC.CC.GRAY.getTextColor())));
    }

    private void closeMap(CommandSender sender, CommandContext context) {
        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) {
            return;
        }

        //TODO Close
    }

}
