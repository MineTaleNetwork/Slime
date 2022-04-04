package cc.minetale.slime.team;

import cc.minetale.mlib.nametag.NameplateProvider;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.SlimeForwardingAudience;
import cc.minetale.slime.core.TeamStyle;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.rule.*;
import cc.minetale.slime.utils.ApplyStrategy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static cc.minetale.slime.Slime.TEAM_MANAGER;

@Getter
public class GameTeam implements SlimeForwardingAudience, IRuleWritable, IRuleReadable {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    /** Can be null if the team is anonymous. Check {@linkplain TeamStyle}. */
    @Setter(AccessLevel.PACKAGE)
    private @Nullable Team handle;

    /** Check {@linkplain TeamStyle}. */
    private boolean anonymous;

    private final String id;

    private final Map<Rule<?>, Object> rules;

    @Setter protected int size;

    /** Can be null if the team is anonymous. Check {@linkplain TeamStyle}. */
    @Setter protected @Nullable ITeamType type;
    /** Can be null if the team is anonymous. Check {@linkplain TeamStyle}. */
    protected @Nullable NameplateProvider nameplateProvider;

    List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    public GameTeam(Game game, String id, int size, @NotNull ITeamType type) {
        this(game, id, size);
        this.anonymous = false;

        this.handle = TEAM_MANAGER.createTeam(
                type.getId(),
                type.getDisplayName(),
                type.getPrefix(),
                type.getColor(),
                type.getSuffix());

        this.type = type;
        this.nameplateProvider = new NameplateProvider(this.handle, ProviderType.SLIME);
    }

    //Anonymous
    public GameTeam(Game game, String id, int size) {
        this.anonymous = true;

        this.game = game;

        this.id = id;
        this.size = size;

        this.rules = Collections.synchronizedMap(new HashMap<>());
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

    //Rules
    @Override
    public <T> void setRule(Rule<T> rule, T value, ApplyStrategy strategy, boolean affectChildren) {
        if(rule instanceof TeamRule) {
            if(strategy == ApplyStrategy.ALWAYS) {
                this.rules.put(rule, value);
            } else if(strategy == ApplyStrategy.NOT_SET) {
                this.rules.putIfAbsent(rule, value);
            }
            return;
        } else if(rule instanceof UniversalRule) {
            if(strategy == ApplyStrategy.ALWAYS) {
                this.rules.put(rule, value);
            } else if(strategy == ApplyStrategy.NOT_SET) {
                this.rules.putIfAbsent(rule, value);
            }
        }

        if(affectChildren)
            this.players.forEach(player -> player.setRule(rule, value, strategy, true));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rule<T>, T> T getRule(R rule) {
        return (T) this.rules.get(rule);
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends SlimeAudience> audiences() {
        return this.players;
    }

}
