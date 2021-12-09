package cc.minetale.slime.spawn;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public final class SpawnManager {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter private ISpawnStrategy strategy = DefaultStrategy.RANDOM;

    private Map<GameTeam, List<Spawn>> spawns = Collections.synchronizedMap(new HashMap<>());

    public List<Spawn> getSpawnsFor(GameTeam team) {
        return this.spawns.computeIfAbsent(team, key -> Collections.synchronizedList(new ArrayList<>()));
    }

    public void addSpawn(Spawn spawn) {
        Set<GameTeam> owners = spawn.getOwners();
        for(GameTeam owner : owners) {
            this.spawns.compute(owner, (key, value) -> {
                List<Spawn> spawns = Objects.requireNonNullElse(value, new ArrayList<>());
                spawns.add(spawn);

                return spawns;
            });
        }
    }

    public void removeSpawn(Spawn spawn) {
        Set<GameTeam> owners = spawn.getOwners();
        for(var owner : owners) {
            List<Spawn> spawns = this.spawns.get(owner);
            if(spawns == null) { continue; }

            spawns.remove(spawn);
        }
    }

    public Spawn findSpawn(GamePlayer player) {
        return this.strategy.find(this, player);
    }

}
