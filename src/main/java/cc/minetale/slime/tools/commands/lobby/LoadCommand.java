package cc.minetale.slime.tools.commands.lobby;

import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.tools.commands.CommonCommands;
import cc.minetale.slime.tools.commands.LobbyCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class LoadCommand extends Command {

    public LoadCommand() {
        super("load");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.loadMap(AbstractMap.Type.LOBBY, sender, context),
                CommonCommands.GAMEMODE_ARG, LobbyCommand.LOBBY_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Lobby",
                Component.text("Usage: /slime lobby load <gamemode> <mapId>", NamedTextColor.GRAY)));
    }

}
