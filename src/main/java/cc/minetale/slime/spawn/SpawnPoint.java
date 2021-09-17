package cc.minetale.slime.spawn;

import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class SpawnPoint {

    private final Set<GameTeam> owners;
    @Setter private Pos position;
    @Setter private Instance instance;

    public SpawnPoint() {
        this.owners = Collections.synchronizedSet(new HashSet<>());
    }

    public SpawnPoint(GameTeam owner) {
        this.owners = Collections.synchronizedSet(new HashSet<>(Collections.singleton(owner)));
    }

    public SpawnPoint(Set<GameTeam> owners) {
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
    }

}
