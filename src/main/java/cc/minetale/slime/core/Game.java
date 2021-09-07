package cc.minetale.slime.core;

import cc.minetale.slime.spawn.SpawnManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
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

public abstract class Game implements TagReadable, TagWritable {

    @Getter private final String identifier = "G#" + RandomStringUtils.randomAlphanumeric(6);

    @Getter @Setter(AccessLevel.PACKAGE)
    protected GameLobby lobby;

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameState state;

    @Getter private final SpawnManager spawnManager = new SpawnManager();

    @Getter protected final Map<String, Instance> instances = new ConcurrentHashMap<>();

    @Getter @Setter(AccessLevel.PACKAGE)
    protected int maxPlayers;

    @Getter private final List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    protected Function<Player, GamePlayer> playerSupplier = GamePlayer::new;

    protected Game(int maxPlayers, @NotNull GameState state,
                   @Nullable Function<Player, GamePlayer> playerSupplier) {

        this.maxPlayers = maxPlayers;
        this.playerSupplier = Objects.requireNonNullElse(playerSupplier, this.playerSupplier);

        state.setGame(this);
        this.state = state;
    }

    public boolean addPlayer(@NotNull GamePlayer player) {
        if(player.getGame() != null || player.getLobby() != null) { return false; }
        if(!canFitPlayer() || !this.state.isJoinable()) { return false; }
        player.setGame(this);
        return this.lobby.addPlayer(player);
    }

    public boolean removePlayer(GamePlayer player) {
        if(player.getGame() != this || player.getLobby() != this.lobby) { return false; }
        player.setGame(null);
        this.players.remove(player);
        return this.lobby.removePlayer(player);
    }

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    GamePlayer createPlayer(Player player) {
        GamePlayer gamePlayer = this.playerSupplier.apply(player);
        gamePlayer.setGame(this);

        return gamePlayer;
    }

    //Tags
    private final NBTCompound nbtCompound = new NBTCompound();

    @Override public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(this.nbtCompound);
    }

    @Override public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(this.nbtCompound, value);
    }

}