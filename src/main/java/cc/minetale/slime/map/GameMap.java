package cc.minetale.slime.map;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.magma.MagmaUtils;
import cc.minetale.mlib.util.DocumentUtil;
import cc.minetale.slime.Slime;
import cc.minetale.slime.spawn.MapSpawn;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.bson.Document;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class GameMap extends AbstractMap {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getMongoDatabase().getCollection("maps");

    protected String id;

    @Setter protected String name;
    @Setter protected String gamemode;

    @Setter protected NamespaceID dimension;

    @Setter protected Vec minPos;
    @Setter protected Vec maxPos;

    private final Map<String, MapSpawn> spawns = new ConcurrentHashMap<>();

    private boolean isOpen;

    public GameMap() {}

    public GameMap(String id, String name, String gamemode, NamespaceID dimension, Vec minPos, Vec maxPos) {
        this.id = id;

        this.name = name;
        this.gamemode = gamemode;

        this.dimension = dimension;

        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    public MapSpawn getSpawn(String id) {
        return this.getSpawns().get(id);
    }

    public void addSpawn(MapSpawn spawn) {
        this.spawns.put(spawn.getId(), spawn);
    }

    public MapSpawn removeSpawn(String spawnId) {
        return this.spawns.remove(spawnId);
    }

    public void removeSpawn(MapSpawn spawn) {
        removeSpawn(spawn.getId());
    }

    @Override
    public NamespaceID getDimensionID() {
        return this.dimension;
    }

    //TODO Make functional with our own Dimension registry
    @Override
    public DimensionType getDimension() {
        return DimensionType.OVERWORLD;
    }

    @Override
    public final UpdateResult setStatus(boolean isOpen) {
        this.isOpen = isOpen;
        return GameMap.getCollection()
                .updateOne(getFilter(), Updates.set("status", isOpen));
    }

    @Override
    public Path getFilePath() {
        return MagmaUtils.getDefaultLocation("maps/" + getId());
    }

    @Override
    protected void load(Document document) {
        this.id = document.getString("_id");

        this.name = document.getString("name");
        this.gamemode = document.getString("gamemode");

        this.dimension = NamespaceID.from(document.getString("dimension"));

        this.minPos = DocumentUtil.documentToVector(document.get("minPos", Document.class));
        this.maxPos = DocumentUtil.documentToVector(document.get("maxPos", Document.class));

        var game = Slime.getRegisteredGame(this.gamemode);
        if(game == null) { return; }

        for(Map.Entry<String, Object> ent : document.get("spawns", Document.class).entrySet()) {
            this.spawns.put(ent.getKey(), MapSpawn.fromDocument((Document) ent.getValue(), game));
        }

        this.isOpen = document.getBoolean("isOpen", false);
    }

    @Override
    public Document toDocument() {
        var document = new Document();
        document.put("_id", this.id);

        document.put("name", this.name);
        document.put("gamemode", this.gamemode);

        document.put("dimension", this.dimension.toString());

        document.put("minPos", DocumentUtil.vectorToDocument(this.minPos));
        document.put("maxPos", DocumentUtil.vectorToDocument(this.maxPos));

        Document spawnsDocument = new Document();
        for (Map.Entry<String, MapSpawn> ent : this.spawns.entrySet()) {
            var baseSpawn = ent.getValue();
            spawnsDocument.put(ent.getKey(), baseSpawn.toDocument());
        }
        document.put("spawns", spawnsDocument);

        document.put("isOpen", this.isOpen);

        return document;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof GameMap other && (this.id.equals(other.getId()) && this.gamemode.equals(other.getGamemode()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.gamemode);
    }

}
