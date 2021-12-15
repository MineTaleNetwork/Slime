package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

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

        var oMap = TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        var playArea = handle.getPlayArea();
        //TODO Update and include more info such as spawns...
        sender.sendMessage(MC.notificationMessage("Map", Component.text()
                .append(Component.text("Displaying information for \"" + MapUtil.getFullId(handle) + "\":"),
                        Component.newline(),
                        Component.text("ID: ", NamedTextColor.GRAY)
                                .append(Component.text(handle.getId(), NamedTextColor.WHITE)),
                        Component.newline(),
                        Component.text("Gamemode: ", NamedTextColor.GRAY)
                                .append(Component.text(handle.getGamemode(), NamedTextColor.WHITE)),
                        Component.newline(),
                        Component.text("Name: ", NamedTextColor.GRAY)
                                .append(Component.text(handle.getName(), NamedTextColor.WHITE)),
                        Component.newline(),
                        Component.text("IsOpen: ", NamedTextColor.GRAY)
                                .append(Component.text(handle.isOpen(), handle.isOpen() ? NamedTextColor.GREEN : NamedTextColor.RED)),
                        Component.newline(),
                        Component.newline(),
                        Component.text("Dimension: ", NamedTextColor.GRAY)
                                .append(Component.text(handle.getDimensionID().asString(), NamedTextColor.WHITE)),
                        Component.newline(),
                        Component.text("PlayArea: ", NamedTextColor.GRAY)
                                .append(Component.text(playArea.getLengthX() + "x" + playArea.getLengthY() + "x" + playArea.getLengthZ(),
                                        NamedTextColor.WHITE)))
                .build()));
    }

}
