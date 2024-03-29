package cc.minetale.slime.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.spawn.MapSpawn;
import cc.minetale.slime.utils.MapUtil;
import cc.minetale.slime.utils.MiscUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.coordinate.Pos;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.tools.commands.SpawnCommand.*;

public final class CreateCommand extends Command {

    public CreateCommand() {
        super("create");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax(this::createSpawn, SPAWN_ARG);
        addSyntax(this::createSpawn, SPAWN_ARG, POSITION_ARG);
        addSyntax(this::createSpawn, SPAWN_ARG, POSITION_ARG, YAW_ARG, PITCH_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime spawn create <id> [position] [rotation]", NamedTextColor.GRAY)));
    }

    public void createSpawn(CommandSender sender, CommandContext context) {
        var id = context.get(SPAWN_ARG);

        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        Pos pos = MiscUtil.getOptionalPosition(context, builder, POSITION_ARG, YAW_ARG, PITCH_ARG);

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

        if(!MapUtil.isSpawnIdAvailable(handle, id)) {
            sender.sendMessage(Message.notification("Map",
                    Component.text("Spawn with this ID already exists.\n" +
                            "You can either remove it with \"/slime spawn remove\" or edit it with other commands.", NamedTextColor.RED)));
            return;
        }

        var spawn = new MapSpawn(id, pos);
        handle.addSpawn(spawn);

        sender.sendMessage(Message.notification("Map",
                Component.text("Successfully created a spawn with ID \"" + id + "\" for \"" + MapUtil.getFullId(handle) + "\". " +
                                "At " + MiscUtil.toString(pos), NamedTextColor.GREEN)
                        .append(Component.newline())
                        .append(Component.text(
                                "- This spawn doesn't have any owners and because of this is available for any team. " +
                                        "You can change that with \"/slime spawn owner\" commands.\n" +
                                        "- It is currently not in the database and inaccessible by players, " +
                                        "but you can change that with \"/slime map open\" after saving.",
                                NamedTextColor.YELLOW))));
    }

}
