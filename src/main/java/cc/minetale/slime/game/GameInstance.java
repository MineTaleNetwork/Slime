package cc.minetale.slime.game;

import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.GameMap;
import lombok.Getter;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;

/**
 * Should be used instead of {@linkplain InstanceContainer}. Mainly used to correlate {@linkplain AbstractMap} with instances.<br>
 * It is possible for you to have your own implementation of this if you want to override {@linkplain InstanceContainer}'s behavior.
 */
public class GameInstance extends InstanceContainer {

    @Getter private AbstractMap map;

    /**
     * Creates a GameInstance, automatically sets the {@linkplain AbstractMap} and registers it.
     */
    public GameInstance(@NotNull UUID uniqueId, @NotNull AbstractMap map) {
        super(uniqueId, map.getDimension());
        INSTANCE_MANAGER.registerInstance(this);

        this.map = map;
    }

    /**
     * Creates a GameInstance, automatically sets the {@linkplain GameMap} and registers it.
     */
    public GameInstance(@NotNull AbstractMap map) {
        this(UUID.randomUUID(), map);
    }

    public void clear() {
        getChunks().forEach(this::unloadChunk);
    }

    public CompletableFuture<Void> setMap(AbstractMap map) {
        clear();
        this.map = map;

        return map.setForInstance(this);
    }

}
