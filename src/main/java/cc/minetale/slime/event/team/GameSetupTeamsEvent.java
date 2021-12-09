package cc.minetale.slime.event.team;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.TeamAssigner;
import cc.minetale.slime.team.TeamManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This event should be listened to and is called during {@linkplain Stage#SETUP}.<br>
 * Its purpose is to create/provide all initial {@linkplain GameTeam}s and assign.
 */
public class GameSetupTeamsEvent implements GameEvent {

    @Getter @NotNull private Game game;

    /** Unmodifiable list of players in the game. */
    @Getter private List<GamePlayer> players;

    /** Teams used in {@linkplain TeamAssigner#assign} and then added to game's {@linkplain TeamManager}. */
    @Getter private List<GameTeam> teams;

    /** Used to initially assign teams, after assigning it gets set in {@linkplain TeamManager}.*/
    @Getter @Setter private TeamAssigner assigner;

    public GameSetupTeamsEvent(@NotNull Game game, @NotNull List<GamePlayer> players, TeamAssigner assigner) {
        this.game = game;
        this.players = Collections.unmodifiableList(players);
        this.teams = new ArrayList<>();
        this.assigner = assigner;
    }

}
