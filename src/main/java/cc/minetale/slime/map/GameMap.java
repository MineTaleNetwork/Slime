package cc.minetale.slime.map;

import cc.minetale.buildingtools.Selection;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.magma.MagmaLoader;
import cc.minetale.magma.MagmaUtils;
import cc.minetale.slime.Slime;
import cc.minetale.slime.spawn.SpawnPoint;
import cc.minetale.slime.utils.MapUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public class GameMap {

    @Getter private static final MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("maps");

    @Getter protected String id;

    @Getter @Setter protected String name;
    @Getter @Setter protected String gamemode;

    @Setter protected NamespaceID dimension;

    @Getter @Setter protected Selection playArea;

    @Getter private final Map<String, SpawnPoint> spawnPoints = new ConcurrentHashMap<>();

    @Getter private boolean isOpen;

    protected GameMap() {}

    public GameMap(String id, String name, String gamemode, NamespaceID dimension, Selection playArea) {
        this.id = id;

        this.name = name;
        this.gamemode = gamemode;

        this.dimension = dimension;

        this.playArea = playArea;
    }

    public static <T extends GameMap> T fromDocument(Document document, Supplier<T> mapProvider) {
        var map = mapProvider.get();
        map.load(document);
        return map;
    }

    public static GameMap fromDocument(Document document) {
        return GameMap.fromDocument(document, GameMap::new);
    }

    public static <T extends GameMap> T fromBoth(String gamemode, String id, MapProvider<T> mapProvider) {
        var map = mapProvider.emptyMap();

        var document = collection.find(MapUtil.getFilter(gamemode, id)).first();

        if(document == null) { return null; }
        map.load(document);

        return map;
    }

    public static GameMap fromBoth(String gamemode, String id) {
        return fromBoth(gamemode, id, MapProvider.DEFAULT);
    }

    public static <T extends GameMap> T fromActiveGame(String id, MapProvider<T> mapProvider) {
        return fromBoth(Slime.getActiveGame().getId(), id, mapProvider);
    }

    public static GameMap fromActiveGame(String id) {
        return fromActiveGame(id, MapProvider.DEFAULT);
    }

    //TODO Use SeaweedFS in production
    public CompletableFuture<Void> setForInstance(InstanceContainer instance) {
        return MagmaLoader.create(getFilePath()).thenAccept(instance::setChunkLoader);
    }

    public SpawnPoint getSpawnPoint(String id) {
        return this.getSpawnPoints().get(id);
    }

    public void addSpawnPoint(SpawnPoint spawnPoint) {
        this.spawnPoints.put(spawnPoint.getId(), spawnPoint);
    }

    public void removeSpawnPoint(String spawnId) {
        this.spawnPoints.remove(spawnId);
    }

    public void removeSpawnPoint(SpawnPoint spawnPoint) {
        removeSpawnPoint(spawnPoint.getId());
    }

    //TODO Use SeaweedFS in production
    public final Path getFilePath() {
        return MagmaUtils.getDefaultLocation(this.id);
    }

    public NamespaceID getDimensionID() {
        return this.dimension;
    }

    //TODO Make functional with our own Dimension registry
    public DimensionType getDimension() {
        return DimensionType.OVERWORLD;
    }

    public final UpdateResult setStatus(boolean isOpen) {
        this.isOpen = isOpen;
        return GameMap.getCollection()
                .updateOne(getFilter(), Updates.set("status", isOpen));
    }

    protected void load(Document document) {
        this.id = document.getString("_id");

        this.name = document.getString("name");
        this.gamemode = document.getString("gamemode");

        this.dimension = NamespaceID.from(document.getString("dimension"));

        this.playArea = Selection.fromDocument(document.get("playArea", Document.class));

        var game = TOOL_MANAGER.isEnabled() ? TOOL_MANAGER.getGame(this.gamemode).orElse(null) : Slime.getActiveGame();

        for(Map.Entry<String, Object> ent : document.get("spawnPoints", Document.class).entrySet()) {
            this.spawnPoints.put(ent.getKey(), SpawnPoint.fromDocument((Document) ent.getValue(), game));
        }

        this.isOpen = document.getBoolean("isOpen", false);
    }

    public Document toDocument() {
        var document = new Document();
        document.put("_id", this.id);

        document.put("name", this.name);
        document.put("gamemode", this.gamemode);

        document.put("dimension", this.dimension.toString());

        document.put("playArea", this.playArea.toDocument());

        Document spawnPointsDocument = new Document();
        for (Map.Entry<String, SpawnPoint> ent : this.spawnPoints.entrySet()) {
            var spawnPoint = ent.getValue();
            spawnPointsDocument.put(ent.getKey(), spawnPoint.toDocument());
        }
        document.put("spawnPoints", spawnPointsDocument);

        document.put("isOpen", this.isOpen);

        return document;
    }

    public final Bson getFilter() {
        return MapUtil.getFilter(this.gamemode, this.id);
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
