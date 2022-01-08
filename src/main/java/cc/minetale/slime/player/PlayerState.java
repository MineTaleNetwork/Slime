package cc.minetale.slime.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minestom.server.entity.GameMode;

/**
 * The default implementation for the {@linkplain IPlayerState}. <br>
 * If you wish to have additional states, you are free to do so, but you mustn't replace and still use these. <br>
 */
@AllArgsConstructor
public enum PlayerState implements IPlayerState {
    LOBBY(GameMode.ADVENTURE, false),

    PLAY(GameMode.SURVIVAL, true),
    DEATHCAM(GameMode.ADVENTURE, true),
    SPECTATE(GameMode.ADVENTURE, true);

    @Getter final GameMode gamemode;

    @Getter @Accessors(fluent = true)
    final boolean showTeam;
}