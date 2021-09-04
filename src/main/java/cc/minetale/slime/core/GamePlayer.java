package cc.minetale.slime.core;

import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class GamePlayer {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Player handle;

    @Nullable @Setter(AccessLevel.PACKAGE)
    private GameLobby lobby;

    @Setter protected int lives = 1;
    @Setter protected boolean canLoseLives = true;

    @Setter GameTeam team;

}
