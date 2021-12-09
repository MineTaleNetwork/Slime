package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.map.tools.TempMap;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.utils.NamespaceID;

import static cc.minetale.slime.map.tools.commands.MapCommand.*;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super("create");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::createMap, MAP_ARG, NAME_ARG, GAMEMODE_ARG, DIMENSION_ARG);
        addSyntax(this::createMap, MAP_ARG, NAME_ARG, GAMEMODE_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map", Component.text("Usage: /slime map create <id> <name> <gamemodeId> [dimensionId]",
                NamedTextColor.GRAY)));
    }

    public void createMap(CommandSender sender, CommandContext context) {
        var id = context.get(MAP_AUTO_ARG);
        var name = context.get(NAME_ARG);
        var gamemode = context.get(GAMEMODE_ARG);
        var dimension = NamespaceID.from(context.has(DIMENSION_ARG) ? context.get(DIMENSION_ARG) : "minecraft:overworld");

        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        var selection = builder.getSelection();

        if(selection == null || selection.isIncomplete()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("You don't have a complete selection!", NamedTextColor.RED)));
            return;
        }

        if(TOOL_MANAGER.mapExists(gamemode, id, true, true)) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Map with ID \"" + MapUtil.getFullId(gamemode, id) + "\" already exists.\n" +
                            "Remove it with \"/slime map remove\" or alternatively you can load an existing one using \"/slime map load\".", NamedTextColor.RED)));
            return;
        }

        var oGame = TOOL_MANAGER.getGame(gamemode);
        if(oGame.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("Cannot find the gamemode! " +
                    "Make sure you typed in the name correctly and the gamemode is installed.", NamedTextColor.RED)));
            return;
        }
        var game = oGame.get();

        var mapProvider = game.getMapProvider();
        var map = mapProvider.createMap(id, name, gamemode, dimension, selection);

        var tempMap = TempMap.ofMap(map, false);

        Slime.TOOL_MANAGER.addMap(tempMap);

        sender.sendMessage(MC.notificationMessage("Map", Component.text()
                .append(Component.text("Successfully created a map with ID \"" + MapUtil.getFullId(map) + "\".", NamedTextColor.GREEN),
                        Component.newline(),
                        Component.text("- Make sure to save your map with \"/slime map save\" when you're finished.\n" +
                                        "- It is currently not in the database and inaccessible by players, " +
                                        "but you can change that with \"/slime map open\" after saving.", NamedTextColor.YELLOW))
                .build()));
    }

}
