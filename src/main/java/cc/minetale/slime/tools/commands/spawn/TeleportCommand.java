package cc.minetale.slime.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.map.GameMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.tools.commands.SpawnCommand.SPAWN_AUTO_ARG;

public final class TeleportCommand extends Command {

    public TeleportCommand() {
        super("tp");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::teleportToSpawn, SPAWN_AUTO_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime spawn tp <spawnId>", NamedTextColor.GRAY)));
    }

    private void teleportToSpawn(CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Couldn't find a map under this ID, make sure the map exists and is loaded.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        if(!(map.getHandle() instanceof GameMap handle)) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong, the expected handle wasn't of GameMap.", NamedTextColor.RED)));
            return;
        }

        var id = context.get(SPAWN_AUTO_ARG);
        var spawn = handle.getSpawn(id);
        if(spawn == null) {
            sender.sendMessage(MC.notificationMessage("Map", Component.text("Spawn doesn't exist! " +
                    "Make sure you typed in the name correctly and the spawn exists.", NamedTextColor.RED)));
            return;
        }

        builder.teleport(spawn.getPosition())
                .thenAccept(v -> sender.sendMessage(MC.notificationMessage("Map",
                        Component.text("You've been teleported to spawn \"" + id + "\".", NamedTextColor.GREEN))));
    }

}
