package cc.minetale.slime.map.tools.commands.map;

import cc.minetale.buildingtools.Utils;
import cc.minetale.commonlib.util.MC;
import cc.minetale.slime.Slime;
import cc.minetale.slime.utils.MapUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.utils.NamespaceID;

import static cc.minetale.slime.map.tools.commands.MapCommand.DIMENSION_ARG;
import static cc.minetale.slime.map.tools.commands.MapCommand.NAME_ARG;

public final class ModifyCommand extends Command {

    public ModifyCommand() {
        super("modify");

        setDefaultExecutor(this::defaultExecutor);

        var subcmd = new Command("name");
        subcmd.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime map modify name <newName>", NamedTextColor.GRAY)));
        });
        subcmd.addSyntax(this::modifyMap, NAME_ARG);
        addSubcommand(subcmd);

        subcmd = new Command("dimension");
        subcmd.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Usage: /slime map modify dimension <newDimensionId>", NamedTextColor.GRAY)));
        });
        subcmd.addSyntax(this::modifyMap, DIMENSION_ARG);
        addSubcommand(subcmd);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Map",
                Component.text("Usage: /slime map modify <name|dimension|bounds> ...", NamedTextColor.GRAY)));
    }

    public void modifyMap(CommandSender sender, CommandContext context) {
        var builder = Utils.getSenderAsBuilder(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var oMap = Slime.TOOL_MANAGER.getMapByInstance(instance);
        if(oMap.isEmpty()) {
            sender.sendMessage(MC.notificationMessage("Map",
                    Component.text("Something went wrong when looking up the map you're currently in.", NamedTextColor.RED)));
            return;
        }
        var map = oMap.get();

        var handle = map.getHandle();

        if(context.has(NAME_ARG)) {
            var newName = context.get(NAME_ARG);
            handle.setName(newName);
            sender.sendMessage(MC.notificationMessage("Map", Component.text().append(
                    Component.text("Successfully changed the name of \"" + MapUtil.getFullId(handle) +
                            "\" to \"" + newName + "\".", NamedTextColor.GREEN),
                    Component.newline(),
                    Component.text("Make sure to save the map with \"/slime map save\".", NamedTextColor.YELLOW))
                    .build())
            );
        } else if(context.has(DIMENSION_ARG)) {
            var newDimension = context.get(DIMENSION_ARG);
            handle.setDimension(NamespaceID.from(newDimension));
            sender.sendMessage(MC.notificationMessage("Map", Component.text()
                    .append(Component.text("Successfully changed the dimension of \"" + MapUtil.getFullId(handle) +
                                    "\" to \"" + newDimension + "\".", NamedTextColor.GREEN),
                            Component.newline(),
                            Component.text("Make sure to save the map with \"/slime map save\".", NamedTextColor.YELLOW))
                    .build()));
        }

        //TODO Editable bounds
    }

}
