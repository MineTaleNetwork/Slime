package cc.minetale.slime.core;

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

public abstract class Game<G extends Game<G,P,S>, P extends GamePlayer<P,S,G>, S extends GameState<S,P,G>> {

    @Getter private final String identifier = "G#" + RandomStringUtils.randomAlphanumeric(6);

    @Getter @Setter(AccessLevel.PACKAGE)
    protected GameLobby<G,P,S> lobby;

    @Getter @Setter(AccessLevel.PACKAGE)
    private GameState<S,P,G> state;

    @Getter protected final Map<String, Instance> instances = new ConcurrentHashMap<>();

    @Getter protected int maxPlayers;
    @Getter private final List<P> players = Collections.synchronizedList(new ArrayList<>());

    protected Supplier<P> playerSupplier = () -> new GamePlayer<P,S,G>().get();

    protected Game(int maxPlayers, @NotNull S state,
                   @Nullable Supplier<P> playerSupplier) {

        this.maxPlayers = maxPlayers;
        this.playerSupplier = Objects.requireNonNullElse(playerSupplier, this.playerSupplier);

        state.setGame(get());
        this.state = state;
    }

    protected abstract boolean canJoin(Player player);

    boolean canFitPlayer() {
        return this.maxPlayers - this.players.size() > 0;
    }

    P createPlayer(Player player) {
        P gamePlayer = this.playerSupplier.get();
        gamePlayer.setGame(get());
        gamePlayer.setHandle(player);

        return gamePlayer;
    }

    @SuppressWarnings("unchecked")
    public G get() {
        return (G) this;
    }

}