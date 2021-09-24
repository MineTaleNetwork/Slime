package cc.minetale.slime.team;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.attribute.IAttributeWritable;
import cc.minetale.slime.core.Game;
import cc.minetale.slime.core.GamePlayer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class GameTeam implements IAttributeWritable, TagReadable, TagWritable {

    @Setter(AccessLevel.PACKAGE)
    private Game game;

    @Setter(AccessLevel.PACKAGE)
    private Team handle;

    @Getter @Setter int size;
    @Getter @Setter ITeamType type;

    @Getter List<GamePlayer> players = Collections.synchronizedList(new ArrayList<>());

    public boolean addPlayers(Collection<GamePlayer> players) {
        if(!canFitPlayers(players.size())) { return false; }
        boolean allAdded = true;
        for(var player : players) {
            if(!addPlayer(player)) { allAdded = false; }
        }
        return allAdded;
    }

    public boolean addPlayer(GamePlayer player) {
        if(!canFitPlayers(1)) { return false; }
        player.setGameTeam(this);
        this.players.add(player);
        return true;
    }

    boolean canFitPlayers(int amount) {
        return this.size - this.players.size() > amount;
    }

    //Attributes
    @Override
    public void setAttribute(Attribute attr, Object value) {
        this.players.forEach(player -> player.setAttribute(attr, value));
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
