package cc.minetale.slime.spawn;

import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * For more information why there are {@linkplain GameSpawn} <strong>and</strong> {@linkplain MapSpawn}
 * check {@linkplain MapSpawn}'s JavaDocs.
 */
@Getter
public final class GameSpawn extends AbstractSpawn implements IOwnableSpawn {

    private String id;
    private Set<GameTeam> owners;
    @Setter private Pos position;
    @Setter private Instance instance;

    public GameSpawn(MapSpawn base, Set<GameTeam> owners, @NotNull Instance instance) {
        this.id = base.getId();
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
        this.position = base.getPosition();
        this.instance = instance;
    }

    public GameSpawn(MapSpawn base, @NotNull Instance instance) {
        this(base, new HashSet<>(), instance);
    }

    @Contract(pure = true)
    public @NotNull @UnmodifiableView Set<GameTeam> getOwners() {
        return Collections.unmodifiableSet(this.owners);
    }

    public boolean addOwner(GameTeam team) {
        return this.owners.add(team);
    }

    public boolean addOwners(@NotNull Collection<GameTeam> teams) {
        var allSuccess = true;
        for(var team : teams) {
            if(!addOwner(team))
                allSuccess = false;
        }
        return allSuccess;
    }

    public boolean removeOwner(GameTeam team) {
        return this.owners.remove(team);
    }

    public boolean removeOwners(@NotNull Collection<GameTeam> teams) {
        var allSuccess = true;
        for(var team : teams) {
            if(!removeOwner(team))
                allSuccess = false;
        }
        return allSuccess;
    }

    public boolean isOwnedBy(GameTeam team) {
        return this.owners.contains(team);
    }

    @Override
    public void clearOwners() {
        this.owners.clear();
    }

    @Override
    public boolean isOwned() {
        return !this.owners.isEmpty();
    }

    @Override
    public boolean isShared() {
        return this.owners.size() > 1;
    }

}
