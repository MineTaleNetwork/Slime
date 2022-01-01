package cc.minetale.slime.team;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class GameTeam implements IAttributeWritable, ForwardingAudience {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Team handle;

    private String id;

    @Setter int size;
    @Setter ITeamType type;

    List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    public GameTeam(Game game, String id, int size, ITeamType type) {
        this.game = game;
        this.id = id;
        this.size = size;
        this.type = type;
    }

    public boolean addPlayer(GamePlayer player) {
        if(!canFitPlayers(1)) { return false; }
        player.setGameTeam(this);
        this.players.add(player);
        return true;
    }

    public boolean addPlayers(Collection<GamePlayer> players) {
        if(!canFitPlayers(players.size())) { return false; }
        boolean allAdded = true;
        for(var player : players) {
            if(!addPlayer(player)) { allAdded = false; }
        }
        return allAdded;
    }

    boolean canFitPlayers(int amount) {
        return this.size - this.players.size() > amount;
    }

    //Attributes
    @Override
    public void setAttribute(Attribute attr, Object value) {
        this.players.forEach(player -> player.setAttribute(attr, value));
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.players;
    }

}
