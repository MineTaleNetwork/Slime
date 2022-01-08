package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.spawn.MapSpawn;
import cc.minetale.slime.spawn.GameSpawn;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This event should be listened to and is called during {@linkplain Stage#SETUP}.<br>
 * Its purpose is for a game to be setup correctly and involves things like: <br>
 * <ul>
 *     <li>Convert {@linkplain MapSpawn}s to {@linkplain GameSpawn}s. <br>
 *     <br>
 *     This is useful if you want to change spawn's position, instance or if even include it in the game based on its ID or game settings.
 *     It's also required because a {@linkplain GameSpawn} requires an instance to spawn players on, more can be found at {@linkplain MapSpawn}.</li>
 * </ul>
 */
public class GameSetupEvent implements GameEvent {

    @Getter private final Game game;

    //Spawns
    @Getter private final List<MapSpawn> mapSpawns;
    @Getter private final List<GameSpawn> gameSpawns;

    public GameSetupEvent(Game game, List<MapSpawn> mapSpawns) {
        this.game = game;

        this.mapSpawns = Collections.unmodifiableList(mapSpawns);
        this.gameSpawns = new ArrayList<>();
    }

}
