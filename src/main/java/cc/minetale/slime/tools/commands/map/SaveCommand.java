package cc.minetale.slime.tools.commands.map;

import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.tools.commands.CommonCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentBoolean;

public final class SaveCommand extends Command {

    private static final ArgumentBoolean SAVE_SETTINGS_ARG = new ArgumentBoolean("saveSettings");
    private static final ArgumentBoolean SAVE_BLOCKS_ARG = new ArgumentBoolean("saveBlocks");

    public SaveCommand() {
        super("save");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> CommonCommands.saveMap(AbstractMap.Type.GAME, sender, context),
                SAVE_SETTINGS_ARG, SAVE_BLOCKS_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime map save <saveSettings> <saveBlocks>", NamedTextColor.GRAY)));
    }

}
