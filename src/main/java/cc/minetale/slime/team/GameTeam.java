package cc.minetale.slime.team;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class GameTeam {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Team handle;

    @Getter @Setter int size;
    @Getter @Setter TeamType type;

    @Getter List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    public boolean addPlayers(Collection<GamePlayer> players) {
        if(!canFitPlayers(players.size())) { return false; }
        boolean allAdded = true;
        for(var player : players) {
            if(!addPlayer(player)) { allAdded = false; }
        }
        return allAdded;
    }

    public boolean addPlayer(GamePlayer player) {
        if(!canFitPlayers(1)) { return false; }
        player.setTeam(this);
        this.players.add(player);
        return true;
    }

    boolean canFitPlayers(int amount) {
        return this.size - this.players.size() > amount;
    }

}
