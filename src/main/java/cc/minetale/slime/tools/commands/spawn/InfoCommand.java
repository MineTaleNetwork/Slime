package cc.minetale.slime.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.tools.commands.SpawnCommand;
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

        addSyntax(this::spawnInfo, SpawnCommand.SPAWN_AUTO_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime spawn info <spawnId>", NamedTextColor.GRAY)));
    }

    private void spawnInfo(CommandSender sender, CommandContext context) {
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

        var id = context.get(SpawnCommand.SPAWN_AUTO_ARG);

        var spawn = handle.getSpawn(id);
        if(spawn == null) {
            sender.sendMessage(Message.notification("Map", Component.text("Spawn doesn't exist! " +
                    "Make sure you typed in the name correctly and the spawn exists.", NamedTextColor.RED)));
            return;
        }

        sender.sendMessage(Message.notification("Map", Component.text()
                .append(MiscUtil.getInformationMessage(
                        "Displaying information for \"" + id + "\":",
                        Map.ofEntries(
                                Map.entry("ID", Component.text(id)),
                                Map.entry("Parent", Component.text(MapUtil.getFullId(map))),
                                Map.entry("Position", Component.text(spawn.getPosition().toString())),
                                Map.entry("Owners", Component.text(spawn.getOwners().size()))
                        )))
                .build()));
    }

}
