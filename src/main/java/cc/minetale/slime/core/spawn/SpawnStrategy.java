package cc.minetale.slime.core.spawn;

/**
 * Specifies the strategy to use for finding a spawnpoint when spawning a player. <br>
 * <ul>
 * <li>RANDOM - Selects a spawnpoint randomly</li>
 * <li>ORDERED - Next player that spawns will spawn on the next spawnpoint in order specified by the {@linkplain java.util.List} used in {@linkplain SpawnManager}.</li>
 * <li>SAFEST - Selects a spawnpoint that's furthest from any enemy</li>
 * <li>ENDANGERED - Selects a spawnpoint that's closest to any enemy</li>
 * <li>EXPOSED - Selects a spawnpoint that has the least amount of teammates nearby</li>
 * </ul>
 */
public enum SpawnStrategy {

    RANDOM, ORDERED, SAFEST, ENDANGERED, EXPOSED

}
