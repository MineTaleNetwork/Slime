package cc.minetale.slime.spawn;

import cc.minetale.buildingtools.Utils;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.utils.TeamUtil;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public final class SpawnPoint {

    @Getter @Setter private String id;
    private Set<ITeamType> owners;
    @Setter @NotNull private Pos position;
    @Setter private Instance instance;

    public SpawnPoint(String id, Set<ITeamType> owners, @NotNull Pos position, @Nullable Instance instance) {
        this.id = id;
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
        this.position = position;
        this.instance = instance;
    }

    public SpawnPoint(String id, ITeamType owner, @NotNull Pos position, @Nullable Instance instance) {
        this(id, Collections.singleton(owner), position, instance);
    }

    public SpawnPoint(String id, @NotNull Pos position, @Nullable Instance instance) {
        this(id, new HashSet<>(), position, instance);
    }

    private SpawnPoint() {}

    public static SpawnPoint fromDocument(Document document, GameExtension game) {
        var spawnPoint = new SpawnPoint();
        spawnPoint.load(document, game);
        return spawnPoint;
    }

    public boolean addOwner(ITeamType team) {
        return this.owners.add(team);
    }

    public boolean removeOwner(ITeamType team) {
        return this.owners.remove(team);
    }

    public void clearOwners() {
        this.owners.clear();
    }

    public boolean isShared() {
        return this.owners.size() > 1;
    }

    private void load(Document document, GameExtension game) {
        for(String teamId : document.getList("owners", String.class)) {
            ITeamType team = TeamUtil.findById(game.getTeamTypes(), teamId);
            this.owners.add(team);
        }

        this.position = Utils.documentToPosition(document.get("position", Document.class));
    }

    public Document toDocument() {
        var document = new Document();

        List<String> ownerIds = new ArrayList<>();
        for(ITeamType owner : this.owners) {
            ownerIds.add(owner.getId());
        }
        document.put("owners", ownerIds);

        document.put("position", Utils.positionToDocument(this.position));

        return document;
    }

}
