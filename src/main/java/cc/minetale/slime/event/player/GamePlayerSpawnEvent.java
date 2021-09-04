package cc.minetale.slime.event.player;

import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import cc.minetale.slime.core.spawn.SpawnPoint;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class GamePlayerSpawnEvent implements PlayerEvent {

    private Game game;
    private GamePlayer gamePlayer;

    @Setter private SpawnPoint spawnPoint; //Proposed spawnpoint by the SpawnManager and its current SpawnStrategy

    public GamePlayerSpawnEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, @NotNull SpawnPoint spawnPoint) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.spawnPoint = spawnPoint;
    }

    @Override public @NotNull Player getPlayer() {
        return this.gamePlayer.getHandle();
    }

}
