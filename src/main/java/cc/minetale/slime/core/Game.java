package cc.minetale.slime.core;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.event.player.GamePlayerJoinEvent;
import cc.minetale.slime.event.player.GamePlayerLeaveEvent;
import cc.minetale.slime.event.team.GameTeamAssignEvent;
import cc.minetale.slime.spawn.SpawnManager;
import cc.minetale.slime.spawn.SpawnPoint;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

public abstract class Game implements IAttributeWritable, TagReadable, TagWritable {

    @Getter private final String identifier = "G#" + RandomStringUtils.randomAlphanumeric(6);

    @Getter @Setter(AccessLevel.PACKAGE)
    protected GameLobby lobby;

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameState state;

    @Getter @Setter(AccessLevel.PACKAGE)
    protected int maxPlayers;

    @Getter @Setter protected IEndCondition endCondition = EndCondition.LAST_ALIVE;

    @Getter @Setter protected Instance mainInstance;
    @Getter protected final Map<String, Instance> instances = new ConcurrentHashMap<>();

    @Getter protected final List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    @Getter protected List<GameTeam> teams;

    @Getter protected final SpawnManager spawnManager = new SpawnManager();

    protected Game(int maxPlayers, @NotNull GameState state) {

        this.maxPlayers = maxPlayers;

        state.setGame(this);
        this.state = state;

        this.mainInstance = INSTANCE_MANAGER.createInstanceContainer();
    }

    /**
     * Called after the countdown ends during {@linkplain BaseState#STARTING} while in the lobby.
     */
    public void start() {
        this.players.addAll(this.lobby.players);
        this.lobby.remove();
        this.lobby = null;

        //TODO Assign teams
        //TODO Respawn using spawnpoints

        this.state.setBaseState(BaseState.PRE_GAME);
    }

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    /**
     * Adds a {@linkplain GamePlayer} to this game (or lobby if still not started) and there is enough space.
     * @param player The player to add
     * @return If successfully added the player
     */
    public boolean addPlayer(@NotNull GamePlayer player) {
        if(player.getGame() != null || player.getLobby() != null) { return false; }
        if(!canFitPlayer() || !this.state.inLobby()) { return false; }

        return forceAddPlayer(player);
    }

    /**
     * Adds a {@linkplain GamePlayer} to this game (or lobby if still not started) ignoring the size limit. <br>
     * See also {@linkplain Game#addPlayer(GamePlayer)} if you want to add a player only if there's enough space.
     * @param player The player to add
     * @return If successfully added the player
     */
    public boolean forceAddPlayer(@NotNull GamePlayer player) {
        var event = new GamePlayerJoinEvent(this, player);
        EventDispatcher.call(event);
        if(event.isCancelled()) { return false; }

        if(!this.state.inLobby()) {
            return forceAddPlayer0(player);
        } else {
            return this.lobby.addPlayer(player);
        }
    }

    /**
     * Adds a {@linkplain GamePlayer} to this game ignoring all checks. <br>
     * See also {@linkplain Game#addPlayer(GamePlayer)} or {@linkplain Game#forceAddPlayer(GamePlayer)}.
     * @param player The player to add
     * @return If successfully added the player
     */
    boolean forceAddPlayer0(@NotNull GamePlayer player) {
        Game otherGame = player.getGame();
        if(otherGame != null)
            otherGame.removePlayer0(player);

        player.setGame(this);
        return this.players.add(player);
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getGame() != this || player.getLobby() != this.lobby) { return false; }

        var event = new GamePlayerLeaveEvent(this, player);
        EventDispatcher.call(event);

        return removePlayer0(player);
    }

    private boolean removePlayer0(GamePlayer player) {
        player.setGame(null);
        if(!this.state.inLobby()) {
            return this.players.remove(player);
        } else {
            return this.lobby.removePlayer(player);
        }
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

        this.teams = Collections.synchronizedList(new ArrayList<>(event.getAssigned().keySet()));
    }

    final void remove() {
        this.players.forEach(this::removePlayer);
        unregisterInstance(this.mainInstance);
        this.instances.forEach((key, instance) -> {
            unregisterInstance(instance);
        });
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

    //Tags
    private final NBTCompound nbtCompound = new NBTCompound();

    @Override
    public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(this.nbtCompound);
    }

    @Override
    public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(this.nbtCompound, value);
    }

}