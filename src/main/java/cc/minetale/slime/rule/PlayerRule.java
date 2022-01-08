package cc.minetale.slime.rule;

import cc.minetale.slime.utils.restriction.Blacklist;
import cc.minetale.slime.utils.restriction.RestrictionList;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PlayerRule<T> extends Rule<T> {

    public static final Set<PlayerRule<?>> ALL_RULES = Collections.synchronizedSet(new HashSet<>());

    public PlayerRule(T defaultValue) {
        super(defaultValue);
        ALL_RULES.add(this);
    }

    /**
     * {@linkplain Integer} (as {@linkplain TimeUnit#SERVER_TICK}) <br>
     * <br>
     * Sets the time in ticks it takes for the player to respawn. <br>
     * By default used in conjunction with {@linkplain PlayerRule#AUTO_DEATHCAM}, <br>
     * and is immediate/disabled if this is set to 0. <br>
     * Should be used by custom behaviours if {@linkplain PlayerRule#AUTO_DEATHCAM} is disabled.
     */
    public static final PlayerRule<Integer> RESPAWN_TIME = new PlayerRule<>(0);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players will automatically lose lives. <br>
     * Disable if you want to handle lives yourself or don't want to deal with this system.
     */
    public static final PlayerRule<Boolean> AUTO_LOSE_LIVES = new PlayerRule<>(true);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with will be a temporary spectator. <br>
     * The time a player will be in this state is determined by {@linkplain PlayerRule#RESPAWN_TIME}. <br>
     * Otherwise you will have to handle the player after death. <br>
     * See also {@linkplain PlayerRule#AUTO_SPECTATOR} and {@linkplain PlayerRule#RESPAWN_TIME}.
     */
    public static final PlayerRule<Boolean> AUTO_DEATHCAM = new PlayerRule<>(true);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with 0 lives left will be a spectator. <br>
     * Otherwise you will have to handle the player after death. <br>
     * See also {@linkplain PlayerRule#AUTO_DEATHCAM}.
     */
    public static final PlayerRule<Boolean> AUTO_SPECTATOR = new PlayerRule<>(true);

    //Blocks

    /**
     * {@linkplain RestrictionList}<{@linkplain Block}> <br>
     * <br>
     * You can only place blocks that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<RestrictionList<Block>> PLACE_BLOCKS = new PlayerRule<>(Blacklist.empty());

    /**
     * {@linkplain RestrictionList}<{@linkplain Block}> <br>
     * <br>
     * You can only break blocks that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<RestrictionList<Block>> BREAK_BLOCKS = new PlayerRule<>(Blacklist.empty());

    //PvP & PvE

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, you can't attack other players. Takes priority over {@linkplain TeamRule#ATTACK_ALLIES}.
     */
    public static final PlayerRule<Boolean> ATTACK_PLAYERS = new PlayerRule<>(true);

    /**
     * {@linkplain RestrictionList}<{@linkplain Class}<? extends {@linkplain Entity}>> <br>
     * <br>
     * You can only attack entities that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<RestrictionList<Class<? extends Entity>>> ATTACK_ENTITIES = new PlayerRule<>(Blacklist.empty());

    //Interactions

    /**
     * {@linkplain RestrictionList}<{@linkplain Block}> <br>
     * <br>
     * You can only interact with blocks that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<RestrictionList<Block>> INTERACT_WITH = new PlayerRule<>(Blacklist.empty());

    /**
     * {@linkplain RestrictionList}<{@linkplain Material}> <br>
     * <br>
     * You can only interact with blocks using items that aren't restricted ({@linkplain RestrictionList#isRestricted(Object)}).
     */
    public static final PlayerRule<RestrictionList<Material>> INTERACT_USING = new PlayerRule<>(Blacklist.empty());

    //Movement

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * You can't move.
     */
    public static final PlayerRule<Boolean> FROZEN = new PlayerRule<>(false);

}
