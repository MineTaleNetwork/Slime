package cc.minetale.slime.spawn;

import cc.minetale.slime.player.GamePlayer;

@FunctionalInterface
public interface ISpawnStrategy {
    GameSpawn find(SpawnManager spawnManager, GamePlayer player);
}
