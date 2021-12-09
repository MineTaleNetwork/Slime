package cc.minetale.slime.spawn;

import cc.minetale.slime.player.GamePlayer;

@FunctionalInterface
public interface ISpawnStrategy {
    Spawn find(SpawnManager spawnManager, GamePlayer player);
}
