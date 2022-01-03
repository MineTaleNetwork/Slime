package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.map.tools.commands.CommonCommands.*;
import static cc.minetale.slime.map.tools.commands.MapCommand.MAP_ARG;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super("create");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.createMap(AbstractMap.Type.GAME, sender, context),
                MAP_ARG, NAME_ARG, GAMEMODE_ARG, DIMENSION_ARG);
        addSyntax((sender, context) -> CommonCommands.createMap(AbstractMap.Type.GAME, sender, context),
                MAP_ARG, NAME_ARG, GAMEMODE_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime map create <id> <name> <gamemodeId> [dimensionId]", NamedTextColor.GRAY)));
    }

}
