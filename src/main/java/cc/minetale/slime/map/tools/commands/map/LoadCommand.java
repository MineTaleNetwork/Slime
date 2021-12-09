package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.tools.TempMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.map.tools.commands.MapCommand.*;

public final class LoadCommand extends Command {

    public LoadCommand() {
        super("load");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::loadMap, GAMEMODE_ARG, MAP_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map", Component.text("Usage: /slime map load <gamemode> <mapId>", NamedTextColor.GRAY)));
    }

    private void loadMap(CommandSender sender, CommandContext context) {
        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        if(!builder.isBuilderMode()) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("You need to be in builder mode to execute this command!",
                    NamedTextColor.RED)));
            return;
        }

        String gamemode = context.get(GAMEMODE_ARG);
        String id = context.get(MAP_AUTO_ARG);

        if(Slime.TOOL_MANAGER.mapExists(gamemode, id, true, false)) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("This map is already loaded! " +
                    "You can teleport to it with \"/slime map tp\".", NamedTextColor.RED)));
            return;
        }

        var oGame = Slime.TOOL_MANAGER.getGame(gamemode);
        if(oGame.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("Cannot find the gamemode! " +
                    "Make sure you typed in the name correctly and the gamemode is installed.", NamedTextColor.RED)));
            return;
        }
        var game = oGame.get();

        var map = GameMap.fromBoth(gamemode, id, game.getMapProvider());
        if(map == null) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("Map doesn't exist! " +
                    "Make sure you typed in the name correctly and the map exists.", NamedTextColor.RED)));
            return;
        }

        var tempMap = TempMap.ofMap(map, true);
        var result = Slime.TOOL_MANAGER.addMap(tempMap);

        if(result) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text(
                    "Successfully loaded \"" + gamemode + ":" + id + "\".", NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(MC.notificationMessage("Map", Component.text(
                    "There was a problem loading \"" + gamemode + ":" + id + "\".", NamedTextColor.RED)));
        }
    }

}
