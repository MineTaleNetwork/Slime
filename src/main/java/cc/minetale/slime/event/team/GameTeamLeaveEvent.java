package cc.minetale.slime.event.team;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class GameTeamLeaveEvent implements PlayerEvent {

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    public GameTeamLeaveEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer) {
        this.game = game;
        this.gamePlayer = gamePlayer;
    }

    @Override public @NotNull Player getPlayer() {
        return this.gamePlayer.getHandle();
    }

}
