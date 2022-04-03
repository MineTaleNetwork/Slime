package cc.minetale.slime.event.game;

import cc.minetale.slime.event.trait.GameEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameInstance;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.map.GameMap;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.world.DimensionType;

/**
 * This event should be listened to and is called during {@linkplain Stage#SETUP}, after {@linkplain PreGameSetupEvent}.<br>
 * Its purpose is for a {@linkplain GameInstance} to be setup correctly and involves things like: <br>
 * <ul>
 *     <li>Override the map's dimension</li>
 * </ul>
 */
public class PreInstanceSetupEvent implements GameEvent {

    @Getter private final Game game;
    @Getter private final GameMap map;

    //Dimension
    @Getter @Setter private DimensionType dimension;

    public PreInstanceSetupEvent(Game game, GameMap map) {
        this.game = game;
        this.map = map;

        this.dimension = map.getDimension();
    }

}
