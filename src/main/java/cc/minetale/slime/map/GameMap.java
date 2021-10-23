package cc.minetale.slime.map;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.magma.MagmaLoader;
import cc.minetale.magma.MagmaUtils;
import cc.minetale.slime.Slime;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;
import org.bson.Document;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class GameMap {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("maps");

    @Setter(AccessLevel.PROTECTED) private String id;
    @Setter(AccessLevel.PROTECTED) private String gamemode;

    @Setter(AccessLevel.PROTECTED) private String dimensionId;

    @Getter private final Map<String, Zone> zones = new ConcurrentHashMap<>();
    @Getter private final Map<String, Pos> points = new ConcurrentHashMap<>();

    private GameMap() {}

    //TODO Block usage by setting to private and only allow to load from database through GameMap#
    public GameMap(String id, String gamemode, String dimensionId) {
        this.id = id;
        this.gamemode = gamemode;

        this.dimensionId = dimensionId;
    }

    protected void fromDocument(Document document) {
        this.id = document.getString("_id");
        this.gamemode = document.getString("gamemode");

        this.dimensionId = document.getString("dimensionId");

        for(Map.Entry<String, Object> ent : document.get("zones", Document.class).entrySet()) {
            this.zones.put(ent.getKey(), new Zone((Document) ent.getValue()));
        }

        for(Map.Entry<String, Object> ent : document.get("points", Document.class).entrySet()) {
            var pos = (Document) ent.getValue();
            this.points.put(ent.getKey(),
                    new Pos(pos.getDouble("x"),
                            pos.getDouble("y"),
                            pos.getDouble("z"),
                            pos.get("yaw", Float.class),
                            pos.get("pitch", Float.class)));
        }
    }

    //TODO Use SeaweedFS in production
    public CompletableFuture<Void> setForInstance(InstanceContainer instance) {
        return MagmaLoader.create(Path.of(this.id + "." + MagmaUtils.FORMAT_NAME)).thenAccept(instance::setChunkLoader);
    }

    public static GameMap load(String name, Supplier<GameMap> mapProvider) {
        var map = mapProvider.get();

        var document = collection.find(Filters.and(
                Filters.eq("gamemode", Slime.getActiveGame().getId()),
                Filters.eq("name", name))).first();

        if(document == null) { return null; }
        map.fromDocument(document);

        return map;
    }

    public static GameMap load(String name) {
        return load(name, GameMap::new);
    }

    //TODO Make functional with our own Dimension registry
    public DimensionType getDimension() {
        return DimensionType.OVERWORLD;
    }

}
