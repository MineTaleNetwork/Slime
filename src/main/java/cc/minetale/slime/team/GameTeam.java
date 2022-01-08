package cc.minetale.slime.team;

import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.SlimeForwardingAudience;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cc.minetale.slime.Slime.TEAM_MANAGER;

@Getter
public class GameTeam implements IAttributeWritable, SlimeForwardingAudience {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Team handle;

    private final String id;

    @Setter protected int size;
    @Setter protected ITeamType type;

    List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    private NameplateProvider nameplateProvider;

    public GameTeam(Game game, String id, int size, ITeamType type) {
        this.game = game;
        this.handle = TEAM_MANAGER.createTeam(
                type.getId(),
                type.getDisplayName(),
                type.getPrefix(),
                type.getColor(),
                type.getSuffix());

        this.id = id;
        this.size = size;
        this.type = type;
        this.nameplateProvider = new NameplateProvider(this.handle, ProviderType.SLIME);
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
            if(!addPlayer(player)) {
                allAdded = false;
            }
        }
        return allAdded;
    }

    boolean canFitPlayers(int amount) {
        return this.players.size() + amount <= this.size;
    }

    //Attributes
    @Override
    public void setAttribute(Attribute attr, Object value) {
        this.players.forEach(player -> player.setAttribute(attr, value));
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends SlimeAudience> audiences() {
        return this.players;
    }

}
