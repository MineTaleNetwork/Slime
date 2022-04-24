package cc.minetale.slime;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.slime.commands.SlimeCommand;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.item.ItemManager;
import cc.minetale.slime.item.base.IDigger;
import cc.minetale.slime.loot.LootRegistry;
import cc.minetale.slime.loot.LootTable;
import cc.minetale.slime.loot.TableType;
import cc.minetale.slime.loot.context.LootContext;
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
import cc.minetale.slime.utils.MiscUtil;
import cc.minetale.slime.utils.jackson.serializer.EnchantmentSerializers;
import cc.minetale.slime.utils.jackson.serializer.MaterialSerializers;
import cc.minetale.slime.utils.jackson.serializer.PotionTypeSerializers;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.extensions.ExtensionManager;
import net.minestom.server.gamedata.tags.TagManager;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.potion.PotionType;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    private static final Map<String, GameInfo> registeredGames = Collections.synchronizedMap(new HashMap<>());

    public static final SlimeCommand MAIN_CMD = new SlimeCommand();

    @Override
    public LoadStatus initialize() {
        final var mapper = CommonLib.getJsonMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .registerModule(new SimpleModule()
                        .addSerializer(IntegerRangeProvider.class, new RangeProviderSerializers.IntegerRange.Serializer())
                        .addDeserializer(IntegerRangeProvider.class, new RangeProviderSerializers.IntegerRange.Deserializer())
                        .addSerializer(FloatRangeProvider.class, new RangeProviderSerializers.FloatRange.Serializer())
                        .addDeserializer(FloatRangeProvider.class, new RangeProviderSerializers.FloatRange.Deserializer())
                        .addSerializer(DoubleRangeProvider.class, new RangeProviderSerializers.DoubleRange.Serializer())
                        .addDeserializer(DoubleRangeProvider.class, new RangeProviderSerializers.DoubleRange.Deserializer())

                        .setSerializerModifier(new NumberProviderSerializers.Modifier())
                        .addDeserializer(NumberProvider.class, new NumberProviderSerializers.Deserializer())

                        .addDeserializer(LootPredicate.class, new LootPredicateSerializers.Deserializer())
                        .addDeserializer(LootEntry.class, new LootEntrySerializers.Deserializer())
                        .addDeserializer(LootFunction.class, new LootFunctionSerializers.Deserializer())

                        .addSerializer(CopyNBTFunction.class, new CopyNBTFunction.Serializers.Serializer())
                        .addDeserializer(CopyNBTFunction.class, new CopyNBTFunction.Serializers.Deserializer())

                        .addSerializer(NamespaceID.class, new NamespaceSerializers.Serializer())
                        .addDeserializer(NamespaceID.class, new NamespaceSerializers.Deserializer())
                        .addKeyDeserializer(NamespaceID.class, new NamespaceSerializers.KeyDeserializer())

                        .addSerializer(Material.class, new MaterialSerializers.Serializer())
                        .addDeserializer(Material.class, new MaterialSerializers.Deserializer())
                        .addSerializer(Enchantment.class, new EnchantmentSerializers.Serializer())
                        .addDeserializer(Enchantment.class, new EnchantmentSerializers.Deserializer())
                        .addSerializer(PotionType.class, new PotionTypeSerializers.Serializer())
                        .addDeserializer(PotionType.class, new PotionTypeSerializers.Deserializer()));

        COMMAND_MANAGER.register(MAIN_CMD);

        //Loot table debug
//        System.out.println(Material.DIAMOND_AXE.registry().maxDamage());
//        var writer = mapper.writerWithDefaultPrettyPrinter();
//        var time = System.currentTimeMillis();
//        var table = LootRegistry.getLootTable(TableType.CHEST);
//        testTable(writer, table);

        ITEM_MANAGER.addVanillaFeatures();

        var table = LootRegistry.getLootTable(TableType.BLOCK);

//        AtomicReference<Block> placeBlock = new AtomicReference<>();
        MinecraftServer.getGlobalEventHandler()
                .addListener(PlayerBlockBreakEvent.class, event -> {
                    final var block = event.getBlock();
                    final var player = event.getPlayer();
                    handleBlockDrop(player, block, event.getBlockPosition(), table);
                })
                //Instant breaking blocks, like snow
                .addListener(PlayerStartDiggingEvent.class, event -> {
                    final var block = event.getBlock();
                    final var player = event.getPlayer();
                    var isInstant = isInstantBreak(block, player);
                    if(!isInstant) { return; }
                    handleBlockDrop(player, block, event.getBlockPosition(), table);
                });
//                .addListener(PlayerBlockInteractEvent.class, event -> {
//                    Block block = placeBlock.get();
//                    if(block == null) { return; }
//
//                    var direction = event.getBlockFace().toDirection();
//                    event.getInstance().setBlock(event.getBlockPosition().add(direction.normalX(), direction.normalY(), direction.normalZ()), block);
//                })
//                .addListener(PlayerChatEvent.class, event -> {
//                    var message = event.getMessage();
//                    if(message.equals("crafting")) {
//                        var inv = new Inventory(InventoryType.CRAFTING, "ew");
//                        event.getPlayer().openInventory(inv);
//                    }
//
//                    var id = NamespaceID.from(message);
//                    var block = Block.fromNamespaceId(id);
//                    if(block == null) {
//                        event.getPlayer().sendMessage("ew");
//                        return;
//                    }
//
//                    Collection<Block> states = block.possibleStates();
//                    var state = getRandomState(states);
//
//                    event.getPlayer().sendMessage(state.toString());
//                    placeBlock.set(state);
//                })

        if(EXTENSION_MANAGER.hasExtension("buildingtools")) {
            LOGGER.info("Building Tools has been detected. Loading Slime as tooling.");
            TOOL_MANAGER.initialize();
        }

        return LoadStatus.SUCCESS;
    }

    //TODO Remove in production
    private boolean isInstantBreak(Block block, Player player) {
        final var minDamage = block.registry().hardness() * 30;

        final var tool = player.getItemInMainHand();
        final var toolMaterial = tool.getMaterial();
        final var toolId = toolMaterial.namespace();

        var possibleItems = ITEM_MANAGER.getItemsByMaterial(toolMaterial);
        if(possibleItems == null || possibleItems.isEmpty()) { return false; }

        float damage = 1f;
        for(var item : possibleItems) {
            if(item instanceof IDigger digger) {
                if(digger.canBreakBlock(block))
                    damage = digger.getMiningSpeedMultiplierFor(block);

                break;
            }
        }

        var efficiencyLevel = tool.getMeta()
                .getEnchantmentMap()
                .getOrDefault(Enchantment.EFFICIENCY, (short) 0);

        if(efficiencyLevel > 0) {
            damage += (efficiencyLevel*efficiencyLevel) + 1;
        }

        for(var timedPotion : player.getActiveEffects()) {
            var potion = timedPotion.getPotion();
            var effect = potion.effect();
            byte amplifier = potion.amplifier();

            if(effect == PotionEffect.HASTE)
                damage *= 1f + (amplifier + 1) * .2f;

            if(effect == PotionEffect.MINING_FATIGUE)
                damage *= switch (amplifier) {
                    case 0 -> 0.3f;
                    case 1 -> 0.09f;
                    case 2 -> 0.0027f;
                    default -> 0.00081f;
                };
        }

        var instance = player.getInstance();
        if(instance == null) { return false; }

        var insideBlock = instance.getBlock(player.getPosition());
        var hasAquaAffinity = MiscUtil.getEquipment(player)
                .stream().noneMatch(item -> {
                    var aquaAffinity = item.getMeta()
                            .getEnchantmentMap()
                            .getOrDefault(Enchantment.AQUA_AFFINITY, (short) 0);

                    return aquaAffinity > 0;
                });

        if(insideBlock == Block.WATER && !hasAquaAffinity)
            damage /= .5f;

        if(!player.isOnGround())
            damage /= .5f;

        return damage > minDamage;
    }

    //TODO Remove in production
    private void handleBlockDrop(Player player, Block block, Point event, LootTable table) {
        final var tool = player.getItemInMainHand();

        var possibleItems = ITEM_MANAGER.getItemsFromStack(tool);
        if(possibleItems == null || possibleItems.isEmpty()) { return; }

        for(var item : possibleItems) {
            if(item instanceof IDigger digger) {
                if(!digger.canBreakBlock(block)) { return; }
                break;
            }
        }

        final var instance = player.getInstance();

        var ctx = new LootContext.BlockCtx(NamespaceID.from(block.namespace()), instance, event, block, tool, player);

        List<ItemStack> loot = table.generateLoot(ctx);

        final var rng = ThreadLocalRandom.current();
        for(var itemStack : loot) {
            var itemEntity = new ItemEntity(itemStack);
            itemEntity.setInstance(instance, new Pos(event).add(.5, .5, .5));

            var velX = (rng.nextDouble() * 2d) - 1d;
            var velZ = (rng.nextDouble() * 2d) - 1d;
            itemEntity.setVelocity(new Vec(velX, 1d, velZ));
        }
    }

