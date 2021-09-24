package cc.minetale.slime.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.utils.time.TimeUnit;

@AllArgsConstructor
public enum Attribute {

    /**
     * {@linkplain Integer} (as {@linkplain TimeUnit#SERVER_TICK}) <br>
     * <br>
     * Sets the time in ticks it takes for the player to respawn. <br>
     * By default used in conjunction with {@linkplain Attribute#AUTO_TEMP_SPECTATOR}, <br>
     * and is immediate/disabled if this is set to 0. <br>
     * Should be used by custom behaviours if {@linkplain Attribute#AUTO_TEMP_SPECTATOR} is disabled.
     */
    RESPAWN_TIME(0),

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players will automatically lose lives. <br>
     * Disable if you want to handle lives yourself or don't want to deal with this system.
     */
    AUTO_LOSE_LIVES(true),

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with will be a temporary spectator. <br>
     * See also {@linkplain Attribute#AUTO_PERM_SPECTATOR} and {@linkplain Attribute#RESPAWN_TIME}.
     */
    AUTO_TEMP_SPECTATOR(true),

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * If enabled, players that died with 0 lives left will be a spectator. <br>
     * Otherwise it is in your hands how to handle the player after death.
     * See also {@linkplain Attribute#AUTO_TEMP_SPECTATOR}.
     */
    AUTO_PERM_SPECTATOR(true);

    @Getter private final Object defaultValue;

}
