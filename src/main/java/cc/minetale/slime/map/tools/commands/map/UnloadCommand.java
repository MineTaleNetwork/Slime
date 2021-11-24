package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class UnloadCommand extends Command {

    public UnloadCommand() {
        super("unload");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::unloadMap);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map unload", MC.CC.GRAY.getTextColor())));
    }

    private void unloadMap(CommandSender sender, CommandContext context) {
        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) {
            return;
        }

        //TODO Get the map from instance, inform if it isn't loaded and warn if there are any unsaved changes

    }

}
