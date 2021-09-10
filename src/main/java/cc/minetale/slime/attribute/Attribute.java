package cc.minetale.slime.attribute;

import net.minestom.server.utils.time.TimeUnit;

public enum Attribute {

    /**
     * {@linkplain Integer} (as {@linkplain TimeUnit#SERVER_TICK}) <br>
     * <br>
     * Sets the time in ticks it takes for the player to respawn. <br>
     * By default used in conjunction with {@linkplain Attribute#AUTO_SPECTATOR}, <br>
     * and is tre
     * and should be used by custom behaviours if {@linkplain Attribute#AUTO_SPECTATOR} is disabled.
     */
    RESPAWN_TIME,

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players will automatically lose lives. <br>
     * Disable if you want to handle lives yourself or don't want to deal with this system.
     */
    AUTO_LOSE_LIVES,

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with will be a temporary spectator. <br>
     * See also {@linkplain Attribute#AUTO_DEATH_SPECTATOR} and {@linkplain Attribute#RESPAWN_TIME}.
     */
    AUTO_SPECTATOR,

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with 0 lives left will be a spectator. <br>
     * Otherwise it is in your hands how to handle the player after death.
     * See also {@linkplain Attribute#AUTO_SPECTATOR}.
     */
    AUTO_DEATH_SPECTATOR

}
