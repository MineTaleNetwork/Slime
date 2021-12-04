package cc.minetale.slime.spawn;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.team.GameTeam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Specifies the strategy to use for finding a spawnpoint when spawning a player. <br>
 * This enum contains the default strategies, versatile but could be too simple for certain games <br>
 * in which case they can make their own enum or class implementing {@linkplain cc.minetale.slime.spawn.ISpawnStrategy}. <br>
 * <ul>
 * <li>RANDOM - Selects a spawnpoint randomly</li>
 * <li>ORDERED - Next player that spawns will spawn on the next spawnpoint in order specified by the {@linkplain java.util.List} used in {@linkplain cc.minetale.slime.spawn.SpawnManager}.</li>
 * <li>SAFEST - Selects a spawnpoint that's furthest from any enemy</li>
 * <li>ENDANGERED - Selects a spawnpoint that's closest to any enemy</li>
 * <li>EXPOSED - Selects a spawnpoint that has the least amount of teammates nearby</li>
 * </ul>
 */
public enum DefaultStrategy implements ISpawnStrategy {

    RANDOM, ORDERED, SAFEST, ENDANGERED, EXPOSED;

    private BiFunction<SpawnManager, GamePlayer, SpawnPoint> supplier;

    static {
        var random = new Random();

        RANDOM.supplier = (manager, player) -> {
            var team = player.getGameTeam();

            List<SpawnPoint> spawnPoints = manager.getSpawnPointsFor(team);
            if(spawnPoints.isEmpty()) { return null; }

            var index = random.nextInt(spawnPoints.size());
            return spawnPoints.get(index);
        };

        Map<Game, Map<GameTeam, Integer>> lastSpawnPointIndexes = new HashMap<>();

        ORDERED.supplier = (manager, player) -> {
            synchronized(lastSpawnPointIndexes) {
                var team = player.getGameTeam();

                List<SpawnPoint> spawnPoints = manager.getSpawnPointsFor(team);
                if(spawnPoints.isEmpty()) { return null; }

                var game = manager.getGame();
                Map<GameTeam, Integer> gameIndexes = lastSpawnPointIndexes.getOrDefault(game, new HashMap<>());

                var lastIndex = gameIndexes.getOrDefault(team, 0);
                var index = lastIndex + 1 % spawnPoints.size();

                gameIndexes.put(team, index);

                lastSpawnPointIndexes.putIfAbsent(game, gameIndexes);

                return spawnPoints.get(index);
            }
        };

        //TODO
        SAFEST.supplier = (manager, player) -> { throw new UnsupportedOperationException(); };
        ENDANGERED.supplier = (manager, player) -> { throw new UnsupportedOperationException(); };
        EXPOSED.supplier = (manager, player) -> { throw new UnsupportedOperationException(); };
    }

    @Override
    public SpawnPoint find(SpawnManager spawnManager, GamePlayer player) {
        return this.supplier.apply(spawnManager, player);
    }

}
