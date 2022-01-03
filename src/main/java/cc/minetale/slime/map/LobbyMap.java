package cc.minetale.slime.map;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.magma.MagmaUtils;
import cc.minetale.mlib.util.DocumentUtil;
import cc.minetale.slime.utils.MapUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.nio.file.Path;
import java.util.Objects;

@Getter
public class LobbyMap extends AbstractMap {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getMongoDatabase().getCollection("lobbies");

    protected String id;
    @Setter protected String gamemode;
    @Setter protected NamespaceID dimension;
    @Setter protected Vec minPos;
    @Setter protected Vec maxPos;
    @Setter protected Pos spawn = Pos.ZERO;
    private boolean isOpen;

    protected LobbyMap() {}

    public LobbyMap(String id, String gamemode, NamespaceID dimension, Vec minPos, Vec maxPos) {
        this.id = id;
        this.gamemode = gamemode;
        this.dimension = dimension;
        this.minPos = minPos;
        this.maxPos = maxPos;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String name) {}

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
        return LobbyMap.getCollection()
                .updateOne(getFilter(), Updates.set("status", isOpen));
    }

    @Override
    public Path getFilePath() {
        return MagmaUtils.getDefaultLocation("lobbies/" + getId());
    }

    @Override
    protected void load(Document document) {
        this.id = document.getString("_id");
        this.gamemode = document.getString("gamemode");
        this.dimension = NamespaceID.from(document.getString("dimension"));
        this.minPos = DocumentUtil.documentToVector(document.get("minPos", Document.class));
        this.maxPos = DocumentUtil.documentToVector(document.get("maxPos", Document.class));
        this.spawn = DocumentUtil.documentToPosition(document.get("spawn", Document.class));
        this.isOpen = document.getBoolean("isOpen", false);
    }

    @Override
    public Document toDocument() {
        var document = new Document();
        document.put("_id", this.id);
        document.put("gamemode", this.gamemode);
        document.put("dimension", this.dimension.toString());
        document.put("minPos", DocumentUtil.vectorToDocument(this.minPos));
        document.put("maxPos", DocumentUtil.vectorToDocument(this.maxPos));
        document.put("spawn", DocumentUtil.positionToDocument(this.spawn));
        document.put("isOpen", this.isOpen);

        return document;
    }

    public final Bson getFilter() {
        return MapUtil.getFilter(this.gamemode, this.id);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof LobbyMap other && (this.id.equals(other.getId()) && this.gamemode.equals(other.getGamemode()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.gamemode);
    }
}
