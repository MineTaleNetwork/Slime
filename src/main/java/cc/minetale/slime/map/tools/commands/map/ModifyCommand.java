package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;

import static cc.minetale.slime.map.tools.commands.MapCommand.*;

public final class ModifyCommand extends Command {

    private static final ArgumentWord NEW_ID_ARG = new ArgumentWord("newId");

    public ModifyCommand() {
        super("modify");

        setDefaultExecutor(this::defaultExecutor);

        var subcmd = new Command("id");
        subcmd.addSyntax(this::modifyMap, SINGLE_ID_ARG, NEW_ID_ARG);
        addSubcommand(subcmd);

        subcmd = new Command("name");
        subcmd.addSyntax(this::modifyMap, SINGLE_ID_ARG, NAME_ARG);
        addSubcommand(subcmd);

        subcmd = new Command("gamemode");
        subcmd.addSyntax(this::modifyMap, SINGLE_ID_ARG, GAMEMODE_ARG);
        addSubcommand(subcmd);

        subcmd = new Command("dimension");
        subcmd.addSyntax(this::modifyMap, SINGLE_ID_ARG, DIMENSION_ARG);
        addSubcommand(subcmd);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map modify <id|name|gamemode|dimension|bounds>", MC.CC.GRAY.getTextColor())));
    }

    public void modifyMap(CommandSender sender, CommandContext context) {
        String id = context.get(SINGLE_ID_ARG);



        if(context.has(NEW_ID_ARG)) {

        }
    }

}
