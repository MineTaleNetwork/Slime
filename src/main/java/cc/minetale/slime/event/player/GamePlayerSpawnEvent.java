package cc.minetale.slime.event.player;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.event.trait.GamePlayerEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.spawn.GameSpawn;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
public class GamePlayerSpawnEvent implements GameEvent, GamePlayerEvent {

    private Game game;
    private GamePlayer gamePlayer;

    @Setter private GameSpawn spawn; //Proposed spawn by the SpawnManager and its current SpawnStrategy

    public GamePlayerSpawnEvent(@NotNull Game game, @NotNull GamePlayer gamePlayer, @NotNull GameSpawn spawn) {
        this.game = game;
        this.gamePlayer = gamePlayer;

        this.spawn = spawn;
    }

}
