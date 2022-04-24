package cc.minetale.slime.game;

import cc.minetale.mlib.nametag.NameplateHandler;
import cc.minetale.mlib.nametag.ProviderType;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.core.MainListener;
import cc.minetale.slime.core.SlimeAudience;
import cc.minetale.slime.core.SlimeForwardingAudience;
import cc.minetale.slime.event.game.GameCreateEvent;
import cc.minetale.slime.event.game.GameRemoveEvent;
import cc.minetale.slime.lobby.GameLobby;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.LobbyMap;
import cc.minetale.slime.map.MapResolver;
import cc.minetale.slime.perceive.PerceiveTeam;
import cc.minetale.slime.utils.GameUtil;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class GameManager implements SlimeForwardingAudience {

    @Getter @Setter private GameInfo gameInfo;

    @Getter private final List<Game> games = Collections.synchronizedList(new ArrayList<>());

    private final Function<GameManager, Game> gameProvider;
    private final Function<Game, GameLobby> lobbyProvider;

    @Getter @Setter private int maxGames;
    @Getter @Setter private int minPlayers;
    @Getter @Setter private int maxPlayers;

    @Getter @Setter private Duration timelimit;

    @Getter @Setter private MapResolverStrategy<GameMap> gameMapResolverStrategy = MapResolverStrategy.random();
    @Getter @Setter private MapResolverStrategy<LobbyMap> lobbyMapResolverStrategy = MapResolverStrategy.random();

    private GameManager(GameInfo gameInfo,
                       @NotNull Function<GameManager, Game> gameProvider,
                       @Nullable Function<Game, GameLobby> lobbyProvider) {

        gameInfo.setGameManager(this);
        this.gameInfo = gameInfo;

        this.gameProvider = gameProvider;
        this.lobbyProvider = Objects.requireNonNullElse(lobbyProvider, GameLobby::new);
    }

    public static GameManager create(GameInfo gameInfo,
                              @NotNull Function<GameManager, Game> gameProvider,
                              @Nullable Function<Game, GameLobby> lobbyProvider) {

        var manager = new GameManager(gameInfo, gameProvider, lobbyProvider);
        MainListener.registerEvents(manager);
        PerceiveTeam.initialize(gameInfo);

        //Remove default NameplateHandlers (replaced by PerceiveTeam to better integrate with Slime)
        NameplateHandler.disableType(ProviderType.RANK);

        GameUtil.setPlayerProvider(gameInfo);

        return manager;
    }

    public CompletableFuture<Game> findGameOrCreate() {
        synchronized(this.games) {
            return this.games.stream()
                    .filter(game -> game.getState().inLobby() && game.canFitPlayer())
                    .findFirst()
                    .map(CompletableFuture::completedFuture)
                    .orElseGet(this::addNewGame);
        }
    }

    public CompletableFuture<Game> addNewGame() {
        if(!canFitNewGame()) { return null; }

        var game = this.gameProvider.apply(this);
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

    public GameMap getGameMap() {
        return this.gameMapResolverStrategy.getMap(this.gameInfo, this.gameInfo.getGameMapResolver());
    }

    public LobbyMap getLobbyMap() {
        return this.lobbyMapResolverStrategy.getMap(this.gameInfo, this.gameInfo.getLobbyMapResolver());
    }

    //Audiences
    @Override
    public @NotNull Iterable<? extends SlimeAudience> audiences() {
        return this.games;
    }

    private interface MapResolverStrategy<T extends AbstractMap> {
        static <T extends AbstractMap> MapResolverStrategy<T> byName(String name) {
            return (info, resolver) -> resolver.fromBoth(info.getId(), name);
        }

        static <T extends AbstractMap> MapResolverStrategy<T> byName(Function<GameInfo, String> nameProvider) {
            return (info, resolver) -> resolver.fromBoth(info.getId(), nameProvider.apply(info));
        }

        static <T extends AbstractMap> MapResolverStrategy<T> random() {
            return (info, resolver) -> resolver.getRandomMap(info.getId());
        }

        @Nullable T getMap(GameInfo info, MapResolver<T> resolver);
    }
}
