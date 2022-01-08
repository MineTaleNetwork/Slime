package cc.minetale.slime.tools.commands.map;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.utils.MapUtil;
import cc.minetale.slime.utils.MiscUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import java.util.Map;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class InfoCommand extends Command {

    public InfoCommand() {
        super("info");

        setDefaultExecutor(this::defaultExecutor);
    }

    //TODO Make a bit more clean
    private void defaultExecutor(CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(AbstractMap.Type.GAME, instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        var area = map.getArea();
        //TODO Update and include more info such as spawns...
        sender.sendMessage(MC.notificationMessage("Map", Component.text()
                .append(MiscUtil.getInformationMessage(
                        "Displaying information for \"" + MapUtil.getFullId(handle) + "\":",
                        Map.ofEntries(
                                Map.entry("ID", Component.text(handle.getId())),
                                Map.entry("Gamemode", Component.text(handle.getGamemode())),
                                Map.entry("Name", Component.text(handle.getName())),
                                Map.entry("IsOpen", Component.text(handle.isOpen(), handle.isOpen() ? NamedTextColor.GREEN : NamedTextColor.RED)),
                                Map.entry("Dimension", Component.text(handle.getDimensionID().asString())),
                                Map.entry("Area", Component.text(area.getLengthX() + "x" + area.getLengthY() + "x" + area.getLengthZ()))
                        )))
                .build()));
    }

}
