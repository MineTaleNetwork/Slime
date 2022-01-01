package cc.minetale.slime.map.tools.commands.spawn;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.utils.MapUtil;
import cc.minetale.slime.utils.TeamUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import java.util.HashSet;
import java.util.Set;

import static cc.minetale.slime.Slime.TOOL_MANAGER;
import static cc.minetale.slime.map.tools.commands.SpawnCommand.SPAWN_ARG;
import static cc.minetale.slime.map.tools.commands.SpawnCommand.SPAWN_AUTO_ARG;

public final class OwnerCommand extends Command {

    public static final ArgumentWord TEAM_AUTO_ADD_ARG = (ArgumentWord) new ArgumentWord("teamId")
            .setSuggestionCallback((sender, context, suggestion) -> {
                var builder = Builder.fromSender(sender);
                if(builder == null) { return; }

                var instance = builder.getInstance();

                var oMap = TOOL_MANAGER.getMapByInstance(instance);
                if(oMap.isEmpty()) { return; }
                var map = oMap.get();

                if(context.has(SPAWN_ARG)) {
                    var spawnId = context.get(SPAWN_ARG);
                    var handle = map.getHandle();
                    var spawn = handle.getSpawn(spawnId);

                    var game = map.getGame();

                    Set<ITeamType> types = new HashSet<>(game.getTeamTypes());
                    Set<ITeamType> owners = spawn.getOwners();
                    types.removeAll(owners);

                    for(ITeamType type : types) {
                        var teamId = type.getId();
                        suggestion.addEntry(new SuggestionEntry(teamId, Component.text(teamId)));
                    }
                }
            });

    public static final ArgumentWord TEAM_AUTO_REMOVE_ARG = (ArgumentWord) new ArgumentWord("teamId")
            .setSuggestionCallback((sender, context, suggestion) -> {
                var builder = Builder.fromSender(sender);
                if(builder == null) { return; }

                var instance = builder.getInstance();

                var oMap = TOOL_MANAGER.getMapByInstance(instance);
                if(oMap.isEmpty()) { return; }
                var map = oMap.get();

                if(context.has(SPAWN_ARG)) {
                    var spawnId = context.get(SPAWN_ARG);
                    var handle = map.getHandle();
                    var spawn = handle.getSpawn(spawnId);

                    Set<ITeamType> owners = spawn.getOwners();

                    for(ITeamType owner : owners) {
                        var teamId = owner.getId();
                        suggestion.addEntry(new SuggestionEntry(teamId, Component.text(teamId)));
                    }
                }
            });

    public OwnerCommand() {
        super("owner");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new AddCommand());
        addSubcommand(new RemoveCommand());
        addSubcommand(new ListCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime spawn owner <add|remove|list>", NamedTextColor.GRAY)));
    }

    public static class AddCommand extends Command {

        public AddCommand() {
            super("add");

            setDefaultExecutor(this::defaultExecutor);

            //TODO Fix argument TEAM_AUTO_ARG (Smart depend on spawnId or have separate arguments)
            addSyntax(this::addOwner, SPAWN_AUTO_ARG, TEAM_AUTO_ADD_ARG);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime spawn owner add <spawnId> <teamId>", NamedTextColor.GRAY)));
        }

