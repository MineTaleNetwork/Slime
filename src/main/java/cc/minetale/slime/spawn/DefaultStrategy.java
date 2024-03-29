package cc.minetale.slime.spawn;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
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

    private BiFunction<SpawnManager, GamePlayer, GameSpawn> supplier;

    static {
        var random = new Random();

        RANDOM.supplier = (manager, player) -> {
            var team = player.getGameTeam();

            List<GameSpawn> spawns = manager.getSpawnsFor(team);
            if(spawns.isEmpty()) { return null; }

            var index = random.nextInt(spawns.size());
            return spawns.get(index);
        };

        Map<Game, Map<GameTeam, Integer>> lastSpawnIndexes = new HashMap<>();

        ORDERED.supplier = (manager, player) -> {
            synchronized(lastSpawnIndexes) {
                var team = player.getGameTeam();

                List<GameSpawn> spawns = manager.getSpawnsFor(team);
                if(spawns.isEmpty()) { return null; }

                var game = manager.getGame();
                Map<GameTeam, Integer> gameIndexes = lastSpawnIndexes.getOrDefault(game, new HashMap<>());

                var lastIndex = gameIndexes.getOrDefault(team, 0);
                var index = lastIndex + 1 % spawns.size();

                gameIndexes.put(team, index);

                lastSpawnIndexes.putIfAbsent(game, gameIndexes);

                return spawns.get(index);
            }
        };

        //TODO
        SAFEST.supplier = (manager, player) -> { throw new UnsupportedOperationException(); };
        ENDANGERED.supplier = (manager, player) -> { throw new UnsupportedOperationException(); };
        EXPOSED.supplier = (manager, player) -> { throw new UnsupportedOperationException(); };
    }

    @Override
    public GameSpawn find(SpawnManager spawnManager, GamePlayer player) {
        return this.supplier.apply(spawnManager, player);
    }

}
