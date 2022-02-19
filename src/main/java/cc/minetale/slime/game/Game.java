package cc.minetale.slime.game;

import cc.minetale.slime.condition.EndCondition;
import cc.minetale.slime.condition.IEndCondition;
import cc.minetale.slime.core.GameState;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.SlimeForwardingAudience;
import cc.minetale.slime.event.game.PostGameSetupEvent;
import cc.minetale.slime.event.game.PreGameSetupEvent;
import cc.minetale.slime.event.player.GamePlayerJoinEvent;
import cc.minetale.slime.event.player.GamePlayerLeaveEvent;
import cc.minetale.slime.event.player.GamePlayerSpawnEvent;
import cc.minetale.slime.event.team.GameSetupTeamsEvent;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.lobby.GameLobby;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.rule.*;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.spawn.SpawnManager;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.TeamManager;
import cc.minetale.slime.utils.ApplyStrategy;
import cc.minetale.slime.utils.TeamUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

public class Game implements SlimeForwardingAudience, IRuleWritable, IRuleReadable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    @Getter private final String identifier = "G#" + RandomStringUtils.randomAlphanumeric(6);

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameManager gameManager; //Parent manager

    @Getter @Setter(AccessLevel.PACKAGE)
    protected GameLobby lobby;

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameState state;

    private final Map<Rule<?>, Object> rules = Collections.synchronizedMap(new HashMap<>());

    @Getter @Setter protected IEndCondition endCondition = EndCondition.LAST_ALIVE;

    @Getter @Setter protected GameInstance mainInstance;
    @Getter protected final Map<String, GameInstance> instances = new ConcurrentHashMap<>();

    @Getter protected final List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());
    @Getter protected int minPlayers;
    @Getter protected int maxPlayers;

    @Getter protected Duration timelimit;

    @Getter protected final SpawnManager spawnManager = new SpawnManager();
    @Getter protected final TeamManager teamManager = new TeamManager();

    public Game(@NotNull GameManager gameManager, @NotNull GameState state) {
        //Copy GameManager's settings
        this.minPlayers = gameManager.getMinPlayers();
        this.maxPlayers = gameManager.getMaxPlayers();

        this.timelimit = gameManager.getTimelimit();

        this.gameManager = gameManager;

        //Setup state
        state.setGame(this);
        this.state = state;
    }

    public CompletableFuture<Void> setup() {
        var map = this.gameManager.getGameMap();
        this.mainInstance = new GameInstance(map);

        var preEvent = new PreGameSetupEvent(this, new ArrayList<>(map.getSpawns().values()));
        EventDispatcher.call(preEvent);

        List<GameSpawn> spawns = preEvent.getGameSpawns();
        if(spawns.isEmpty())
            LOGGER.warn("There aren't any spawns converted. Convert MapSpawns to GameSpawns through GameSetupEvent. Expect issues.");

        this.spawnManager.addSpawns(spawns);

        return this.mainInstance.setMap(map)
                .thenRun(() -> {
                    var postEvent = new PostGameSetupEvent(this, this.mainInstance);
                    EventDispatcher.call(postEvent);
                });
    }

    /**
     * Called after the countdown ends during {@linkplain Stage#STARTING} while in the lobby.
     */
    public void start() {
        //Assign players to their teams
        setupTeams();

        synchronized(this.players) {
            this.players.forEach(player -> {
                var team = player.getGameTeam();
                if(team == null) {
                    LOGGER.warn("Player \"" + player.getUsername() + "\" doesn't have an assigned team.");
                    player.kick("Something went wrong. Please contact an administrator.");
                    return;
                }

                var event = new GamePlayerSpawnEvent(this, player, this.spawnManager.findSpawn(player));
                EventDispatcher.call(event);

                var spawn = event.getSpawn();
                if(spawn == null) {
                    LOGGER.warn("Couldn't find a spawn for player \"" +
                            player.getUsername() + "\" with the \"" + team.getType().getId() + "\" GameTeam.");
                    player.kick("Something went wrong. Please contact an administrator.");
                    return;
                }

                Loadout.removeIfAny(player);

                player.setCurrentSpawn(spawn);
                player.setInstance(spawn.getInstance(), spawn.getPosition());
            });
        }

        this.lobby.remove();
        this.lobby = null;

        this.state.setStage(Stage.PRE_GAME);
    }

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    /**
     * Adds a {@linkplain GamePlayer} to this game (and lobby if still not started) and there is enough space.
     * @param player The player to add
     * @return If successfully added the player
     */
    public boolean addPlayer(@NotNull GamePlayer player) {
        if(player.getGame() != null || player.getLobby() != null) { return false; }
        if(!canFitPlayer() || !this.state.inLobby()) { return false; }

        return forceAddPlayer(player);
    }

    /**
     * Adds a {@linkplain GamePlayer} to this game (and lobby if still not started) ignoring the size limit. <br>
     * See also {@linkplain Game#addPlayer(GamePlayer)} if you want to add a player only if there's enough space.
     * @param player The player to add
     * @return If successfully added the player
     */
    public boolean forceAddPlayer(@NotNull GamePlayer player) {
        var event = new GamePlayerJoinEvent(this, player);
        EventDispatcher.call(event);
        if(event.isCancelled()) { return false; }

        return forceAddPlayer0(player);
    }

    /**
     * Adds a {@linkplain GamePlayer} to this game (and lobby if still not started) ignoring all checks. <br>
     * See also {@linkplain Game#addPlayer(GamePlayer)} or {@linkplain Game#forceAddPlayer(GamePlayer)}.
     * @param player The player to add
     * @return If successfully added the player
     */
    boolean forceAddPlayer0(@NotNull GamePlayer player) {
        Game otherGame = player.getGame();
        if(otherGame != null)
            otherGame.removePlayer0(player);

        player.setGame(this);

        var passed = true;
        if(this.state.inLobby())
            passed = this.lobby.addPlayer(player);

        if(!this.players.contains(player)) {
            this.players.add(player);
        } else {
            return false;
        }

        return passed;
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getGame() != this || player.getLobby() != this.lobby) { return false; }

        var event = new GamePlayerLeaveEvent(this, player);
        EventDispatcher.call(event);

        return removePlayer0(player);
    }

    private boolean removePlayer0(GamePlayer player) {
        player.setGame(null);

        var passed = true;
        if(this.state.inLobby())
            passed = this.lobby.removePlayer(player);

        return this.players.remove(player) && passed;
    }

    public boolean isPlayerInGame(GamePlayer player) {
        return this.players.contains(player) || this.lobby.isPlayerInLobby(player);
    }

    public Instance getSpawnInstance(GamePlayer player) {
        if(this.state.inLobby())
            return this.lobby.getInstance();

        GameSpawn spawn = this.spawnManager.findSpawn(player);
        player.setCurrentSpawn(spawn);

        return Objects.requireNonNullElse(spawn.getInstance(), this.mainInstance);
    }

    /**
     * Sets up teams and everything related.
     */
    final void setupTeams() {
        var event = new GameSetupTeamsEvent(this, this.players, this.teamManager.getAssigner());
        EventDispatcher.call(event);

        var assigner = event.getAssigner();
        if(assigner == null)
            throw new NullPointerException("Assigned teams is null. Set them through GameTeamAssignEvent and use GameSetupTeamsEvent.");

        var teams = event.getTeams();
        if(teams.isEmpty())
            throw new IllegalStateException("Assigned teams is empty. Please provide at least one team to assign players to on GameSetupTeamsEvent.");

        //TODO Move assignment to TeamManager
        Map<GameTeam, Set<GamePlayer>> assignedTeams = assigner.assign(this, teams, players);
        if(assignedTeams == null || assignedTeams.isEmpty())
            throw new IllegalStateException("Assigned teams is null or empty. Make sure the TeamAssigner provided is functioning correctly.");

        this.teamManager.addTeams(assignedTeams.keySet());
        this.teamManager.setAssigner(assigner);

        TeamUtil.assignPlayers(assignedTeams);
    }

    final void remove() {
        this.players.forEach(this::removePlayer);
        unregisterInstance(this.mainInstance);
        this.instances.forEach((key, instance) -> unregisterInstance(instance));
    }

    private static void unregisterInstance(Instance instance) {
        instance.getPlayers().forEach(Player::remove);
        INSTANCE_MANAGER.unregisterInstance(instance);
    }

    //Rules
    @Override
    public <T> void setRule(Rule<T> rule, T value, ApplyStrategy strategy, boolean affectChildren) {
        if(rule instanceof GameRule) {
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
            this.teamManager.getTeams().forEach((id, team) -> team.setRule(rule, value, strategy, true));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rule<T>, T> T getRule(R rule) {
        return (T) this.rules.get(rule);
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends SlimeAudience> audiences() {
        return !this.state.inLobby() ? this.teamManager.getTeams().values() : this.lobby.audiences();
    }
}