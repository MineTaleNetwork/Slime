package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.condition.EndCondition;
import cc.minetale.slime.condition.IEndCondition;
import cc.minetale.slime.event.player.GamePlayerJoinEvent;
import cc.minetale.slime.event.player.GamePlayerLeaveEvent;
import cc.minetale.slime.event.player.GamePlayerSpawnEvent;
import cc.minetale.slime.event.team.GameTeamAssignEvent;
import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.spawn.SpawnManager;
import cc.minetale.slime.spawn.SpawnPoint;
import cc.minetale.slime.state.BaseState;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

public abstract class Game implements IAttributeWritable, ForwardingAudience {

    @Getter private final String identifier = "G#" + RandomStringUtils.randomAlphanumeric(6);

    @Getter @Setter(AccessLevel.PACKAGE)
    protected GameLobby lobby;

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameState state;

    @Getter @Setter protected IEndCondition endCondition = EndCondition.LAST_ALIVE;

    @Getter @Setter protected Instance mainInstance;
    @Getter protected final Map<String, Instance> instances = new ConcurrentHashMap<>();

    @Getter protected final List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    @Getter protected List<GameTeam> teams;

    @Getter protected final SpawnManager spawnManager = new SpawnManager();

    protected Game(@NotNull GameState state) {
        state.setGame(this);
        this.state = state;

        this.mainInstance = INSTANCE_MANAGER.createInstanceContainer();
    }

    /**
     * Called after the countdown ends during {@linkplain BaseState#STARTING} while in the lobby.
     */
    public void start() {
        this.players.addAll(this.lobby.players);

        //Assign players to their teams
        var teamAssignEvent = new GameTeamAssignEvent(this, this.players);
        EventDispatcher.call(teamAssignEvent);

        assignTeams();

        this.players.forEach(player -> {
            var spawnEvent = new GamePlayerSpawnEvent(this, player, this.spawnManager.findSpawnPoint(player));
            EventDispatcher.call(spawnEvent);

            var spawnPoint = spawnEvent.getSpawnPoint();
            if(spawnPoint == null)
                throw new NullPointerException("Couldn't find a spawnpoint for GamePlayer \"" +
                        player.getUsername() + "\" with the \"" + player.getGameTeam().getType().getId() + "\" GameTeam.");

            Loadout.removeIfAny(player);

            player.setCurrentSpawn(spawnPoint);
            player.setInstance(spawnPoint.getInstance(), spawnPoint.getPosition());
        });

        this.lobby.remove();
        this.lobby = null;

        this.state.setBaseState(BaseState.PRE_GAME);
    }

    boolean canFitPlayer() {
        return Slime.getActiveGame().getMaxPlayers() - this.players.size() > 0;
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

        SpawnPoint spawnPoint = this.spawnManager.findSpawnPoint(player);
        player.setCurrentSpawn(spawnPoint);

        return Objects.requireNonNullElse(spawnPoint.getInstance(), this.mainInstance);
    }

    final void assignTeams() {
        var event = new GameTeamAssignEvent(this, this.players);
        EventDispatcher.call(event);

        Map<GameTeam, Set<GamePlayer>> assignedTeams = event.getAssigned();
        if(assignedTeams == null)
            throw new NullPointerException("Assigned teams is null. Set them through GameTeamAssignEvent and use TeamAssigner or set an empty Map for no teams.");

        assignedTeams.forEach(GameTeam::addPlayers);
        this.teams = Collections.synchronizedList(new ArrayList<>(assignedTeams.keySet()));
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

    //Attributes
    @Override
    public final void setAttribute(Attribute attr, Object value) {
        this.teams.forEach(team -> team.setAttribute(attr, value));
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return !this.state.inLobby() ? this.teams : this.lobby.audiences();
    }
}