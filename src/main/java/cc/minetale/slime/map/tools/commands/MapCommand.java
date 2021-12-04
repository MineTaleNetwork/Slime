package cc.minetale.slime.map.tools.commands;

import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.map.tools.ToolManager;
import cc.minetale.slime.map.tools.commands.map.*;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public final class MapCommand extends Command {

    public static final ToolManager TOOL_MANAGER = Slime.TOOL_MANAGER;

    //Arguments
    public static final ArgumentWord MAP_ARG = new ArgumentWord("id");
    public static final ArgumentWord MAP_AUTO_ARG = (ArgumentWord) MAP_ARG
            .setSuggestionCallback((sender, context, suggestion) -> {
                if(context.has("gamemode")) {
                    String gamemode = context.get("gamemode");

                    var oGame = Slime.TOOL_MANAGER.getGame(gamemode);
                    if(oGame.isEmpty()) { return; }
                    var game = oGame.get();

                    Slime.TOOL_MANAGER.getActiveMapsForGame(game)
                            .forEach(map -> {
                                var handle = map.getHandle();
                                var id = handle.getId();
                                suggestion.addEntry(new SuggestionEntry(id, Component.text(id)));
                            });
                }
            });

    public static final ArgumentString NAME_ARG = new ArgumentString("name");

    public static final ArgumentWord GAMEMODE_ARG = (ArgumentWord) new ArgumentWord("gamemode")
            .setSuggestionCallback((CommandSender sender, CommandContext context, Suggestion suggestion) -> {
                for(GameExtension game : Slime.TOOL_MANAGER.getAvailableGames()) {
                    var id = game.getId();
                    suggestion.addEntry(new SuggestionEntry(id, Component.text(id)));
                }
            });

    //TODO Make it so if any gamemode extension is loaded, it'll restrict this to only IDs of the currently loaded gamemode extensions
    public static final ArgumentWord DIMENSION_ARG = new ArgumentWord("dimension")
            .from("minecraft:overworld", "minecraft:nether", "minecraft:the_end");

    public MapCommand() {
        super("map");

        setDefaultExecutor(this::defaultExecutor);

        addSubcommand(new CreateCommand());

        addSubcommand(new ModifyCommand());
        addSubcommand(new RemoveCommand());
        addSubcommand(new SaveCommand());

        addSubcommand(new ListCommand());
        addSubcommand(new InfoCommand());

        addSubcommand(new LoadCommand());
        addSubcommand(new UnloadCommand());

        addSubcommand(new OpenCommand());
        addSubcommand(new CloseCommand());

        addSubcommand(new TeleportCommand());
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.Chat.notificationMessage("Map", Component.text("Usage: /slime map <create|modify|remove|save|list|info|load|unload|open|close|tp>", MC.CC.GRAY.getTextColor())));
    }

}