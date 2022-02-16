package cc.minetale.slime.spawn;

import cc.minetale.mlib.util.DocumentUtil;
import cc.minetale.slime.core.GameExtension;
import cc.minetale.slime.event.game.PreGameSetupEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.utils.TeamUtil;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Used in maps, unlike {@linkplain GameSpawn} these are independent from the {@linkplain Game} <br>
 * and its environment (like teams, instances, players etc.). <br>
 * This allows {@linkplain GameSpawn} to have a much richer API, handle more boilerplate code <br>
 * and makes coding and managing map creation easier because this isn't dependent on {@linkplain Game} which doesn't exist during map creation,
 * for example due to this requiring {@linkplain ITeamType} and not {@linkplain GameTeam} as owners or not requiring an instance. <br>
 * <br>
 * Also see: {@linkplain PreGameSetupEvent}
 */
@Getter
public final class MapSpawn extends AbstractSpawn implements IOwnableSpawn {

    private String id;
    private final Set<ITeamType> owners;
    @Setter private Pos position;

    public MapSpawn(String id, Set<ITeamType> owners, @NotNull Pos position) {
        this.id = id;
        this.owners = Collections.synchronizedSet(new HashSet<>(owners));
        this.position = position;
    }

    public MapSpawn(String id, ITeamType owner, @NotNull Pos position) {
        this(id, Collections.singleton(owner), position);
    }

    public MapSpawn(String id, @NotNull Pos position) {
        this(id, new HashSet<>(), position);
    }

    private MapSpawn() {
        this.owners = Collections.synchronizedSet(new HashSet<>());
    }

    public static @NotNull MapSpawn fromDocument(Document document, GameExtension game) {
        var spawn = new MapSpawn();
        spawn.load(document, game);
        return spawn;
    }

    public boolean addOwner(ITeamType team) {
        return this.owners.add(team);
    }

    public boolean addOwners(@NotNull Collection<ITeamType> teams) {
        var allSuccess = true;
        for(var team : teams) {
            if(!addOwner(team))
                allSuccess = false;
        }
        return allSuccess;
    }

    public boolean removeOwner(ITeamType team) {
        return this.owners.remove(team);
    }

    public boolean removeOwners(@NotNull Collection<ITeamType> teams) {
        var allSuccess = true;
        for(var team : teams) {
            if(!removeOwner(team))
                allSuccess = false;
        }
        return allSuccess;
    }

    public boolean isOwnedBy(ITeamType team) {
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

    private void load(@NotNull Document document, GameExtension game) {
        this.id = document.getString("_id");

        for(String teamId : document.getList("owners", String.class)) {
            ITeamType team = TeamUtil.findById(game.getTeamTypes(), teamId);
            this.owners.add(team);
        }

        this.position = DocumentUtil.documentToPosition(document.get("position", Document.class));
    }

    public @NotNull Document toDocument() {
        var document = new Document();

        document.put("_id", this.id);

        List<String> ownerIds = new ArrayList<>();
        for(ITeamType owner : this.owners) {
            ownerIds.add(owner.getId());
        }
        document.put("owners", ownerIds);

        document.put("position", DocumentUtil.positionToDocument(this.position));

        return document;
    }

}
