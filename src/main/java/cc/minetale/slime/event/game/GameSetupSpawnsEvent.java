package cc.minetale.slime.event.game;

import cc.minetale.slime.game.Game;
import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.spawn.BaseSpawn;
import cc.minetale.slime.spawn.Spawn;
import cc.minetale.slime.game.Stage;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO Do we really need to do it this way?
/**
 * This event should be listened to and is called during {@linkplain Stage#SETUP}.<br>
 * Its purpose is to "convert" all initial {@linkplain BaseSpawn} retrieved from {@linkplain GameMap} to ready-to-use {@linkplain Spawn}.
 */
public class GameSetupSpawnsEvent implements GameEvent {

    @Getter private final Game game;
    @Getter private final List<BaseSpawn> baseSpawns;

    @Getter private final List<Spawn> spawns;

    public GameSetupSpawnsEvent(Game game, List<BaseSpawn> baseSpawns) {
        this.game = game;
        this.baseSpawns = Collections.unmodifiableList(baseSpawns);

        this.spawns = new ArrayList<>();
    }

}
