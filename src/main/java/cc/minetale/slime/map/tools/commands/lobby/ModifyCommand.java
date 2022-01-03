package cc.minetale.slime.map.tools.commands.lobby;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.map.tools.commands.CommonCommands.DIMENSION_ARG;

public final class ModifyCommand extends Command {

    public ModifyCommand() {
        super("modify");

        setDefaultExecutor(this::defaultExecutor);

        var subcmd = new Command("dimension");
        subcmd.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MC.notificationMessage("Lobby",
                    Component.text("Usage: /slime lobby modify dimension <newDimensionId>", NamedTextColor.GRAY)));
        });
        subcmd.addSyntax((sender, context) -> CommonCommands.modifyMap(AbstractMap.Type.LOBBY, sender, context),
                DIMENSION_ARG);
        addSubcommand(subcmd);

        //TODO modify bounds
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Lobby",
                Component.text("Usage: /slime lobby modify <dimension|bounds>", NamedTextColor.GRAY)));
    }

}
