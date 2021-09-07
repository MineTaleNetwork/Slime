package cc.minetale.slime.core;

import cc.minetale.slime.team.GameTeam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GamePlayer implements TagReadable, TagWritable {

    private static final Map<Player, GamePlayer> WRAPPERS = new ConcurrentHashMap<>();

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Player handle;

    @Nullable @Setter(AccessLevel.PACKAGE)
    private GameLobby lobby;

    @Setter protected int lives = 1;
    @Setter protected boolean canLoseLives = true;

    @Setter GameTeam team;

    protected GamePlayer(Player player) {
        WRAPPERS.put(player, this);
        this.handle = player;
    }

    public static void registerWrapper(GamePlayer gamePlayer) {
        WRAPPERS.put(gamePlayer.handle, gamePlayer);
    }

    public static void unregisterWrapper(GamePlayer gamePlayer) {
        WRAPPERS.remove(gamePlayer.handle);
    }

    @SuppressWarnings("unchecked")
    public static <T extends GamePlayer> T getWrapper(Player player) {
        return (T) WRAPPERS.get(player);
    }

    //Tags
    private final NBTCompound nbtCompound = new NBTCompound();

    @Override public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(this.nbtCompound);
    }

    @Override public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(this.nbtCompound, value);
    }

}
