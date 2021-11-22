package cc.minetale.slime.map.tools.commands;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.map.tools.ToolManager;
import cc.minetale.slime.map.tools.commands.game.ListCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;

public final class GameCommand extends Command {

    public static final ToolManager TOOL_MANAGER = Slime.TOOL_MANAGER;

    //Arguments
    public static final ArgumentWord SINGLE_ID_ARG = new ArgumentWord("id");

    public GameCommand() {
        super("game");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new ListCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime game <list>", MC.CC.GRAY.getTextColor())));
    }

}