        private void addOwner(CommandSender sender, CommandContext context) {
            var builder = Builder.fromSender(sender);
            if(builder == null) { return; }

            if(!builder.isBuilderMode()) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text("You need to be in builder mode to execute this command!",
                        NamedTextColor.RED)));
                return;
            }

            var spawnId = context.get(SPAWN_AUTO_ARG);
            var teamId = context.get(TEAM_AUTO_ADD_ARG);

            var instance = builder.getInstance();

            var oMap = TOOL_MANAGER.getMapByInstance(instance);
            if(oMap.isEmpty()) {
                sender.sendMessage(MC.notificationMessage("Map",
                        Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
                return;
            }
            var map = oMap.get();

            var spawn = map.getHandle().getSpawn(spawnId);
            if(spawn == null) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text("Spawn doesn't exist! " +
                        "Make sure you typed in the name correctly and the spawn exists.", NamedTextColor.RED)));
                return;
            }

            Set<? extends ITeamType> types = map.getGame().getTeamTypes();
            var team = TeamUtil.findById(types, teamId);

            if(spawn.addOwner(team)) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text(
                        "Successfully added team \"" + teamId + "\" to owners for spawn \"" +
                                spawnId + "\" in map " + "\"" + MapUtil.getFullId(map) + "\".",
                        NamedTextColor.GREEN)));
            } else {
                sender.sendMessage(MC.notificationMessage("Map", Component.text(
                        "There was a problem adding team \"" + teamId + "\" to owners for spawn \"" +
                                spawnId + "\" in map " + "\"" + MapUtil.getFullId(map) + "\".",
                        NamedTextColor.RED)));
            }
        }

    }

    public static class RemoveCommand extends Command {

        public RemoveCommand() {
            super("remove");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::addOwner, SPAWN_AUTO_ARG, TEAM_AUTO_REMOVE_ARG);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime spawn owner remove <spawnId> <teamId>", NamedTextColor.GRAY)));
        }

        private void addOwner(CommandSender sender, CommandContext context) {
            var builder = Builder.fromSender(sender);
            if(builder == null) { return; }

            if(!builder.isBuilderMode()) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text("You need to be in builder mode to execute this command!",
                        NamedTextColor.RED)));
                return;
            }

            var spawnId = context.get(SPAWN_AUTO_ARG);
            var teamId = context.get(TEAM_AUTO_REMOVE_ARG);

            var instance = builder.getInstance();

            var oMap = TOOL_MANAGER.getMapByInstance(instance);
            if(oMap.isEmpty()) {
                sender.sendMessage(MC.notificationMessage("Map",
                        Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
                return;
            }
            var map = oMap.get();

            var spawn = map.getHandle().getSpawn(spawnId);
            if(spawn == null) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text("Spawn doesn't exist! " +
                        "Make sure you typed in the name correctly and the spawn exists.", NamedTextColor.RED)));
                return;
            }

            Set<? extends ITeamType> types = map.getGame().getTeamTypes();
            var team = TeamUtil.findById(types, teamId);

            if(spawn.removeOwner(team)) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text(
                        "Successfully removed team \"" + teamId + "\" from owners for spawn \"" +
                                spawnId + "\" in map \"" + MapUtil.getFullId(map) + "\".",
                        NamedTextColor.GREEN)));
            } else {
                sender.sendMessage(MC.notificationMessage("Map", Component.text(
                        "There was a problem removing team \"" + teamId + "\" from owners for spawn \"" +
                                spawnId + "\" in map \"" + MapUtil.getFullId(map) + "\".",
                        NamedTextColor.RED)));
            }
        }

    }

    public static class ListCommand extends Command {

        public ListCommand() {
            super("list");

            setDefaultExecutor(this::defaultExecutor);

            addSyntax(this::addOwner, SPAWN_AUTO_ARG);
        }

        private void defaultExecutor(CommandSender sender, CommandContext context) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime spawn owner list <spawnId>", NamedTextColor.GRAY)));
        }

        private void addOwner(CommandSender sender, CommandContext context) {
            var builder = Builder.fromSender(sender);
            if(builder == null) { return; }

            if(!builder.isBuilderMode()) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text("You need to be in builder mode to execute this command!",
                        NamedTextColor.RED)));
                return;
            }

            var spawnId = context.get(SPAWN_AUTO_ARG);

            var instance = builder.getInstance();

            var oMap = TOOL_MANAGER.getMapByInstance(instance);
            if(oMap.isEmpty()) {
                sender.sendMessage(MC.notificationMessage("Map",
                        Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
                return;
            }
            var map = oMap.get();

            var spawn = map.getHandle().getSpawn(spawnId);
            if(spawn == null) {
                sender.sendMessage(MC.notificationMessage("Map", Component.text("Spawn doesn't exist! " +
                        "Make sure you typed in the name correctly and the spawn exists.", NamedTextColor.RED)));
                return;
            }

            sender.sendMessage("Owners for \"" + spawn.getId() + "\": ");
            for(ITeamType type : spawn.getOwners()) {
                sender.sendMessage(type.getId());
            }
        }

    }

}
