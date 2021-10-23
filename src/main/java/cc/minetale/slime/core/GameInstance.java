package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.map.GameMap;
import lombok.Getter;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Should be used instead of {@linkplain InstanceContainer}. Mainly used to correlate {@linkplain GameMap} with instances.<br>
 * It is possible for you to have your own implementation of this if you want to override {@linkplain InstanceContainer}'s behavior.
 */
public class GameInstance extends InstanceContainer {

    @Getter private GameMap map;

    /**
     * Creates a GameInstance, automatically sets the {@linkplain GameMap} and registers it.
     */
    public GameInstance(@NotNull UUID uniqueId, @NotNull GameMap map) {
        super(uniqueId, map.getDimension());
        setMap(map);

        Slime.INSTANCE_MANAGER.registerInstance(this);
    }

    /**
     * Creates a GameInstance, automatically sets the {@linkplain GameMap} and registers it.
     */
    public GameInstance(@NotNull GameMap map) {
        this(UUID.randomUUID(), map);
    }

    public void clear() {
        getChunks().forEach(this::unloadChunk);
    }

    public void setMap(GameMap map) {
        clear();
        map.setForInstance(this);
    }

}
