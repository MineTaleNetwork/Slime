package cc.minetale.slime.game;

import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.core.MainListener;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.SlimeForwardingAudience;
import cc.minetale.slime.event.game.GameCreateEvent;
import cc.minetale.slime.event.game.GameRemoveEvent;
import cc.minetale.slime.lobby.GameLobby;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GameManager implements SlimeForwardingAudience {

    @Getter @Setter private GameExtension extension;

    @Getter private final List<Game> games = Collections.synchronizedList(new ArrayList<>());

    @Getter @Setter private int maxGames;

    private final Supplier<Game> gameProvider;
    private Function<Game, GameLobby> lobbyProvider = GameLobby::new;

    public GameManager(GameExtension extension,
                       int maxGames,
                       @NotNull Supplier<Game> gameProvider,
                       @Nullable Function<Game, GameLobby> lobbyProvider) {

        this.extension = extension;

        this.maxGames = maxGames;

        this.gameProvider = gameProvider;
        this.lobbyProvider = Objects.requireNonNullElse(lobbyProvider, this.lobbyProvider);

        MainListener.registerEvents(this);
    }

    public CompletableFuture<Game> findGameOrCreate() {
        synchronized(this.games) {
            return this.games.stream()
                    .filter(game -> game.getState().inLobby() &&
                            game.canFitPlayer())
                    .findFirst()
                    .map(CompletableFuture::completedFuture)
                    .orElseGet(this::addNewGame);
        }
    }

    public CompletableFuture<Game> addNewGame() {
        if(!canFitNewGame()) { return null; }

        var game = this.gameProvider.get();
        var lobby = this.lobbyProvider.apply(game);

        game.setLobby(lobby);

        var future = new CompletableFuture<Game>();

        CompletableFuture.allOf(lobby.setup(), game.setup())
                .thenRun(() -> {
                    var event = new GameCreateEvent(game);
                    EventDispatcher.call(event);
                    if(event.isCancelled()) {
                        future.complete(null);
                        return;
                    }

                    this.games.add(game);

                    future.complete(game);
                });

        return future;
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

    //Audiences
    @Override
    public @NotNull Iterable<? extends SlimeAudience> audiences() {
        return this.games;
    }

}
