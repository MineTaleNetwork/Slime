package cc.minetale.slime.core;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GameManager<G extends Game<G,P,S>, P extends GamePlayer<P,S,G>, S extends GameState<S,P,G>> {

    @Getter @Setter private int maxGames;

    @Getter private final List<G> games = Collections.synchronizedList(new ArrayList<>());
    private final Supplier<G> gameSupplier;
    private final Function<G, S> stateSupplier;
    private Supplier<? extends GameLobby<G,P,S>> lobbySupplier = GameLobby::new;

    public GameManager(int maxGames,
                       @NotNull Supplier<G> gameSupplier, @NotNull Function<G, S> stateSupplier,
                       @Nullable Supplier<? extends GameLobby<G,P,S>> lobbySupplier) {

        this.maxGames = maxGames;

        this.gameSupplier = gameSupplier;
        this.stateSupplier = stateSupplier;

        this.lobbySupplier = Objects.requireNonNullElse(lobbySupplier, this.lobbySupplier);
    }

    public G findGameOrCreate() {
        return this.games.stream()
                .filter(game -> game.getState().getBaseState() == GameState.State.IN_LOBBY &&
                        game.canFitPlayer())
                .findFirst()
                .orElseGet(this::addNewGame);
    }

    public G addNewGame() {
        if(!canFitNewGame()) { return null; }
        return addNewGame0();
    }

    private G addNewGame0() {
        G game = this.gameSupplier.get();
        S state = this.stateSupplier.apply(game);

        var lobby = this.lobbySupplier.get();

        game.setState(state);
        game.setLobby(lobby);

        this.games.add(game);
        return game;
    }

    private boolean canFitNewGame() {
        return this.maxGames - this.games.size() > 0;
    }

}
