package cc.minetale.slime.core;

import cc.minetale.slime.spawn.SpawnManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public abstract class Game {

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

    protected Supplier<GamePlayer> playerSupplier = GamePlayer::new;

    protected Game(int maxPlayers, @NotNull GameState state,
                   @Nullable Supplier<GamePlayer> playerSupplier) {

        this.maxPlayers = maxPlayers;
        this.playerSupplier = Objects.requireNonNullElse(playerSupplier, this.playerSupplier);

        state.setGame(this);
        this.state = state;
    }

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    GamePlayer createPlayer(Player player) {
        GamePlayer gamePlayer = this.playerSupplier.get();
        gamePlayer.setGame(this);
        gamePlayer.setHandle(player);

        return gamePlayer;
    }

}