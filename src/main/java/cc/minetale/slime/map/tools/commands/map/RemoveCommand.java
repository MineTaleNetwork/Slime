package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::removeMap);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map", Component.text("Usage: /slime map remove", NamedTextColor.GRAY)));
    }

    public void removeMap(CommandSender sender, CommandContext context) {
        //TODO Yes
    }

}
