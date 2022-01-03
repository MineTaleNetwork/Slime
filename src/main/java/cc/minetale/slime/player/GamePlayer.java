package cc.minetale.slime.player;

import cc.minetale.flame.util.FlamePlayer;
import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.Attributes;
import cc.minetale.slime.attribute.IAttributeReadable;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.lobby.GameLobby;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.spawn.Spawn;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class GamePlayer extends FlamePlayer implements IAttributeReadable, IAttributeWritable {

    @Setter private Game game;

    @Nullable @Setter private GameLobby lobby;

    private final Map<Attribute<?>, Object> attributes;

    /** If the player dies when they have 0 lives, they cannot respawn. Anything below 0 means the player is dead. */
    @Setter protected int lives = 0;
    @Setter protected int score = 0;

    private IPlayerState state;

    protected Spawn currentSpawn; //Spawnpoint this player spawned from last
    @Setter protected GameTeam gameTeam;

    public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        Map<Attribute<?>, Object> attributes = new HashMap<>(Attributes.ALL_ATTRIBUTES.size());
        for(Attribute<?> attribute : Attributes.ALL_ATTRIBUTES) {
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

    public final void setCurrentSpawn(@NotNull Spawn spawn) {
        this.currentSpawn = spawn;
        setRespawnPoint(this.currentSpawn.getPosition());
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
    public <T> T getAttribute(Attribute<T> attr) {
        return (T) this.attributes.get(attr);
    }

}
