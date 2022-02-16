package cc.minetale.slime.tools.commands.map;

import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.tools.commands.CommonCommands;
import cc.minetale.slime.tools.commands.MapCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super("create");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.createMap(AbstractMap.Type.GAME, sender, context),
                MapCommand.MAP_ARG, CommonCommands.NAME_ARG, CommonCommands.GAMEMODE_ARG, CommonCommands.DIMENSION_ARG);
        addSyntax((sender, context) -> CommonCommands.createMap(AbstractMap.Type.GAME, sender, context),
                MapCommand.MAP_ARG, CommonCommands.NAME_ARG, CommonCommands.GAMEMODE_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime map create <id> <name> <gamemodeId> [dimensionId]", NamedTextColor.GRAY)));
    }

}
