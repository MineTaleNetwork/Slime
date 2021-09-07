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

public final class GameManager {

    @Getter @Setter private int maxGames;
    @Getter @Setter private int maxPlayers;

    @Getter private final List<Game> games = Collections.synchronizedList(new ArrayList<>());

    private final Supplier<Game> gameSupplier;
    private Function<Game, GameLobby> lobbySupplier = GameLobby::new;

    public GameManager(int maxGames, int maxPlayers,
                       @NotNull Supplier<Game> gameSupplier,
                       @Nullable Function<Game, GameLobby> lobbySupplier) {

        this.maxGames = maxGames;
        this.maxPlayers = maxPlayers;

        this.gameSupplier = gameSupplier;
        this.lobbySupplier = Objects.requireNonNullElse(lobbySupplier, this.lobbySupplier);
    }

    public Game findGameOrCreate() {
        return this.games.stream()
                .filter(game -> game.getState().getBaseState() == GameState.State.IN_LOBBY &&
                        game.canFitPlayer())
                .findFirst()
                .orElseGet(this::addNewGame);
    }

    public Game addNewGame() {
        if(!canFitNewGame()) { return null; }
        return addNewGame0();
    }

    private Game addNewGame0() {
        var game = this.gameSupplier.get();

        var lobby = this.lobbySupplier.apply(game);
        game.setLobby(lobby);

        this.games.add(game);
        return game;
    }

    private boolean canFitNewGame() {
        return this.maxGames - this.games.size() > 0;
    }

}
