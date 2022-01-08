package cc.minetale.slime.tools.commands;

import cc.minetale.buildingtools.Builder;
import cc.minetale.commonlib.util.MC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;

public class DebugCommand extends Command {

    private static Instance debugInstance;

    //Arguments
    public static final ArgumentWord OPTION_ARG = new ArgumentWord("option")
            .from("brokenBiomes",
                    "tpToDebugInstance");

    public DebugCommand() {
        super("debug");

        setDefaultExecutor(this::defaultExecutor);

        addSyntax((sender, context) -> {
            if(!context.has(OPTION_ARG)) { return; }
            var option = context.get(OPTION_ARG);
            switch(option) {
                case "brokenBiomes" -> checkForBrokenBiomes(sender, context);
                case "tpToDebugInstance" -> teleportToDebugInstance(sender, context);
                default -> defaultExecutor(sender, context);
            }
        }, OPTION_ARG);
    }

    private void defaultExecutor(CommandSender sender, CommandContext context) {
        sender.sendMessage(MC.notificationMessage("Debug",
                Component.text("Usage: /slime debug <option>", NamedTextColor.GRAY)));
    }

    private void checkForBrokenBiomes(CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        var instance = builder.getInstance();

        var minY = instance.getDimensionType().getMinY();
        var sectionSize = Chunk.CHUNK_SECTION_SIZE;
        var minSection = minY / sectionSize;

        for(Chunk chunk : instance.getChunks()) {
            for(int i = 0; i < chunk.getSections().size(); i++) {
                var section = chunk.getSections().get(i);

                var palette = section.biomePalette();
                var dimension = palette.dimension();

                var expected = dimension * dimension * dimension;
                var current = palette.maxSize();

                if(current != expected) {
                    sender.sendMessage("Section " + (i + minSection) + " at chunk " + chunk.getChunkX() + ", " + chunk.getChunkZ() +
                            " has its palette not match expected maximum size. Expected: " + expected + " Current: " + current);
                }

                for(int x = 0; x < Chunk.CHUNK_SIZE_X; x += 4) {
                    for(int y = 0; y < Chunk.CHUNK_SECTION_SIZE; y += 4) {
                        for(int z = 0; z < Chunk.CHUNK_SIZE_Z; z += 4) {
                            var id = palette.get(x, y, z);
                            if(id < 0) {
                                sender.sendMessage("Section " + (i + minSection) + " at chunk " + chunk.getChunkX() + ", " + chunk.getChunkZ() +
                                        " has its palette contain an id below zero (Current: " + id + ") at " + x + ", " + y + ", " + z);
                            }
                            if(MinecraftServer.getBiomeManager().getById(id) == null) {
                                sender.sendMessage("Section " + (i + minSection) + " at chunk " + chunk.getChunkX() + ", " + chunk.getChunkZ() +
                                        " has its palette contain an id of a nonexistent biome (Current: " + id + ") at " + x + ", " + y + ", " + z);
                            }
                        }
                    }
                }
            }
        }
    }

    private void teleportToDebugInstance(CommandSender sender, CommandContext context) {
        var builder = Builder.fromSender(sender);
        if(builder == null) { return; }

        if(debugInstance == null) {
            debugInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
        }

        builder.setInstance(debugInstance);
    }

}
