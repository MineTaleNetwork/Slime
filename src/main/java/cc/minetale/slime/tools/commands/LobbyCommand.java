package cc.minetale.slime.tools.commands;

import cc.minetale.commonlib.util.Message;
import cc.minetale.slime.Slime;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.tools.commands.lobby.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class LobbyCommand extends Command {

    //Arguments
    public static final ArgumentWord LOBBY_ARG = new ArgumentWord("id");
    public static final ArgumentWord LOBBY_AUTO_ARG = (ArgumentWord) LOBBY_ARG
            .setSuggestionCallback((sender, context, suggestion) -> {
                if(context.has(CommonCommands.GAMEMODE_ARG)) {
                    var gamemode = context.get(CommonCommands.GAMEMODE_ARG);

                    var game = Slime.getRegisteredGame(gamemode);
                    if(game == null) { return; }

                    TOOL_MANAGER.getActiveMapsForGame(AbstractMap.Type.LOBBY, game)
                            .forEach(map -> {
                                var handle = map.getHandle();
                                var id = handle.getId();
                                suggestion.addEntry(new SuggestionEntry(id, Component.text(id)));
                            });
                }
            });

    public LobbyCommand() {
        super("lobby");

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
        sender.sendMessage(Message.notification("Lobby",
                Component.text("Usage: /slime lobby <create|modify|remove|save|list|info|load|unload|open|close|tp>", NamedTextColor.GRAY)));
    }

}