package cc.minetale.slime.core;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.event.player.GamePlayerJoinEvent;
import cc.minetale.slime.event.player.GamePlayerLeaveEvent;
import cc.minetale.slime.event.team.GameTeamAssignEvent;
import cc.minetale.slime.spawn.SpawnManager;
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

import static cc.minetale.slime.core.GameLobby.INSTANCE_MANAGER;

public abstract class Game implements IAttributeWritable, TagReadable, TagWritable {

    @Getter private final String identifier = "G#" + RandomStringUtils.randomAlphanumeric(6);

    @Getter @Setter(AccessLevel.PACKAGE)
    protected GameLobby lobby;

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameState state;

    @Getter @Setter(AccessLevel.PACKAGE)
    protected int maxPlayers;

    @Getter @Setter protected IEndCondition endCondition = EndCondition.LAST_ALIVE;

    @Getter protected final Map<String, Instance> instances = new ConcurrentHashMap<>();

    @Getter protected final List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());
    protected Function<Player, GamePlayer> playerSupplier = GamePlayer::new;

    @Getter protected List<GameTeam> teams;

    @Getter protected final SpawnManager spawnManager = new SpawnManager();

    protected Game(int maxPlayers, @NotNull GameState state,
                   @Nullable Function<Player, GamePlayer> playerSupplier) {

        this.maxPlayers = maxPlayers;
        this.playerSupplier = Objects.requireNonNullElse(playerSupplier, this.playerSupplier);

        state.setGame(this);
        this.state = state;
    }

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    public boolean addPlayer(@NotNull GamePlayer player) {
        if(player.getGame() != null || player.getLobby() != null) { return false; }
        if(!canFitPlayer() || !this.state.isJoinable()) { return false; }

        return forceAddPlayer(player);
    }

    public boolean forceAddPlayer(@NotNull GamePlayer player) {
        var event = new GamePlayerJoinEvent(this, player);
        EventDispatcher.call(event);
        if(event.isCancelled()) { return false; }

        player.setGame(this);
        return this.lobby.addPlayer(player);
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getGame() != this || player.getLobby() != this.lobby) { return false; }

        var event = new GamePlayerLeaveEvent(this, player);
        EventDispatcher.call(event);

        return removePlayer0(player);
    }

    private boolean removePlayer0(GamePlayer player) {
        player.setGame(null);
        this.players.remove(player);
        return this.lobby.removePlayer(player);
    }

    protected GamePlayer createPlayer(Player player) {
        GamePlayer gamePlayer = this.playerSupplier.apply(player);
        gamePlayer.setGame(this);

        return gamePlayer;
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