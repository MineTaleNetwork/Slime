package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.slime.Slime;
import cc.minetale.slime.map.tools.TempMap;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;

public final class ListCommand extends Command {

    public ListCommand() {
        super("list");

        setDefaultExecutor(this::defaultExecutor);
    }

    //TODO Make a bit more clean
    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage("Active Maps: ");
        for(TempMap map : Slime.TOOL_MANAGER.getActiveMaps()) {
            var handle = map.getHandle();
            sender.sendMessage(handle.getGamemode() + ":" + handle.getId());
        }
    }

}
