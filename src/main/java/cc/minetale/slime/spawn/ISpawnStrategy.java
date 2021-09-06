package cc.minetale.slime.spawn;

import cc.minetale.slime.core.GamePlayer;

@FunctionalInterface
public interface ISpawnStrategy {
    SpawnPoint find(SpawnManager spawnManager, GamePlayer player);
}
