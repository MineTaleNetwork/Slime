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
import static cc.minetale.slime.map.tools.commands.LobbyCommand.LOBBY_AUTO_ARG;

public final class TeleportCommand extends Command {

    public TeleportCommand() {
        super("tp");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.teleportToMap(AbstractMap.Type.LOBBY, sender, context),
                GAMEMODE_ARG, LOBBY_AUTO_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Lobby",
                Component.text("Usage: /slime lobby tp <gamemode> <id>", NamedTextColor.GRAY)));
    }

}
