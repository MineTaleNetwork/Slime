package cc.minetale.slime.perceive;

import cc.minetale.commonlib.grant.Rank;
import cc.minetale.mlib.util.TeamUtil;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static cc.minetale.slime.Slime.TEAM_MANAGER;

/**
 * Used to override how players based on different circumstances see other players' teams. <br>
 * Called perceive teams because the other player is in reality in some team, <br>
 * but has different teams (is "perceived" differently by players) for someone (or multiple players). <br>
 * <br>
 * Act of perceiving means that a player <i>"perceiving"</i> sees other player as the {@linkplain PerceiveTeam}, not their actual team. <br>
 * <br>
 * Perceive teams only work if the {@linkplain PlayerState}.showTeam is <strong>TRUE</strong> <br>
 * (or <strong>NOT_SET</strong> and previous state had it set to <strong>TRUE</strong>). <br>
 * <br>
 * Examples of perceive teams include:
 * <ul>
 *     <li>
 *         Spectators seeing other spectators as well... spectators and not their in their own team (which they'll still be in after dying)
 *     </li>
 *
 *     <li>
 *         Players seeing other players as the role they're in (e.g. Medic, Support, Assault, Tank...)
 *     </li>
 * </ul>
 */

public class PerceiveTeam {

    public static final List<PerceiveTeam> REGISTERED = Collections.synchronizedList(new ArrayList<>());

    // Default Perceive Teams

    //Default rank teams
    @Getter private static Map<Rank, PerceiveTeam> ranks;

    //Default anonymous teams
    @Getter private static PerceiveTeam anonymousSelf;
    @Getter private static PerceiveTeam anonymousOthers;

    public static final PerceiveTeam SPECTATOR = new PerceiveTeam("spectator", NamedTextColor.GRAY, Component.empty(), Component.empty());

    /**
     * Viewer -> Target that the viewer perceives as if in this team <br>
     * Possibility of this team being actually perceived by the viewer isn't guaranteed. <br>
     * Check {@linkplain IDelusory#targetPossiblyPerceivedAs(GamePlayer)} and {@linkplain IDelusory#targetPerceivedAs(GamePlayer)}
     */
    public final Map<GamePlayer, Set<GamePlayer>> perceivedPlayers = Collections.synchronizedMap(new HashMap<>());

    //TODO Is it even needed when we have handle?
    @Getter private String name;
    @Getter private NamedTextColor color;
    @Getter private Component prefix;
    @Getter private Component suffix;

    @Getter private @Nullable Team handle;

    public PerceiveTeam(String name, NamedTextColor color, Component prefix, Component suffix) {
        this.name = name;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public PerceiveTeam(Team team) {
        this.name = team.getTeamName();
        this.color = team.getTeamColor();
        this.prefix = team.getPrefix();
        this.suffix = team.getSuffix();

        this.handle = team;
    }

    @ApiStatus.Internal
    public static void initialize(GameInfo info) {
        //Rank teams
        ranks = new EnumMap<>(Rank.class);
        for(var ent : TeamUtil.RANK_MAP.entrySet()) {
            var rank = ent.getKey();
            var team = ent.getValue();

            var perceiveTeam = new PerceiveTeam(team);
            perceiveTeam.register();

            ranks.put(rank, perceiveTeam);
        }
        ranks = Collections.synchronizedMap(ranks);

        //Anonymous teams
        anonymousSelf = new PerceiveTeam("anonymous-self",
                info.getAnonymousSelfColor(), info.getAnonymousSelfPrefix(), info.getAnonymousSelfSuffix());
        anonymousSelf.register();

        anonymousOthers = new PerceiveTeam("anonymous-others",
                info.getAnonymousOthersColor(), info.getAnonymousOthersPrefix(), info.getAnonymousOthersSuffix());
        anonymousOthers.register();

        SPECTATOR.register();
    }

    public static PerceiveTeam getRankTeamFor(GamePlayer player) {
        var profile = player.getProfile();
        var grant = profile.getGrant();
        var rank = grant.getRank();

        return ranks.get(rank);
    }

    public void register() {
        //Don't recreate the handle if it was already created
        if(this.handle == null)
            this.handle = TEAM_MANAGER.createTeam(this.name, this.prefix, this.color, this.suffix);

        REGISTERED.add(this);
    }

    /** Returns all players that the viewer sees as if in this team. */
    public Set<GamePlayer> getCurrentlyPerceivedBy(GamePlayer viewer) {
        return this.perceivedPlayers.get(viewer);
    }

    /** Use {@linkplain IDelusory}. */
    @ApiStatus.Internal
    public void add(GamePlayer viewer, GamePlayer target) {
        this.perceivedPlayers.compute(viewer, (key, value) -> {
            value = Objects.requireNonNullElse(value, Collections.synchronizedSet(new HashSet<>()));
            if(value.add(target)) {
                var packet = getAddEntitiesToTeamPacket(Set.of(target));
                viewer.sendPacket(packet);
            }

            return value;
        });
    }

    /** Use {@linkplain IDelusory}. */
    @ApiStatus.Internal
    public void addAll(GamePlayer viewer, Collection<GamePlayer> targets) {
        for(var target : targets) {
            add(viewer, target);
        }
    }

    /** Use {@linkplain IDelusory}. */
    @ApiStatus.Internal
    public void remove(GamePlayer viewer, GamePlayer target) {
        this.perceivedPlayers.compute(viewer, (key, value) -> {
            if(value == null) { return null; }

            if(value.remove(target)) {
                var packet = getRemoveEntitiesToTeamPacket(Set.of(target));
                viewer.sendPacket(packet);
            }

            if(!value.isEmpty()) {
                return value;
            } else {
                return null;
            }
        });
    }

    /** Use {@linkplain IDelusory}. */
    @ApiStatus.Internal
    public void removeAll(GamePlayer viewer, Collection<GamePlayer> targets) {
        for(var target : targets) {
            remove(viewer, target);
        }
    }

    @ApiStatus.Internal
    public void removeAll(GamePlayer viewer) {
        for(var target : this.perceivedPlayers.get(viewer)) {
            remove(viewer, target);
        }
    }

    public TeamsPacket getTeamCreationPacket() {
        return this.handle.createTeamsCreationPacket();
    }

    public TeamsPacket getAddEntitiesToTeamPacket(Collection<GamePlayer> targets) {
        Collection<String> members = new LinkedList<>();
        for(var target : targets) {
            members.add(target.getUsername());
        }
        return new TeamsPacket(this.name, new TeamsPacket.AddEntitiesToTeamAction(members));
    }

    public TeamsPacket getAddEntitiesToTeamPacket(GamePlayer viewer) {
        return getAddEntitiesToTeamPacket(this.perceivedPlayers.get(viewer));
    }

    public TeamsPacket getRemoveEntitiesToTeamPacket(Collection<GamePlayer> targets) {
        Collection<String> members = new LinkedList<>();
        for(var target : targets) {
            members.add(target.getUsername());
        }
        return new TeamsPacket(this.name, new TeamsPacket.RemoveEntitiesToTeamAction(members.toArray(new String[0])));
    }

    public TeamsPacket getRemoveEntitiesToTeamPacket(GamePlayer viewer) {
        return getRemoveEntitiesToTeamPacket(this.perceivedPlayers.get(viewer));
    }

}
