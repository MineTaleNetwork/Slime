package cc.minetale.slime.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.entity.GameMode;

/**
 * The default implementation for the {@linkplain IPlayerState}. <br>
 * If you wish to have additional states, you are free to do so, but you mustn't replace and still use these. <br>
 */
@AllArgsConstructor
public enum PlayerState implements IPlayerState {
    LOBBY(GameMode.ADVENTURE),

    PLAY(GameMode.SURVIVAL),
    SPECTATE(GameMode.ADVENTURE);

    @Getter final GameMode gamemode;
}