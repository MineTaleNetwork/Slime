package cc.minetale.slime.event.team;

import cc.minetale.slime.event.player.GamePlayerJoinEvent;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.event.trait.GameTeamEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This event, unlike {@linkplain GameSetupTeamsEvent} gets called whenever a player joins a team post initial assignment. <br>
 * Almost always when a player joins the game after it has started, after {@linkplain GamePlayerJoinEvent}.
 */
public class GameTeamJoinEvent implements GameEvent, GamePlayerEvent, GameTeamEvent, CancellableEvent {

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
