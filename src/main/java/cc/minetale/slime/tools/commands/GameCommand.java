package cc.minetale.slime.tools.commands;

import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.tools.commands.game.ListCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class GameCommand extends Command {

    public GameCommand() {
        super("game");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new ListCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime game <list>", NamedTextColor.GRAY)));
    }

}