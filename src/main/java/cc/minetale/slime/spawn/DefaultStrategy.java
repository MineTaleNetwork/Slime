package cc.minetale.slime.spawn;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.team.GameTeam;

import java.util.*;
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
            var team = player.getTeam();
            Map<GameTeam, List<SpawnPoint>> spawnPoints = manager.getSpawnPoints();
            List<SpawnPoint> teamSpawnPoints = spawnPoints.get(team);

            var index = random.nextInt(spawnPoints.size());
            return teamSpawnPoints.get(index);
        };

        Map<Game, Map<GameTeam, Integer>> lastSpawnPointIndexes = new HashMap<>();

        ORDERED.supplier = (manager, player) -> {
            synchronized(lastSpawnPointIndexes) {
                var team = player.getTeam();
                Map<GameTeam, List<SpawnPoint>> spawnPoints = manager.getSpawnPoints();
                List<SpawnPoint> teamSpawnPoints = spawnPoints.get(team);

                var game = manager.getGame();
                lastSpawnPointIndexes.putIfAbsent(game, new HashMap<>());
                Map<GameTeam, Integer> gameIndexes = lastSpawnPointIndexes.get(game);

                gameIndexes.putIfAbsent(team, 0);
                var lastIndex = gameIndexes.get(team);
                var index = lastIndex + 1 % teamSpawnPoints.size();

                gameIndexes.put(team, index);
                return teamSpawnPoints.get(index);
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
