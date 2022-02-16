package cc.minetale.slime.tools.commands.lobby;

import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.removeMap(AbstractMap.Type.LOBBY, sender, context));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Lobby",
                Component.text("Usage: /slime lobby remove", NamedTextColor.GRAY)));
    }

}
