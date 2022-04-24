package cc.minetale.slime.rule;

import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.misc.restriction.Blacklist;
import cc.minetale.slime.misc.restriction.RestrictionList;
import cc.minetale.slime.misc.restriction.Whitelist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

//TODO Make something similar to ItemPredicate for restrictive rules
public class PlayerRule<T> extends Rule<T> {

    public static final Set<PlayerRule<?>> ALL_RULES = Collections.synchronizedSet(new HashSet<>());

    public static final IRuleType TYPE = new RuleType(true, PlayerRule.class);

    public PlayerRule(T defaultValue) {
        super(defaultValue);
        ALL_RULES.add(this);
    }

    public PlayerRule(T defaultValue, BiConsumer<GamePlayer, T> onRuleChange) {
        super(defaultValue, (BiConsumer) onRuleChange);
        ALL_RULES.add(this);
    }

    @Override
    public IRuleType getType() {
        return TYPE;
    }

    /**
     * {@linkplain Integer} (as {@linkplain TimeUnit#SERVER_TICK}) <br>
     * <br>
     * Sets the time in ticks it takes for the player to respawn. <br>
     * By default used in conjunction with {@linkplain PlayerRule#AUTO_DEATHCAM}, <br>
     * and is immediate/disabled if this is set to 0. <br>
     * Should be used by custom behaviours if {@linkplain PlayerRule#AUTO_DEATHCAM} is disabled.
     */
    public static final PlayerRule<@NotNull Duration> RESPAWN_TIME = new PlayerRule<>(Duration.ZERO);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players will automatically lose lives. <br>
     * Disable if you want to handle lives yourself or don't want to deal with this system.
     */
    public static final PlayerRule<@NotNull Boolean> AUTO_LOSE_LIVES = new PlayerRule<>(true);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with will be a temporary spectator. <br>
     * The time a player will be in this state is determined by {@linkplain PlayerRule#RESPAWN_TIME}. <br>
     * Otherwise you will have to handle the player after death. <br>
     * See also {@linkplain SpectatorSettings#autoEnable} and {@linkplain PlayerRule#RESPAWN_TIME}.
     */
    public static final PlayerRule<@NotNull Boolean> AUTO_DEATHCAM = new PlayerRule<>(true);

    public static final PlayerRule<@NotNull SpectatorSettings> SPECTATOR = new PlayerRule<>(new SpectatorSettings(
            true,
            true, (spectator, player) -> {
                if(spectator == player) { return false; }

                //If you're in a team, only show players within your team.
                var team = spectator.getGameTeam();
                if(team != null && team.getSize() > 1 && !team.isPlayerInTeam(player)) {
                    return false;
                }

//                return player.isAlive();
                return true;
            },
            true,
            true));

    @AllArgsConstructor @Setter
    public static class SpectatorSettings {
        /**
         * If enabled, players that died with 0 lives left will be a spectator. <br>
         * Otherwise you will have to handle the player after death yourself. <br>
         */
        private boolean autoEnable;

        //"Players" menu
        private boolean enablePlayersMenu;
        private BiPredicate<GamePlayer, GamePlayer> playerMenuPredicate;

        //"Spectator Settings" menu
        private boolean enableSettingsMenu;

        //"Games" menu
        private boolean enableGamesMenu;

        public boolean autoEnable() {
            return this.autoEnable;
        }

        public boolean enablePlayersMenu() {
            return this.enablePlayersMenu;
        }

        public boolean shouldShowPlayerInMenu(GamePlayer spectator, GamePlayer player) {
            return this.playerMenuPredicate.test(spectator, player);
        }

        public boolean enableSettingsMenu() {
            return this.enableSettingsMenu;
        }

        public boolean enableGamesMenu() {
            return this.enableGamesMenu;
        }
    }

    //Blocks

    /**
     * {@linkplain RestrictionList}&lt;{@linkplain Block}&gt; <br>
     * <br>
     * You can only place blocks that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<@NotNull RestrictionList<Block>> PLACE_BLOCKS = new PlayerRule<>(Blacklist.empty());

    /**
     * {@linkplain RestrictionList}&lt;{@linkplain Block}&gt; <br>
     * <br>
     * You can only break blocks that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<@NotNull RestrictionList<Block>> BREAK_BLOCKS = new PlayerRule<>(Blacklist.empty());

    //PvP & PvE

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, you can't attack other players. Takes priority over {@linkplain TeamRule#ATTACK_ALLIES}.
     */
    public static final PlayerRule<@NotNull Boolean> ATTACK_PLAYERS = new PlayerRule<>(true);

    /**
     * {@linkplain RestrictionList}&lt;{@linkplain Class}&lt;? extends {@linkplain Entity}&gt;&gt; <br>
     * <br>
     * You can only attack entities that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<@NotNull RestrictionList<Class<? extends Entity>>> ATTACK_ENTITIES = new PlayerRule<>(Blacklist.empty());

    //Interactions

    /**
     * {@linkplain InteractSettings} <br>
     * <br>
     * You can only interact if the interaction meets specified criteria.
     */
    public static final PlayerRule<@NotNull InteractSettings> INTERACT = new PlayerRule<>(InteractSettings.unrestricted());

    @AllArgsConstructor @Setter
    public static class InteractSettings {

        private @NotNull RestrictionList<Block> restrictedBlocks;
        private @NotNull RestrictionList<Material> restrictedMaterials; //TODO Use an ItemPredicate as described at the top of this class

        public static InteractSettings unrestricted() {
            return new InteractSettings(Blacklist.empty(), Blacklist.empty());
        }

        public static InteractSettings restricted() {
            return new InteractSettings(Whitelist.empty(), Whitelist.empty());
        }

        public static InteractSettings withRestrictedBlocks(@NotNull RestrictionList<Block> restrictedBlocks) {
            return new InteractSettings(restrictedBlocks, Blacklist.empty());
        }

        public static InteractSettings withRestrictedMaterials(@NotNull RestrictionList<Material> restrictedMaterials) {
            return new InteractSettings(Blacklist.empty(), restrictedMaterials);
        }

        public InteractSettings restrictBlocks(@NotNull RestrictionList<Block> restrictedBlocks) {
            this.restrictedBlocks = restrictedBlocks;
            return this;
        }

        public InteractSettings restrictMaterials(@NotNull RestrictionList<Material> restrictedMaterials) {
            this.restrictedMaterials = restrictedMaterials;
            return this;
        }

        public boolean canInteractWith(Block block) {
            return !this.restrictedBlocks.isRestricted(block);
        }

        public boolean canInteractUsing(Material material) {
            return !this.restrictedMaterials.isRestricted(material);
        }

    }

    //Movement

    /**
     * {@linkplain FreezeType} <br>
     * <br>
     * You can't move.
     */
    public static final PlayerRule<@NotNull FreezeType> FROZEN = new PlayerRule<>(FreezeType.NONE);

    public enum FreezeType {
        BOTH,
        POSITION,
        ANGLES,
        NONE
    }

    //Health & Food

    /**
     * {@linkplain HungerSettings} <br>
     * <br>
     * Settings related to hunger.
     */
    public static final PlayerRule<@NotNull HungerSettings> HUNGER = new PlayerRule<>(
            new HungerSettings(20, 20, 0),
            (player, value) -> {
                var food = value.getFood();
                if(food != -1)
                    player.setFood(food);

                var foodSaturation = value.getFoodSaturation();
                if(foodSaturation != -1)
                    player.setFoodSaturation(foodSaturation);
            });

    @AllArgsConstructor @Getter @Setter
    public static class HungerSettings {
        /**
         * Whenever the rule has changed, it'll reset player's food to this. <Br>
         * <strong>-1</strong> means the food won't be reset.
         */
        private int food;

        /**
         * Whenever the rule has changed, it'll reset player's food saturation to this. <Br>
         * <strong>-1</strong> means the food saturation won't be reset.
         */
        private float foodSaturation;

        /** <strong>0</strong> means hunger won't affect the player. */
        private float hungerMultiplier;
    }

    //Inventory

    /**
     * {@linkplain RestrictionList}&lt;{@linkplain ClickType}&gt; <br>
     * <br>
     * Restrict inventory clicking to specified {@linkplain ClickType}(s).
     * //TODO More options?
     */
    public static final PlayerRule<@NotNull RestrictionList<ClickType>> INVENTORY_CLICK = new PlayerRule<>(Blacklist.empty());

    //Other

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * You can pickup items only while in specified states.
     * TODO Add range option
     */
    public static final PlayerRule<@NotNull Boolean> PICKUP_ITEMS = new PlayerRule<>(true);

    /**
     * {@linkplain DropSettings} <br>
     * <br>
     * Dropping items. <br>
     * <strong>null</strong> value means this rule is disabled.
     */
    public static final PlayerRule<@Nullable DropSettings> DROP_ITEMS = new PlayerRule<>(
            new DropSettings(
                    (player, itemStack) -> itemStack,

                    (player, itemStack) -> new ItemEntity(itemStack),

                    (player, itemStack) -> Duration.ofMillis(500),
                    (player, itemStack) -> Vec.fromPoint(player.getPosition().add(0, player.getEyeHeight(), 0)),
                    (player, itemStack) -> player.getPosition().direction().mul(7D)));

    @AllArgsConstructor @Setter
    public static class DropSettings {

        private @Nullable BiFunction<GamePlayer, ItemStack, ItemStack> dropModifier;

        private @NotNull BiFunction<GamePlayer, ItemStack, ItemEntity> itemEntityProvider;

        private @NotNull BiFunction<GamePlayer, ItemStack, Duration> pickupDelayProvider;
        private @NotNull BiFunction<GamePlayer, ItemStack, Vec> positionProvider, velocityProvider;

        public boolean modifiesDrop() {
            return this.dropModifier != null;
        }

        public ItemStack modifyDrop(GamePlayer player, ItemStack itemStack) {
            if(!modifiesDrop()) { return itemStack; }
            return this.dropModifier.apply(player, itemStack);
        }

        public ItemEntity getDropItemEntity(GamePlayer player, ItemStack itemStack) {
            return this.itemEntityProvider.apply(player, itemStack);
        }

        public Duration getDropPickupDelay(GamePlayer player, ItemStack itemStack) {
            return this.pickupDelayProvider.apply(player, itemStack);
        }

        public Vec getDropPosition(GamePlayer player, ItemStack itemStack) {
            return this.positionProvider.apply(player, itemStack);
        }

        public Vec getDropVelocity(GamePlayer player, ItemStack itemStack) {
            return this.velocityProvider.apply(player, itemStack);
        }

    }

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * Keeps items in your inventory whenever you die. <br>
     * Usually is set to false if {@linkplain #DROP_ITEMS_ON_DEATH} is true and vice-versa.
     */
    public static final PlayerRule<@NotNull Boolean> KEEP_INVENTORY = new PlayerRule<>(false);

    /**
     * {@linkplain DeathDropSettings} <br>
     * <br>
     * Drop items whenever you die. <br>
     * <strong>null</strong> value means this rule is disabled.
     */
    public static final PlayerRule<@Nullable DeathDropSettings> DROP_ITEMS_ON_DEATH = new PlayerRule<>(
            new DeathDropSettings(
                    (player, info) -> info,

                    (player, info) -> new ItemEntity(info.getCurrentlyDropping()),

                    (player, info) -> Duration.ofMillis(500),
                    (player, info) -> player.getPosition().asVec(),
                    (player, info) -> new Vec(2 - (Math.random() * 4), 5D, 2 - (Math.random() * 4))));

    @AllArgsConstructor @Setter
    public static class DeathDropSettings {

        private @Nullable BiFunction<GamePlayer, DeathDropInfo, DeathDropInfo> dropInfoModifier;

        private @NotNull BiFunction<GamePlayer, DeathDropInfo, ItemEntity> itemEntityProvider;

        private @NotNull BiFunction<GamePlayer, DeathDropInfo, Duration> pickupDelayProvider;
        private @NotNull BiFunction<GamePlayer, DeathDropInfo, Vec> positionProvider, velocityProvider;

        public boolean modifiesDropInfo() {
            return this.dropInfoModifier != null;
        }

        public DeathDropInfo modifyDropInfo(GamePlayer player, DeathDropInfo info) {
            if(!modifiesDropInfo()) { return info; }
            return this.dropInfoModifier.apply(player, info);
        }

        public ItemEntity getDropItemEntity(GamePlayer player, DeathDropInfo info) {
            return this.itemEntityProvider.apply(player, info);
        }

        public Duration getDropPickupDelay(GamePlayer player, DeathDropInfo info) {
            return this.pickupDelayProvider.apply(player, info);
        }

        public Vec getDropPosition(GamePlayer player, DeathDropInfo info) {
            return this.positionProvider.apply(player, info);
        }

        public Vec getDropVelocity(GamePlayer player, DeathDropInfo info) {
            return this.velocityProvider.apply(player, info);
        }

    }

    @AllArgsConstructor @Getter
    public static class DeathDropInfo {
        @Setter private @NotNull ItemStack currentlyDropping;
        private final Collection<ItemStack> itemsToDrop;
    }

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * Is the respawn screen shown for the player when they die?
     */
    public static final PlayerRule<@NotNull Boolean> RESPAWN_SCREEN = new PlayerRule<>(false, Player::setEnableRespawnScreen);

    /**
     * {@linkplain FlyingSettings} <br>
     * <br>
     * Settings related to flying.
     */
    public static final PlayerRule<@NotNull FlyingSettings> FLYING = new PlayerRule<>(
            new FlyingSettings(false, false, 0.05F),
            (player, settings) -> {
                player.setFlying(settings.isFlying());
                player.setAllowFlying(settings.allowsFlying());
                player.setFlyingSpeed(settings.getFlySpeed());
            });

    @AllArgsConstructor @Setter
    public static class FlyingSettings {
        @Getter private boolean flying;
        private boolean allowFlying;
        @Getter private float flySpeed;

        public boolean allowsFlying() {
            return this.allowFlying;
        }
    }
}
