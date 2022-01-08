package cc.minetale.slime.spawn;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public final class SpawnManager {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter private ISpawnStrategy strategy = DefaultStrategy.RANDOM;

    private final List<GameSpawn> spawns = Collections.synchronizedList(new ArrayList<>());

    @Contract(pure = true)
    public @NotNull @UnmodifiableView List<GameSpawn> getSpawns() {
        return Collections.unmodifiableList(this.spawns);
    }

    public List<GameSpawn> getSpawnsFor(GameTeam team) {
        synchronized(this.spawns) {
            return this.spawns.stream()
                    .filter(spawn -> !spawn.isOwned() || spawn.isOwnedBy(team))
                    .sorted((first, second) -> {
                        var firstOwned = first.isOwnedBy(team);
                        var secondOwned = second.isOwnedBy(team);

                        if(firstOwned && !secondOwned) {
                            return 1;
                        } else if(!firstOwned && secondOwned) {
                            return -1;
                        } else {
                            //They are both not owned
                            var firstId = first.getId();
                            var secondId = second.getId();
                            return firstId.compareTo(secondId);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    public void addSpawn(@NotNull GameSpawn spawn) {
        this.spawns.add(spawn);
    }

    public void addSpawns(@NotNull Collection<GameSpawn> spawns) {
        spawns.forEach(this::addSpawn);
    }

    public void removeSpawn(@NotNull GameSpawn spawn) {
        this.spawns.remove(spawn);
    }

    public void removeSpawns(@NotNull Collection<GameSpawn> spawns) {
        spawns.forEach(this::removeSpawn);
    }

    public GameSpawn findSpawn(GamePlayer player) {
        return this.strategy.find(this, player);
    }

}
