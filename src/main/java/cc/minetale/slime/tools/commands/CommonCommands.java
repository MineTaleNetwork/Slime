package cc.minetale.slime.tools.commands;

import cc.minetale.buildingtools.Builder;
import cc.minetale.buildingtools.Selection;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.map.*;
import cc.minetale.slime.tools.TempMap;
import cc.minetale.slime.utils.MapUtil;
import cc.minetale.slime.utils.Requirement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.util.TriState;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentBoolean;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.utils.NamespaceID;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.tools.commands.LobbyCommand.LOBBY_AUTO_ARG;
import static cc.minetale.slime.tools.commands.MapCommand.MAP_AUTO_ARG;

public class CommonCommands {

    public static final ArgumentString NAME_ARG = new ArgumentString("name");

    public static final ArgumentWord GAMEMODE_ARG = (ArgumentWord) new ArgumentWord("gamemode")
            .setSuggestionCallback((CommandSender sender, CommandContext context, Suggestion suggestion) -> {
                for(GameExtension game : TOOL_MANAGER.getAvailableGames()) {
                    var id = game.getId();
                    suggestion.addEntry(new SuggestionEntry(id, Component.text(id)));
                }
            });

    public static final ArgumentWord DIMENSION_ARG = new ArgumentWord("dimension")
            .from("minecraft:overworld", "minecraft:nether", "minecraft:the_end");

    public static final ArgumentBoolean SAVE_SETTINGS_ARG = new ArgumentBoolean("saveSettings");
    public static final ArgumentBoolean SAVE_BLOCKS_ARG = new ArgumentBoolean("saveBlocks");

