package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.Pos;

import static cc.minetale.slime.map.tools.commands.MapCommand.GAMEMODE_ARG;
import static cc.minetale.slime.map.tools.commands.MapCommand.SINGLE_ID_ARG;

public final class TeleportCommand extends Command {

    public TeleportCommand() {
        super("tp");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::teleportToMap, GAMEMODE_ARG, SINGLE_ID_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map tp <gamemode> <id>", MC.CC.GRAY.getTextColor())));
    }

    private void teleportToMap(CommandSender sender, CommandContext context) {
        String gamemode = context.get(GAMEMODE_ARG);
        String id = context.get(SINGLE_ID_ARG);

        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        var tempMap = Slime.TOOL_MANAGER.getMap(gamemode, id);
        if(tempMap.isEmpty()) {
            sender.sendMessage(MC.Chat.notificationMessage("Map",
                    Component.text("Couldn't find a map under this ID, make sure the map exists and is loaded.", MC.CC.RED.getTextColor())));
            return;
        }

        builder.setInstance(tempMap.get().getInstance(), Pos.ZERO);
    }

}
