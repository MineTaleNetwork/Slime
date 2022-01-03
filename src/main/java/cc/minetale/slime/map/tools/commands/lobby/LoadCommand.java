package cc.minetale.slime.map.tools.commands.lobby;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.map.tools.commands.CommonCommands.GAMEMODE_ARG;
import static cc.minetale.slime.map.tools.commands.LobbyCommand.LOBBY_ARG;

public final class LoadCommand extends Command {

    public LoadCommand() {
        super("load");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.loadMap(AbstractMap.Type.LOBBY, sender, context),
                GAMEMODE_ARG, LOBBY_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Lobby",
                Component.text("Usage: /slime lobby load <gamemode> <mapId>", NamedTextColor.GRAY)));
    }

}
