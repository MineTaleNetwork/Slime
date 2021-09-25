package cc.minetale.slime.spawn;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public final class SpawnManager {

    @Getter @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Getter @Setter private ISpawnStrategy strategy = DefaultStrategy.RANDOM;

    @Getter private Map<GameTeam, List<SpawnPoint>> spawnPoints = Collections.synchronizedMap(new HashMap<>());

    public List<SpawnPoint> getSpawnPointsFor(GameTeam team) {
        return this.spawnPoints.computeIfAbsent(team, key -> Collections.synchronizedList(new ArrayList<>()));
    }

    public void addSpawnPoint(GameTeam team, SpawnPoint spawnpoint) {
        spawnpoint.getOwners().add(team);

        this.spawnPoints.compute(team, (key, value) -> {
            List<SpawnPoint> spawnPoints = Objects.requireNonNullElse(value, new ArrayList<>());
            spawnPoints.add(spawnpoint);

            return spawnPoints;
        });
    }

    public void removeSpawnPoint(SpawnPoint spawnpoint) {
        Set<GameTeam> owners = spawnpoint.getOwners();
        for(var owner : owners) {
            List<SpawnPoint> spawnPoints = this.spawnPoints.get(owner);
            if(spawnPoints == null) { continue; }

            spawnPoints.remove(spawnpoint);
        }
    }

    public SpawnPoint findSpawnPoint(GamePlayer player) {
        return this.strategy.find(this, player);
    }

}
