package cc.minetale.slime.core;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.magma.MagmaReader;
import cc.minetale.magma.MagmaUtils;
import cc.minetale.slime.utils.Zone;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import org.bson.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class GameMap {

    @Getter private static MongoCollection<Document> collection = CommonLib.getCommonLib().getMongoDatabase().getCollection("maps");

    @Setter(AccessLevel.PROTECTED) private String name;
    @Setter(AccessLevel.PROTECTED) private String gamemode;

    @Getter private final Map<String, Zone> zones = new ConcurrentHashMap<>();
    @Getter private final Map<String, Pos> points = new ConcurrentHashMap<>();

    public GameMap(String name, String gamemode) {
        this.name = name;
        this.gamemode = gamemode;
    }

    protected void fromDocument(Document document) {
        this.name = document.getString("name");
        this.gamemode = document.getString("gamemode");

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

    public void paste(GameExtension game) {
        MagmaReader.read(game.getDataDirectory().resolve("test." + MagmaUtils.FORMAT_NAME));
    }

    public static GameMap load(GameExtension game, String name, Supplier<GameMap> mapProvider) {
        var map = mapProvider.get();

        var document = collection.find(Filters.and(
                Filters.eq("gamemode", game.getId()),
                Filters.eq("name", name))).first();

        if(document == null) { return null; }
        map.fromDocument(document);

        return map;
    }

    public static GameMap load(GameExtension game, String name) {
        return load(game, name, () -> new GameMap(game.getId(), name));
    }

}
