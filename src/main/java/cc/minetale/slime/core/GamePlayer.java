package cc.minetale.slime.core;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeReadable;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GamePlayer implements IAttributeReadable, IAttributeWritable, TagReadable, TagWritable {

    private static final Map<Player, GamePlayer> WRAPPERS = new ConcurrentHashMap<>();

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Player handle;

    @Nullable @Setter(AccessLevel.PACKAGE)
    private GameLobby lobby;

    private final Map<Attribute, Object> attributes = Collections.synchronizedMap(new EnumMap<>(Attribute.class));

    @Setter protected int lives = 0; //If the player dies when they have 0 lives, they cannot respawn.
    @Setter protected int score = 0;

    private IPlayerState state;

    @Setter protected GameTeam team;

    protected GamePlayer(Player player) {
        WRAPPERS.put(player, this);
        this.handle = player;
    }

    public final void setState(IPlayerState state) {
        var event = new GamePlayerStateChangeEvent(this.game, this, this.state, state);
        EventDispatcher.call(event);

        state = event.getNewState();

        this.state = state;
        this.handle.setGameMode(state.getGamemode());
    }

    public final void spawn() {
        this.handle.respawn(); //TODO Set state and force spectator if attribute is enabled
    }

    public static void registerWrapper(GamePlayer gamePlayer) {
        WRAPPERS.put(gamePlayer.handle, gamePlayer);
    }

    public static void unregisterWrapper(GamePlayer gamePlayer) {
        WRAPPERS.remove(gamePlayer.handle);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GamePlayer> @Nullable T getWrapper(Player player) {
        return (T) WRAPPERS.get(player);
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

    @Override
    public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(this.nbtCompound);
    }

    @Override
    public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(this.nbtCompound, value);
    }
}
