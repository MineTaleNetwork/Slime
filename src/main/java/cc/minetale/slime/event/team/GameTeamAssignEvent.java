package cc.minetale.slime.event.team;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.TeamAssigner;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Called whenever a {@linkplain Game} wants to assign {@linkplain GamePlayer}s to {@linkplain GameTeam}s. <br>
 * Should return a {@linkplain Map} retrieved through a {@linkplain TeamAssigner} implementation.
 */
public class GameTeamAssignEvent implements GameEvent {

    @Getter @NotNull private Game game;

    @Getter private List<GamePlayer> players;
    @Getter @Setter private Map<GameTeam, List<GamePlayer>> assigned;

    public GameTeamAssignEvent(@NotNull Game game, @NotNull List<GamePlayer> players) {
        this.game = game;
        this.players = Collections.unmodifiableList(players);
    }

}
