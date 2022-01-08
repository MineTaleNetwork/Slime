package cc.minetale.slime.tools.commands.map;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class ModifyCommand extends Command {

    public ModifyCommand() {
        super("modify");

        setDefaultExecutor(this::defaultExecutor);

        var subcmd = new Command("name");
        subcmd.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime map modify name <newName>", NamedTextColor.GRAY)));
        });
        subcmd.addSyntax((sender, context) -> CommonCommands.modifyMap(AbstractMap.Type.GAME, sender, context),
                CommonCommands.NAME_ARG);
        addSubcommand(subcmd);

        subcmd = new Command("dimension");
        subcmd.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime map modify dimension <newDimensionId>", NamedTextColor.GRAY)));
        });
        subcmd.addSyntax((sender, context) -> CommonCommands.modifyMap(AbstractMap.Type.GAME, sender, context),
                CommonCommands.DIMENSION_ARG);
        addSubcommand(subcmd);

        //TODO modify bounds
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime map modify <name|dimension|bounds>", NamedTextColor.GRAY)));
    }

}
