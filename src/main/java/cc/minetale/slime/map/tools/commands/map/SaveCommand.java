package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import net.kyori.adventure.text.Component;
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

        addSyntax(this::saveMap, SAVE_SETTINGS_ARG, SAVE_BLOCKS_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map save <saveSettings> <saveBlocks>", MC.CC.GRAY.getTextColor())));
    }

    private void saveMap(CommandSender sender, CommandContext context) {
        boolean saveSettings = context.get(SAVE_SETTINGS_ARG);
        boolean saveBlocks = context.get(SAVE_BLOCKS_ARG);

        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = Slime.TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", MC.CC.RED.getTextColor())));
            return;
        }
        var map = oMap.get();

        if(saveSettings)
            map.saveSettings();

        if(saveBlocks)
            map.saveBlocks();
    }

}
