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
import java.util.function.Function;

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
    protected Function<Player, GamePlayer> playerProvider = GamePlayer::new;

    @Getter protected List<GameTeam> teams;

    @Getter protected final SpawnManager spawnManager = new SpawnManager();

    protected Game(int maxPlayers, @NotNull GameState state,
                   @Nullable Function<Player, GamePlayer> playerProvider) {

        this.maxPlayers = maxPlayers;
        this.playerProvider = Objects.requireNonNullElse(playerProvider, this.playerProvider);

        state.setGame(this);
        this.state = state;

        this.mainInstance = INSTANCE_MANAGER.createInstanceContainer();
    }

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    public Instance getSpawnInstance(GamePlayer player) {
        if(this.state.inLobby())
            return this.lobby.getInstance();

        SpawnPoint spawnPoint = this.spawnManager.findSpawnPoint(player);
        player.setCurrentSpawn(spawnPoint);
        return Objects.requireNonNullElse(spawnPoint.getInstance(), this.mainInstance);
    }

    public boolean addPlayer(@NotNull GamePlayer player) {
        if(player.getGame() != null || player.getLobby() != null) { return false; }
        if(!canFitPlayer() || !this.state.inLobby()) { return false; }

        return forceAddPlayer(player);
    }

    public boolean forceAddPlayer(@NotNull GamePlayer player) {
        var event = new GamePlayerJoinEvent(this, player);
        EventDispatcher.call(event);
        if(event.isCancelled()) { return false; }

        if(!this.state.inLobby()) {
            Game otherGame = player.getGame();
            if(otherGame != null)
                otherGame.removePlayer0(player);

            player.setGame(this);
            return this.players.add(player);
        } else {
            return this.lobby.addPlayer(player);
        }
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

    protected GamePlayer createPlayer(Player player) {
        return this.playerProvider.apply(player);
    }

    final void assignTeams() {
        var event = new GameTeamAssignEvent(this, this.players);
        EventDispatcher.call(event);

        this.teams = Collections.synchronizedList(new ArrayList<>(event.getAssigned().keySet()));
    }

    final void remove() {
        this.players.forEach(this::removePlayer);
        this.instances.forEach((key, instance) -> {
            instance.getPlayers().forEach(Player::remove);
            INSTANCE_MANAGER.unregisterInstance(instance);
        });
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