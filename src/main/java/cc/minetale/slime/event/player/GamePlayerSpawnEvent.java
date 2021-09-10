package cc.minetale.slime.event.player;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.spawn.SpawnPoint;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
public class GamePlayerSpawnEvent implements GameEvent, GamePlayerEvent {

    private Game game;
    private GamePlayer gamePlayer;

    @Setter private SpawnPoint spawnPoint; //Proposed spawnpoint by the SpawnManager and its current SpawnStrategy

    public GamePlayerSpawnEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, @NotNull SpawnPoint spawnPoint) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.spawnPoint = spawnPoint;
    }

}
