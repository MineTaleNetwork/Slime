package cc.minetale.slime.core;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeReadable;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.spawn.SpawnPoint;
import cc.minetale.slime.state.IPlayerState;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GamePlayer extends Player implements IAttributeReadable, IAttributeWritable {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Nullable @Setter(AccessLevel.PACKAGE)
    private GameLobby lobby;

    private final Map<Attribute, Object> attributes;

    /** If the player dies when they have 0 lives, they cannot respawn. Anything below 0 means the player is dead. */
    @Setter protected int lives = 0;
    @Setter protected int score = 0;

    private IPlayerState state;

    @Setter protected SpawnPoint currentSpawn; //Spawnpoint this player spawned from last
    @Setter protected GameTeam gameTeam;

    public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        EnumMap<Attribute, Object> attributes = new EnumMap<>(Attribute.class);
        for(Attribute attribute : Attribute.values()) {
            attributes.put(attribute, attribute.getDefaultValue());
        }
        this.attributes = Collections.synchronizedMap(attributes);
    }

    public final void setState(IPlayerState state) {
        var event = new GamePlayerStateChangeEvent(this.game, this, this.state, state);
        EventDispatcher.call(event);

        state = event.getNewState();

        this.state = state;
        setGameMode(state.getGamemode());
    }

    public final void spawn() {
        respawn(); //TODO Set state and force spectator if attribute is enabled
    }

    public final boolean isAlive() {
        return this.lives < 0;
    }

    //Attributes
    @Override
    public void setAttribute(Attribute attr, Object value) {
        this.attributes.put(attr, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(Attribute attr) {
        return (T) this.attributes.get(attr);
    }

    //Tags
    private final NBTCompound nbtCompound = new NBTCompound();

}
