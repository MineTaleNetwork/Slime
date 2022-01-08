package cc.minetale.slime.tools.commands.map;

import cc.minetale.slime.tools.TempMap;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public final class ListCommand extends Command {

    public ListCommand() {
        super("list");

        setDefaultExecutor(this::defaultExecutor);
    }

    //TODO Make a bit more clean
    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage("Active Game Maps:");
        for(TempMap map : TOOL_MANAGER.getActiveGameMaps()) {
            var handle = map.getHandle();
            sender.sendMessage(handle.getGamemode() + ":" + handle.getId());
        }
    }

}
