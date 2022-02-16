package cc.minetale.slime;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.slime.commands.SlimeCommand;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.item.ItemManager;
import cc.minetale.slime.loot.entry.LootEntry;
import cc.minetale.slime.loot.function.CopyNBTFunction;
import cc.minetale.slime.loot.function.LootFunction;
import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.serializer.*;
import cc.minetale.slime.loot.util.DoubleRangeProvider;
import cc.minetale.slime.loot.util.FloatRangeProvider;
import cc.minetale.slime.loot.util.IntegerRangeProvider;
import cc.minetale.slime.loot.util.NumberProvider;
import cc.minetale.slime.tools.ToolManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.extensions.Extension;
import net.minestom.server.extensions.ExtensionManager;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.NamespaceID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Slime extends Extension {

    private static final Logger LOGGER = LoggerFactory.getLogger(Slime.class);

    public static final ExtensionManager EXTENSION_MANAGER = MinecraftServer.getExtensionManager();
    public static final ConnectionManager CONNECTION_MANAGER = MinecraftServer.getConnectionManager();
    public static final SchedulerManager SCHEDULER_MANAGER = MinecraftServer.getSchedulerManager();

    public static final InstanceManager INSTANCE_MANAGER = MinecraftServer.getInstanceManager();
    public static final CommandManager COMMAND_MANAGER = MinecraftServer.getCommandManager();
    public static final TeamManager TEAM_MANAGER = MinecraftServer.getTeamManager();
    public static final TagManager TAG_MANAGER = MinecraftServer.getTagManager();

    public static final ToolManager TOOL_MANAGER = new ToolManager();
    public static final ItemManager ITEM_MANAGER = new ItemManager();

    public static final SlimeCommand MAIN_CMD = new SlimeCommand();


    //TODO Make the setter its own method and safely switch games
    //TODO Figure out a reasonable way to shorten any calls to this, now for example you have to do Slime.getActiveGame().getMaxGames()
    @Getter @Setter private static GameExtension activeGame;

    @Override public void initialize() {
        final var mapper = CommonLib.getJsonMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .registerModule(new SimpleModule()
                        .addSerializer(IntegerRangeProvider.class, new RangeProviderSerializers.IntegerRange.Serializer())
                        .addDeserializer(IntegerRangeProvider.class, new RangeProviderSerializers.IntegerRange.Deserializer())
                        .addSerializer(FloatRangeProvider.class, new RangeProviderSerializers.FloatRange.Serializer())
                        .addDeserializer(FloatRangeProvider.class, new RangeProviderSerializers.FloatRange.Deserializer())
                        .addSerializer(DoubleRangeProvider.class, new RangeProviderSerializers.DoubleRange.Serializer())
                        .addDeserializer(DoubleRangeProvider.class, new RangeProviderSerializers.DoubleRange.Deserializer())

                        .setSerializerModifier(new NumberProviderSerializers.NumberProviderModifier())
                        .addDeserializer(NumberProvider.class, new NumberProviderSerializers.NumberProviderDeserializer())

                        .addDeserializer(LootPredicate.class, new LootPredicateSerializers.LootPredicateDeserializer())
                        .addDeserializer(LootEntry.class, new LootEntrySerializers.LootEntryDeserializer())
                        .addDeserializer(LootFunction.class, new LootFunctionSerializers.LootFunctionDeserializer())

                        .addSerializer(CopyNBTFunction.class, new CopyNBTFunction.Serializers.CopyNBTFunctionSerializer())
                        .addDeserializer(CopyNBTFunction.class, new CopyNBTFunction.Serializers.CopyNBTFunctionDeserializer())

                        .addSerializer(NamespaceID.class, new NamespaceSerializers.NamespaceSerializer())
                        .addDeserializer(NamespaceID.class, new NamespaceSerializers.NamespaceDeserializer())
                        .addKeyDeserializer(NamespaceID.class, new NamespaceSerializers.NamespaceKeyDeserializer()));

        COMMAND_MANAGER.register(MAIN_CMD);

        ITEM_MANAGER.addVanillaFeatures();

        if(EXTENSION_MANAGER.hasExtension("buildingtools")) {
            LOGGER.info("Building Tools has been detected. Loading Slime as tooling.");
            TOOL_MANAGER.initialize();
        }
    }

    @Override public void terminate() { }

}
