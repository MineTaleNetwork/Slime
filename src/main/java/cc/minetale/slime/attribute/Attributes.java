package cc.minetale.slime.attribute;

import net.minestom.server.utils.time.TimeUnit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class Attributes {

    private Attributes() {}

    public static final Set<Attribute<?>> ALL_ATTRIBUTES = Collections.synchronizedSet(new HashSet<>());

    /**
     * {@linkplain Integer} (as {@linkplain TimeUnit#SERVER_TICK}) <br>
     * <br>
     * Sets the time in ticks it takes for the player to respawn. <br>
     * By default used in conjunction with {@linkplain Attributes#AUTO_DEATHCAM}, <br>
     * and is immediate/disabled if this is set to 0. <br>
     * Should be used by custom behaviours if {@linkplain Attributes#AUTO_DEATHCAM} is disabled.
     */
    public static final Attribute<Integer> RESPAWN_TIME = new Attribute<>(0);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players will automatically lose lives. <br>
     * Disable if you want to handle lives yourself or don't want to deal with this system.
     */
    public static final Attribute<Boolean> AUTO_LOSE_LIVES = new Attribute<>(true);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with will be a temporary spectator. <br>
     * The time a player will be in this state is determined by {@linkplain Attributes#RESPAWN_TIME}. <br>
     * Otherwise you will have to handle the player after death. <br>
     * See also {@linkplain Attributes#AUTO_SPECTATOR} and {@linkplain Attributes#RESPAWN_TIME}.
     */
    public static final Attribute<Boolean> AUTO_DEATHCAM = new Attribute<>(true);

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with 0 lives left will be a spectator. <br>
     * Otherwise you will have to handle the player after death. <br>
     * See also {@linkplain Attributes#AUTO_DEATHCAM}.
     */
    public static final Attribute<Boolean> AUTO_SPECTATOR = new Attribute<>(true);

}
