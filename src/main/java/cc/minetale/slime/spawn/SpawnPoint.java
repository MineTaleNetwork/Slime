package cc.minetale.slime.spawn;

import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class SpawnPoint {

    private final Set<GameTeam> owners;
    @Setter @NotNull private Pos position;
    @Setter @NotNull private Instance instance;

    public SpawnPoint(@NotNull Pos position, @NotNull Instance instance) {
        this.owners = Collections.synchronizedSet(new HashSet<>());
        this.position = position;
        this.instance = instance;
    }

    public SpawnPoint(GameTeam owner, @NotNull Pos position, @NotNull Instance instance) {
        this(Collections.singleton(owner), position, instance);
    }

    public SpawnPoint(Set<GameTeam> owners, @NotNull Pos position, @NotNull Instance instance) {
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
        this.position = position;
        this.instance = instance;
    }

    public boolean addOwner(GameTeam team) {
        return this.owners.add(team);
    }

    public boolean removeOwner(GameTeam team) {
        return this.owners.remove(team);
    }

    public boolean isShared() {
        return this.owners.size() > 1;
    }

}
