package cc.minetale.slime.lobby;

import cc.minetale.slime.Slime;
import cc.minetale.slime.map.LobbyMap;
import lombok.Getter;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Should be used instead of {@linkplain InstanceContainer}. Mainly used to correlate {@linkplain LobbyMap} with instances.<br>
 * It is possible for you to have your own implementation of this if you want to override {@linkplain InstanceContainer}'s behavior.
 */
public class LobbyInstance extends InstanceContainer {

    @Getter private LobbyMap map;

    /**
     * Creates a LobbyInstance, automatically sets the {@linkplain LobbyMap} and registers it.
     */
    public LobbyInstance(@NotNull UUID uniqueId, @NotNull LobbyMap map) {
        super(uniqueId, map.getDimension());
        setMap(map);

        Slime.INSTANCE_MANAGER.registerInstance(this);
    }

    /**
     * Creates a LobbyInstance, automatically sets the {@linkplain LobbyMap} and registers it.
     */
    public LobbyInstance(@NotNull LobbyMap map) {
        this(UUID.randomUUID(), map);
    }

    public void clear() {
        getChunks().forEach(this::unloadChunk);
    }

    public void setMap(LobbyMap map) {
        clear();
        map.setForInstance(this);
    }

}
