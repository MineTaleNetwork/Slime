package cc.minetale.slime.tools.commands;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.tools.commands.spawn.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import java.util.Set;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public class SpawnCommand extends Command {

    public static final ArgumentWord SPAWN_ARG = new ArgumentWord("spawnId");
    public static final ArgumentWord SPAWN_AUTO_ARG = (ArgumentWord) new ArgumentWord("spawnId")
            .setSuggestionCallback((sender, context, suggestion) -> {
                var builder = Builder.fromSender(sender);
                if(builder == null) { return; }

                var instance = builder.getInstance();

                var oMap = TOOL_MANAGER.getMapByInstance(instance);
                if(oMap.isEmpty()) { return; }
                var map = oMap.get();

                if(!(map.getHandle() instanceof GameMap handle)) { return; }

                for(String spawnId : handle.getSpawns().keySet()) {
                    suggestion.addEntry(new SuggestionEntry(spawnId, Component.text(spawnId)));
                }
            });

    public static final ArgumentWord TEAM_ARG = new ArgumentWord("teamId");
    public static final ArgumentWord TEAM_AUTO_ALL_ARG = (ArgumentWord) new ArgumentWord("teamId")
            .setSuggestionCallback((sender, context, suggestion) -> {
                var builder = Builder.fromSender(sender);
                if(builder == null) { return; }

                var instance = builder.getInstance();

                var oMap = TOOL_MANAGER.getMapByInstance(instance);
                if(oMap.isEmpty()) { return; }
                var map = oMap.get();

                var game = map.getGame();
                Set<? extends ITeamType> types = game.getTeamTypes();

                for(ITeamType team : types) {
                    var teamId = team.getId();
                    suggestion.addEntry(new SuggestionEntry(teamId, Component.text(teamId)));
                }
            });

    public static final ArgumentRelativeVec3 POSITION_ARG = new ArgumentRelativeVec3("position");
    public static final ArgumentFloat PITCH_ARG = new ArgumentFloat("pitch"); //TODO ArgumentRotation possibly?
    public static final ArgumentFloat YAW_ARG = new ArgumentFloat("yaw");

    public SpawnCommand() {
        super("spawn");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new CreateCommand());
        addSubcommand(new RemoveCommand());
        addSubcommand(new OwnerCommand());
        addSubcommand(new TeleportCommand());
        addSubcommand(new InfoCommand());
        addSubcommand(new ListCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(Message.notification("Map",
                Component.text("Usage: /slime spawn <create|remove|owner|tp|info|list>", NamedTextColor.GRAY)));
    }

}
