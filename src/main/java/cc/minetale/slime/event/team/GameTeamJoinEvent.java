package cc.minetale.slime.event.team;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.event.trait.GameTeamEvent;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

public class GameTeamJoinEvent implements GamePlayerEvent, GameTeamEvent, CancellableEvent {

    private boolean cancelled;

    @Getter private Game game;
    @Getter private GamePlayer gamePlayer;

    @Getter @Setter private GameTeam team;

    public GameTeamJoinEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, @NotNull GameTeam team) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.team = team;
    }

    @Override public boolean isCancelled() { return this.cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

}