    public static void closeMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(type, instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Something went wrong when looking up the " + type.getLowercase() + " you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();
        var result = handle.setStatus(true);

        if(result.getModifiedCount() > 0) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Successfully closed \"" + MapUtil.getFullId(handle) + "\".", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Couldn't open \"" + MapUtil.getFullId(handle) + "\". Are you sure it's not closed already?\n" +
                            "You can check with \"/slime map status\".", NamedTextColor.RED)));
        }
    }

    public static void createMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var selection = builder.getSelection();

        if(selection == null || selection.isIncomplete()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("You don't have a complete selection!", NamedTextColor.RED)));
            return;
        }

        var name = context.get(NAME_ARG);
        var gamemode = context.get(GAMEMODE_ARG);
        var dimension = NamespaceID.from(context.has(DIMENSION_ARG) ? context.get(DIMENSION_ARG) : "minecraft:overworld");

        String id;
        if(type == AbstractMap.Type.GAME) {
            id = context.get(MAP_AUTO_ARG);
        } else if(type == AbstractMap.Type.LOBBY) {
            id = context.get(LOBBY_AUTO_ARG);
        } else {
            return;
        }

        if(TOOL_MANAGER.mapExists(type, gamemode, id, true, true)) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text(type.getPascalcase() + " with ID \"" + MapUtil.getFullId(gamemode, id) + "\" already exists.\n" +
                            "Remove it with \"/slime " + type.getLowercase() + " remove\" or alternatively you can load an existing one using" +
                            "\"/slime " + type.getLowercase() + " load\".", NamedTextColor.RED)));
            return;
        }

        var oGame = TOOL_MANAGER.getGame(gamemode);
        if(oGame.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Cannot find the gamemode!" +
                            "Make sure you typed in the name correctly and the gamemode is installed.", NamedTextColor.RED)));
            return;
        }
        var game = oGame.get();

        selection = new Selection(selection);

        MapProvider<?> mapProvider = type.getProvider(game);
        var map = mapProvider.createMap(id, name, gamemode, dimension, selection);

        var tempMap = TempMap.ofMap(map, false);

        TOOL_MANAGER.addMap(tempMap);

        sender.sendMessage(MC.notificationMessage(type.getPascalcase(), Component.text()
                .append(Component.text("Successfully created a " + type.getLowercase() +
                                " with ID \"" + MapUtil.getFullId(map) + "\".", NamedTextColor.GREEN),
                        Component.newline(),
                        Component.text("- Make sure to save your " + type.getLowercase() +
                                " with \"/slime " + type.getLowercase() + " save\" when you're finished.\n" +
                                "- It is currently not in the database and inaccessible by players, " +
                                "but you can change that with \"/slime " + type.getLowercase() + " open\" after saving.", NamedTextColor.YELLOW))
                .build()));
    }

    public static void loadMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        if(!builder.isBuilderMode()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("You need to be in builder mode to execute this command!", NamedTextColor.RED)));
            return;
        }

        var gamemode = context.get(GAMEMODE_ARG);

        String id;
        if(type == AbstractMap.Type.GAME) {
            id = context.get(MAP_AUTO_ARG);
        } else if(type == AbstractMap.Type.LOBBY) {
            id = context.get(LOBBY_AUTO_ARG);
        } else {
            return;
        }

        if(TOOL_MANAGER.mapExists(type, gamemode, id, true, false)) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("This " + type.getLowercase() + " is already loaded! " +
                            "You can teleport to it with \"/slime " + type.getLowercase() + " tp\".", NamedTextColor.RED)));
            return;
        }

        var oGame = TOOL_MANAGER.getGame(gamemode);
        if(oGame.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Cannot find the gamemode! " +
                            "Make sure you typed in the name correctly and the gamemode is installed.", NamedTextColor.RED)));
            return;
        }
        var game = oGame.get();

        MapResolver<AbstractMap> resolver = type.getResolver(game);
        MapProvider<AbstractMap> provider = type.getProvider(game);
        AbstractMap map = resolver.fromBoth(gamemode, id, provider);

        if(map == null) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text(type.getPascalcase() + " doesn't exist!" +
                            "Make sure you typed in the name correctly and the " + type.getLowercase() + " exists.", NamedTextColor.RED)));
            return;
        }

        var tempMap = TempMap.ofMap(map, true);
        var result = TOOL_MANAGER.addMap(tempMap);

        if(result) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Successfully loaded \"" + MapUtil.getFullId(tempMap) + "\".", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("There was a problem loading \"" + MapUtil.getFullId(tempMap) + "\".", NamedTextColor.RED)));
        }
    }

    public static void modifyMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(type, instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Something went wrong when looking up the " +
                            type.getLowercase() + " you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        if(context.has(NAME_ARG)) {
            var newName = context.get(NAME_ARG);
            handle.setName(newName);
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(), Component.text().append(
                            Component.text("Successfully changed the name of \"" + MapUtil.getFullId(handle) +
                                    "\" to \"" + newName + "\".", NamedTextColor.GREEN),
                            Component.newline(),
                            Component.text("Make sure to save changes with \"/slime " + type.getLowercase() + " save\".", NamedTextColor.YELLOW))
                    .build())
            );
        } else if(context.has(DIMENSION_ARG)) {
            var newDimension = context.get(DIMENSION_ARG);
            handle.setDimension(NamespaceID.from(newDimension));
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(), Component.text()
                    .append(Component.text("Successfully changed the dimension of \"" + MapUtil.getFullId(handle) +
                                    "\" to \"" + newDimension + "\".", NamedTextColor.GREEN),
                            Component.newline(),
                            Component.text("Make sure to save changes \"/slime " + type.getLowercase() + " save\".", NamedTextColor.YELLOW))
                    .build()));
        }

        //TODO Editable bounds
    }

    public static void openMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(type, instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Something went wrong when looking up the " + type.getLowercase() + " you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();
        var result = handle.setStatus(true);

        if(result.getModifiedCount() > 0) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Successfully opened \"" + MapUtil.getFullId(handle) + "\".", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Couldn't open \"" + MapUtil.getFullId(handle) + "\". Are you sure it's not open already?\n" +
                            "You can check with \"/slime " + type.getLowercase() + " info\".", NamedTextColor.RED)));
        }
    }

    public static void removeMap(AbstractMap.Type game, CommandSender sender, CommandContext context) {
        //TODO
    }

    //TODO Command shows results as failure for both when saving for the first time and always failure for block saving
    public static void saveMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var saveSettings = context.get(SAVE_SETTINGS_ARG);
        var saveBlocks = context.get(SAVE_BLOCKS_ARG);

        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(type, instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Something went wrong when looking up the " + type.getLowercase() + " you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var settingsFuture = CompletableFuture.completedFuture(TriState.NOT_SET);
        if(saveSettings) {
            //Only game maps have requirements (unless needed at some point for some reason)
            if(type == AbstractMap.Type.GAME) {
                List<Requirement<TempMap>> failedRequirements = map.getUnsatisfiedRequirements();
                if(!failedRequirements.isEmpty()) {
                    var errorMsg = Component.text()
                            .append(Component.text("Unable to save settings due to the following requirements not being met:"));

                    for(Requirement<TempMap> requirement : failedRequirements) {
                        errorMsg.append(
                                Component.newline(),
                                Component.text("-"), Component.space(),
                                Component.text(requirement.getName(), NamedTextColor.RED, TextDecoration.BOLD), Component.newline(),
                                Component.text(requirement.getDescription(), NamedTextColor.RED));
                    }
                    builder.sendMessage(errorMsg);

                    settingsFuture = CompletableFuture.completedFuture(TriState.FALSE);
                }
            } else if(type == AbstractMap.Type.LOBBY) {
                settingsFuture = CompletableFuture.completedFuture(TriState.TRUE);
            }

            if(settingsFuture.getNow(TriState.FALSE) != TriState.FALSE) {
                var result = map.saveSettings();
                settingsFuture = CompletableFuture.completedFuture(TriState.byBoolean(result.getMatchedCount() > 0));
            }
        }

        var blocksFuture = CompletableFuture.completedFuture(TriState.NOT_SET);
        if(saveBlocks) {
            blocksFuture = map.saveBlocks()
                    .thenCompose(result -> CompletableFuture.completedFuture(TriState.byBoolean(result)))
                    .completeOnTimeout(TriState.FALSE, 10000, TimeUnit.SECONDS)
                    .exceptionally(throwable -> TriState.FALSE);
        }

        settingsFuture.thenAcceptBothAsync(blocksFuture, (settingsResult, blocksResult) -> {
            var settingsIncluded = settingsResult != TriState.NOT_SET;
            var settingsSuccess = settingsResult == TriState.TRUE;

            var blocksIncluded = blocksResult != TriState.NOT_SET;
            var blocksSuccess = blocksResult == TriState.TRUE;
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(), Component.text()
                    .append(Component.text("You've saved " + type.getLowercase() + " " + MapUtil.getFullId(map) + "...",
                                    NamedTextColor.YELLOW),
                            Component.newline(),
                            Component.text("Settings: ", NamedTextColor.GRAY),
                            Component.text(settingsIncluded ? (settingsSuccess ? "Success" : "Failed") : "Excluded",
                                    settingsIncluded ? (settingsSuccess ? NamedTextColor.GREEN : NamedTextColor.RED) : NamedTextColor.DARK_GRAY),
                            Component.newline(),
                            Component.text("Blocks: ", NamedTextColor.GRAY),
                            Component.text(blocksIncluded ? (blocksSuccess ? "Success" : "Failed") : "Excluded",
                                    blocksIncluded ? (blocksSuccess ? NamedTextColor.GREEN : NamedTextColor.RED) : NamedTextColor.DARK_GRAY)
                    ).build()));
        });
    }

    public static void teleportToMap(AbstractMap.Type type, CommandSender sender, CommandContext context) {
        var gamemode = context.get(GAMEMODE_ARG);
        var id = context.get(MAP_AUTO_ARG);

        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        Optional<TempMap> oTempMap = TOOL_MANAGER.getMap(type, gamemode, id);
        if(oTempMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                    Component.text("Couldn't find a " + type.getLowercase() +
                            " under this ID, make sure the " + type.getLowercase() + " exists and is loaded.", NamedTextColor.RED)));
            return;
        }
        var tempMap = oTempMap.get();

        builder.setInstance(tempMap.getInstance(), Pos.ZERO)
                .thenAccept(v -> {
                    var fullId = MapUtil.getFullId(tempMap);

                    var sidebar = builder.getSidebar();
                    sidebar.updateLineContent("4", Component.text()
                            .append(Component.text("Map: ", NamedTextColor.GOLD, TextDecoration.BOLD),
                                    Component.text(fullId, NamedTextColor.GRAY))
                            .build());
                    sidebar.updateLineContent("3", Component.text()
                            .append(Component.text("Type: ", NamedTextColor.GOLD, TextDecoration.BOLD),
                                    Component.text(type.getPascalcase(), NamedTextColor.GRAY))
                            .build());

                    sender.sendMessage(MC.notificationMessage(type.getPascalcase(),
                            Component.text("You've been teleported to \"" + fullId + "\".", NamedTextColor.GREEN)));
                });
    }

    public static void unloadMap(AbstractMap.Type lobby, CommandSender sender, CommandContext context) {
        //TODO
    }

}
