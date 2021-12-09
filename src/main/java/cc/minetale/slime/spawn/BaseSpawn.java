package cc.minetale.slime.spawn;

import cc.minetale.buildingtools.Utils;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.utils.TeamUtil;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
public final class BaseSpawn {

    private String id;
    private Set<ITeamType> owners;
    @Setter private Pos position;

    public BaseSpawn(String id, Set<ITeamType> owners, @NotNull Pos position) {
        this.id = id;
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
        this.position = position;
    }

    public BaseSpawn(String id, ITeamType owner, @NotNull Pos position) {
        this(id, Collections.singleton(owner), position);
    }

    public BaseSpawn(String id, @NotNull Pos position) {
        this(id, new HashSet<>(), position);
    }

    private BaseSpawn() {}

    public static BaseSpawn fromDocument(Document document, GameExtension game) {
        var spawnPoint = new BaseSpawn();
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
