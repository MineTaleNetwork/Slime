package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.TriState;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentBoolean;

import java.util.concurrent.CompletableFuture;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class SaveCommand extends Command {

    private static final ArgumentBoolean SAVE_SETTINGS_ARG = new ArgumentBoolean("saveSettings");
    private static final ArgumentBoolean SAVE_BLOCKS_ARG = new ArgumentBoolean("saveBlocks");

    public SaveCommand() {
        super("save");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::saveMap, SAVE_SETTINGS_ARG, SAVE_BLOCKS_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map", Component.text("Usage: /slime map save <saveSettings> <saveBlocks>", NamedTextColor.GRAY)));
    }

    private void saveMap(CommandSender sender, CommandContext context) {
        boolean saveSettings = context.get(SAVE_SETTINGS_ARG);
        boolean saveBlocks = context.get(SAVE_BLOCKS_ARG);

        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var settingsFuture = CompletableFuture.completedFuture(TriState.NOT_SET);
        if(saveSettings) {
            var result = map.saveSettings();
            settingsFuture = CompletableFuture.completedFuture(TriState.byBoolean(result.getMatchedCount() > 0));
        }

        var blocksFuture = CompletableFuture.completedFuture(TriState.NOT_SET);
        if(saveBlocks) {
            blocksFuture = map.saveBlocks()
                    .thenCompose(result -> CompletableFuture.completedFuture(TriState.byBoolean(result)));
        }

        settingsFuture.thenAcceptBothAsync(blocksFuture, (settingsResult, blocksResult) -> {
            var settingsIncluded = settingsResult != TriState.NOT_SET;
            var settingsSuccess = settingsResult == TriState.TRUE;

            var blocksIncluded = blocksResult != TriState.NOT_SET;
            var blocksSuccess = blocksResult == TriState.TRUE;
            sender.sendMessage(MC.notificationMessage("Map", Component.text()
                            .append(Component.text("You've saved map " + map.getGame().getId() + ":" + map.getHandle().getId() + "...",
                                            NamedTextColor.YELLOW),
                                    Component.newline(),
                                    Component.text("Settings: ", NamedTextColor.GRAY),
                                    Component.text(settingsIncluded ? (settingsSuccess ? "Success" : "Failed") : "Not included",
                                            settingsIncluded ? (settingsSuccess ? NamedTextColor.GREEN : NamedTextColor.RED) : NamedTextColor.DARK_GRAY),
                                    Component.newline(),
                                    Component.text("Blocks: ", NamedTextColor.GRAY),
                                    Component.text(blocksIncluded ? (blocksSuccess ? "Success" : "Failed") : "Not included",
                                            blocksIncluded ? (blocksSuccess ? NamedTextColor.GREEN : NamedTextColor.RED) : NamedTextColor.DARK_GRAY)
                            ).build()));
        });
    }

}