//    private Block getRandomState(Collection<Block> states) {
//        for(var state : states) {
//            if(ThreadLocalRandom.current().nextBoolean())
//                return state;
//        }
//        return getRandomState(states);
//    }
//
//    private void testTable(ObjectWriter writer, LootTable table) {
//        for(Map.Entry<NamespaceID, Loot> ent : table.getRawTable().entrySet()) {
//            var lootId = ent.getKey();
//            var loot = ent.getValue();
//            if(testLoot(lootId, loot)) break;
//        }
//        try {
//            System.out.println(writer.writeValueAsString(table.getLootFor(NamespaceID.from("buried_treasure"))));
//        } catch(JsonProcessingException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private boolean testLoot(NamespaceID lootId, Loot loot) {
//        if(loot == null) {
//            System.out.println(lootId + " has null!");
//            return true;
//        }
//
//        if(loot.getFunctions()
//                .stream()
//                .anyMatch(Objects::isNull)) {
//
//            System.out.println(lootId + " has null!");
//            return true;
//        }
//
//        for(var pool : loot.getPools()) {
//            if(!testPool(lootId, pool)) { return false; }
//        }
//
//        return false;
//    }
//
//    private boolean testPool(NamespaceID lootId, LootPool pool) {
//        for(var lootEnt : pool.getEntries()) {
//            if(lootEnt == null) {
//                System.out.println(lootId + " has null!");
//                return false;
//            }
//
//            for(var condition : lootEnt.getConditions()) {
//                if(!testCondition(lootId, condition)) { return false; }
//            }
//
//            for(var function : lootEnt.getFunctions()) {
//                if(function == null) {
//                    System.out.println(lootId + " has null!");
//                    return false;
//                }
//            }
//        }
//
//        for(var condition : pool.getConditions()) {
//            if(!testCondition(lootId, condition)) { return false; }
//        }
//
//        for(var function : pool.getFunctions()) {
//            if(function == null) {
//                System.out.println(lootId + " has null!");
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private boolean testCondition(NamespaceID lootId, LootPredicate condition) {
//        if(condition == null) {
//            System.out.println(lootId + " has null!");
//            return false;
//        }
//
//        if(condition instanceof LogicalPredicate logicalCondition) {
//            if(!logicalCondition.getTerms()
//                    .stream()
//                    .allMatch(alternative -> testCondition(lootId, alternative))) {
//
//                return false;
//            }
//        }
//
//        return true;
//    }

    public static GameInfo getRegisteredGame(String id) {
        return registeredGames.get(id);
    }

    public static boolean registerGame(@NotNull GameInfo game) {
        return registeredGames.putIfAbsent(game.getId(), game) == null;
    }

    public static boolean unregisterGame(String id) {
        return registeredGames.remove(id) != null;
    }

    public static boolean unregisterGame(@NotNull GameInfo game) {
        return unregisterGame(game.getId());
    }

    public static @NotNull @UnmodifiableView Collection<GameInfo> getRegisteredGames() {
        return Collections.unmodifiableCollection(registeredGames.values());
    }

    @Override public void terminate() { }

}
