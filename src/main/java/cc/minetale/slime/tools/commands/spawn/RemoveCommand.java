package cc.minetale.slime.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.tools.commands.SpawnCommand.SPAWN_AUTO_ARG;

public final class RemoveCommand extends Command {

    public RemoveCommand() {
        super("remove");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::removeSpawn, SPAWN_AUTO_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime spawn remove <id>", NamedTextColor.GRAY)));
    }

    public void removeSpawn(CommandSender sender, CommandContext context) {
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

        if(!(map.getHandle() instanceof GameMap handle)) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong, the expected handle wasn't of GameMap.", NamedTextColor.RED)));
            return;
        }

        var id = context.get(SPAWN_AUTO_ARG);

        if(MapUtil.isSpawnIdAvailable(handle, id)) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("Spawn doesn't exist! " +
                    "Make sure you typed in the name correctly and the spawn exists.", NamedTextColor.RED)));
            return;
        }

        if(handle.removeSpawn(id) != null) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text(
                    "Successfully removed spawn \"" + id + "\" from " + "\"" + MapUtil.getFullId(map) + "\".",
                    NamedTextColor.GREEN)));
        } else {
            sender.sendMessage(MC.notificationMessage("Map", Component.text(
                    "There was a problem removing spawn \"" + id + "\" from " + "\"" + MapUtil.getFullId(map) + "\".",
                    NamedTextColor.RED)));
        }
    }

}
