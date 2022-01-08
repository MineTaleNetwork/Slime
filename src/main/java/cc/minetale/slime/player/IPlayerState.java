package cc.minetale.slime.player;

import net.minestom.server.entity.GameMode;

public interface IPlayerState {
    GameMode getGamemode();
    boolean showTeam();
}
