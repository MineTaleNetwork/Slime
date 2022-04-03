package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameInstance;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.spawn.MapSpawn;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This event should be listened to and is called during {@linkplain Stage#SETUP}, before {@linkplain PostGameSetupEvent}.<br>
 * Its purpose is for a {@linkplain GameInstance} to be setup correctly and involves things like: <br>
 * <ul>
 *     <li>(Required) Convert {@linkplain MapSpawn}(s) to {@linkplain GameSpawn}(s). <br>
 *     <br>
 *     This is useful if you want to change spawn's position, instance or if even include it in the game based on its ID or game settings.
 *     It's also required because a {@linkplain GameSpawn} requires an instance to spawn players on, more can be found at {@linkplain MapSpawn}.</li>
 *
 *     <li>Spawning entities</li>
 *     <li>Setting blocks</li>
 * </ul>
 *
 */
public class PostInstanceSetupEvent implements GameEvent {

    @Getter private final Game game;
    @Getter private final GameInstance instance;

    //Spawns
    @Getter private final List<MapSpawn> mapSpawns;
    @Getter private final List<GameSpawn> gameSpawns;

    public PostInstanceSetupEvent(Game game, GameMap map, GameInstance instance) {
        this.game = game;
        this.instance = instance;

        this.mapSpawns = List.copyOf(map.getSpawns().values());
        this.gameSpawns = new ArrayList<>();
    }

}
