package cc.minetale.slime.map.tools.commands;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.map.tools.commands.spawn.CreateCommand;
import cc.minetale.slime.map.tools.commands.spawn.RemoveCommand;
import cc.minetale.slime.team.ITeamType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class SpawnCommand extends Command {

    public static final ArgumentWord SPAWN_ARG = new ArgumentWord("id");
    public static final ArgumentWord SPAWN_AUTO_ARG = (ArgumentWord) SPAWN_ARG
            .setSuggestionCallback((sender, context, suggestion) -> {
                var builder = Utils.getSenderAsBuilder(sender);
                if(builder == null) { return; }

                var instance = builder.getInstance();

                var oMap = Slime.TOOL_MANAGER.getMapByInstance(instance);
                if(oMap.isEmpty()) { return; }
                var map = oMap.get();

                var handle = map.getHandle();

                for(String spawnId : handle.getSpawnPoints().keySet()) {
                    suggestion.addEntry(new SuggestionEntry(spawnId, Component.text(spawnId)));
                }
            });

    public static final ArgumentWord TEAM_ARG = new ArgumentWord("teamId");
    public static final ArgumentWord TEAM_AUTO_ARG = (ArgumentWord) TEAM_ARG
            .setSuggestionCallback((sender, context, suggestion) -> {
                var builder = Utils.getSenderAsBuilder(sender);
                if(builder == null) { return; }

                var instance = builder.getInstance();

                var oMap = Slime.TOOL_MANAGER.getMapByInstance(instance);
                if(oMap.isEmpty()) { return; }
                var map = oMap.get();

                for(ITeamType team : map.getGame().getTeamTypes()) {
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
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime map modify <name|dimension|bounds> ...", NamedTextColor.GRAY)));
    }

}
