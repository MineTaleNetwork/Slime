package cc.minetale.slime.map.tools.commands.lobby;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class UnloadCommand extends Command {

    public UnloadCommand() {
        super("unload");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.unloadMap(AbstractMap.Type.LOBBY, sender, context));
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Lobby",
                Component.text("Usage: /slime lobby unload", NamedTextColor.GRAY)));
    }

}
