package cc.minetale.slime.map.tools.commands.game;

import cc.minetale.slime.Slime;
import cc.minetale.slime.core.GameExtension;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class ListCommand extends Command {

    public ListCommand() {
        super("list");

        setDefaultExecutor(this::defaultExecutor);
    }

    //TODO Make a bit more clean
    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage("Available Games: ");
        for(GameExtension game : Slime.TOOL_MANAGER.getAvailableGames()) {
            sender.sendMessage(game.getId());
        }
    }

}
