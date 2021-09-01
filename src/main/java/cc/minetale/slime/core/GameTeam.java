package cc.minetale.slime.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class GameTeam<G extends Game<G,P,S>, P extends GamePlayer<P,S,G>, S extends GameState<S,P,G>> {

    @Setter(AccessLevel.PACKAGE)
    private G game;

    @Setter(AccessLevel.PACKAGE)
    private Team handle;

    @Getter @Setter TeamType type;

    @Getter List<P> players = Collections.synchronizedList(new ArrayList<>());

}
