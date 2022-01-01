package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.Pos;

import static cc.minetale.slime.map.tools.commands.MapCommand.GAMEMODE_ARG;
import static cc.minetale.slime.map.tools.commands.MapCommand.MAP_AUTO_ARG;

public final class TeleportCommand extends Command {

    public TeleportCommand() {
        super("tp");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::teleportToMap, GAMEMODE_ARG, MAP_AUTO_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime map tp <gamemode> <id>", NamedTextColor.GRAY)));
    }

    private void teleportToMap(CommandSender sender, CommandContext context) {
        var gamemode = context.get(GAMEMODE_ARG);
        var id = context.get(MAP_AUTO_ARG);

        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var oTempMap = Slime.TOOL_MANAGER.getMap(gamemode, id);
        if(oTempMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Couldn't find a map under this ID, make sure the map exists and is loaded.", NamedTextColor.RED)));
            return;
        }
        var tempMap = oTempMap.get();

        builder.setInstance(tempMap.getInstance(), Pos.ZERO)
                .thenAccept(v -> {
                    var fullId = MapUtil.getFullId(tempMap);

                    var sidebar = builder.getSidebar();
                    sidebar.updateLineContent("3", Component.text()
                            .append(Component.text("Map: ", NamedTextColor.GOLD, TextDecoration.BOLD),
                                    Component.text(fullId, NamedTextColor.GRAY))
                            .build());

                    sender.sendMessage(MC.notificationMessage("Map",
                            Component.text("You've been teleported to \"" + fullId + "\".", NamedTextColor.GREEN)));
                });
    }

}
