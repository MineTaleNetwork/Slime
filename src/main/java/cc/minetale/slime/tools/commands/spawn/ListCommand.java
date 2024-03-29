package cc.minetale.slime.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class ListCommand extends Command {

    public ListCommand() {
        super("list");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::listSpawns);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime spawn list", NamedTextColor.GRAY)));
    }

    public void listSpawns(CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var map = TOOL_MANAGER.getMapByInstance(instance);
        if(map == null) {
            sender.sendMessage(Message.notification("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
            return;
        }

        if(!(map.getHandle() instanceof GameMap handle)) {
            sender.sendMessage(Message.notification("Map",
                    Component.text("Something went wrong, the expected handle wasn't of GameMap.", NamedTextColor.RED)));
            return;
        }

        sender.sendMessage("Spawns for \"" + MapUtil.getFullId(handle) + "\": ");
        for(String spawnId : handle.getSpawns().keySet()) {
            sender.sendMessage(spawnId);
        }
    }

}
