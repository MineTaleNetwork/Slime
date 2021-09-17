package cc.minetale.slime.core;

import cc.minetale.slime.event.game.GameCreateEvent;
import cc.minetale.slime.event.game.GameRemoveEvent;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;
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

    private final Supplier<Game> gameProvider;
    private Function<Game, GameLobby> lobbyProvider = GameLobby::new;

    public GameManager(int maxGames, int maxPlayers,
                       @NotNull Supplier<Game> gameProvider,
                       @Nullable Function<Game, GameLobby> lobbyProvider) {

        this.maxGames = maxGames;
        this.maxPlayers = maxPlayers;

        this.gameProvider = gameProvider;
        this.lobbyProvider = Objects.requireNonNullElse(lobbyProvider, this.lobbyProvider);

        MainListener.registerEvents(this);
    }

    public Game findGameOrCreate() {
        return this.games.stream()
                .filter(game -> game.getState().getBaseState() == BaseState.IN_LOBBY &&
                        game.canFitPlayer())
                .findFirst()
                .orElseGet(this::addNewGame);
    }

    public Game addNewGame() {
        if(!canFitNewGame()) { return null; }

        var game = this.gameProvider.get();
        var lobby = this.lobbyProvider.apply(game);

        game.setLobby(lobby);

        var event = new GameCreateEvent(game);
        EventDispatcher.call(event);
        if(event.isCancelled()) { return null; }

        this.games.add(game);

        return game;
    }

    public Game removeGame(Game game) {
        var event = new GameRemoveEvent(game);
        EventDispatcher.call(event);

        game.remove();
        this.games.remove(game);

        return game;
    }

    private boolean canFitNewGame() {
        return this.maxGames - this.games.size() > 0;
    }

}
