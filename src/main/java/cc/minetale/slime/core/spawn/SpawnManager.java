package cc.minetale.slime.core.spawn;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnManager {

    @Getter @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Getter @Setter private SpawnStrategy strategy = SpawnStrategy.RANDOM;

    @Getter Map<GameTeam, List<SpawnPoint>> spawnPoints = new ConcurrentHashMap<>();

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

    //SpawnStrategy specific
    private static final Random RANDOM = new Random();
    @Getter Map<GameTeam, Integer> lastSpawnPointIndexes = new ConcurrentHashMap<>();

    public SpawnPoint findSpawnPoint(GamePlayer player) {
        var team = player.getTeam();
        var spawnPoints = this.spawnPoints.get(team);
        switch(this.strategy) {
            case RANDOM:
                int index = RANDOM.nextInt(spawnPoints.size());
                this.lastSpawnPointIndexes.put(team, index);
                return spawnPoints.get(index);
            case ORDERED:
                index = this.lastSpawnPointIndexes.get(team) + 1 % spawnPoints.size();
                this.lastSpawnPointIndexes.put(team, index);
                return spawnPoints.get(index);
            case SAFEST:
                throw new UnsupportedOperationException(); //TODO
            case ENDANGERED:
                throw new UnsupportedOperationException(); //TODO
            case EXPOSED:
                throw new UnsupportedOperationException(); //TODO
        }
        return null;
    }

}
