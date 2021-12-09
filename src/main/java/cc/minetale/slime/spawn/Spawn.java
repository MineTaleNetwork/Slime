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
public final class Spawn {

    private String id;
    private Set<GameTeam> owners;
    @Setter private Pos position;
    @Setter private Instance instance;

    public Spawn(BaseSpawn base, Set<GameTeam> owners, @NotNull Instance instance) {
        this.id = base.getId();
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
        this.position = base.getPosition();
        this.instance = instance;
    }

    public Spawn(BaseSpawn base, @NotNull Instance instance) {
        this(base, new HashSet<>(), instance);
    }

    public boolean addOwner(GameTeam team) {
        return this.owners.add(team);
    }

    public boolean removeOwner(GameTeam team) {
        return this.owners.remove(team);
    }

    public void clearOwners() {
        this.owners.clear();
    }

    public boolean isShared() {
        return this.owners.size() > 1;
    }

}
