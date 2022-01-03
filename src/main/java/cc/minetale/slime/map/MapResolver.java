package cc.minetale.slime.map;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.slime.Slime;
import cc.minetale.slime.utils.MapUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.List;

public interface MapResolver<T extends AbstractMap> {

    <E extends T> E fromDocument(Document document, MapProvider<E> provider);
    T fromDocument(Document document);

    <E extends T> E fromBoth(String gamemode, String id, MapProvider<E> provider);
    T fromBoth(String gamemode, String id);

    <E extends T> E fromActiveGame(String id, MapProvider<E> provider);
    T fromActiveGame(String id);

    <E extends T> E getRandomMap(String gamemode, MapProvider<E> provider);
    T getRandomMap(String gamemode);

    boolean isInDatabase(String gamemode, String id);

    MapResolver<GameMap> DEFAULT_GAME = new MapResolver<>() {
        private static final MongoCollection<Document> collection = CommonLib.getMongoDatabase().getCollection("maps");

        @Override
        public <E extends GameMap> E fromDocument(Document document, MapProvider<E> provider) {
            var map = provider.emptyMap();
            map.load(document);
            return map;
        }

        @Override
        public GameMap fromDocument(Document document) {
            return fromDocument(document, MapProvider.DEFAULT_GAME);
        }

        @Override
        public <E extends GameMap> E fromBoth(String gamemode, String id, MapProvider<E> provider) {
            var map = provider.emptyMap();

            var document = collection.find(MapUtil.getFilter(gamemode, id)).first();

            if(document == null) { return null; }
            map.load(document);

            return map;
        }

        @Override
        public GameMap fromBoth(String gamemode, String id) {
            return fromBoth(gamemode, id, MapProvider.DEFAULT_GAME);
        }

        @Override
        public <E extends GameMap> E fromActiveGame(String id, MapProvider<E> provider) {
            return fromBoth(Slime.getActiveGame().getId(), id, provider);
        }

        @Override
        public GameMap fromActiveGame(String id) {
            return fromActiveGame(id, MapProvider.DEFAULT_GAME);
        }

        @Override
        public <E extends GameMap> E getRandomMap(String gamemode, MapProvider<E> provider) {
            gamemode = gamemode.isBlank() ? Slime.getActiveGame().getId() : gamemode;

            var map = provider.emptyMap();

            var document = collection.aggregate(List.of(
                    Aggregates.match(Filters.eq("gamemode", gamemode)),
                    Aggregates.sample(1))).first();

            if(document == null) { return null; }
            map.load(document);

            return map;
        }

        @Override
        public GameMap getRandomMap(String gamemode) {
            gamemode = gamemode.isBlank() ? Slime.getActiveGame().getId() : gamemode;
            return getRandomMap(gamemode, MapProvider.DEFAULT_GAME);
        }

        @Override
        public boolean isInDatabase(String gamemode, String id) {
            return collection.countDocuments(
                    Filters.and(
                            Filters.eq("_id", id),
                            Filters.eq("gamemode", gamemode)
                    ), new CountOptions().limit(1)) > 0;
        }
    };

    MapResolver<LobbyMap> DEFAULT_LOBBY = new MapResolver<>() {
        private static final MongoCollection<Document> collection = CommonLib.getMongoDatabase().getCollection("lobbies");

        @Override public <E extends LobbyMap> E fromDocument(Document document, MapProvider<E> provider) {
            var map = provider.emptyMap();
            map.load(document);
            return map;
        }

        @Override public LobbyMap fromDocument(Document document) {
            return fromDocument(document, MapProvider.DEFAULT_LOBBY);
        }

        @Override public <E extends LobbyMap> E fromBoth(String gamemode, String id, MapProvider<E> provider) {
            var map = provider.emptyMap();

            var document = collection.find(MapUtil.getFilter(gamemode, id)).first();

            if(document == null) { return null; }
            map.load(document);

            return map;
        }

        @Override public LobbyMap fromBoth(String gamemode, String id) {
            return fromBoth(gamemode, id, MapProvider.DEFAULT_LOBBY);
        }

        @Override public <E extends LobbyMap> E fromActiveGame(String id, MapProvider<E> provider) {
            return fromBoth(Slime.getActiveGame().getId(), id, provider);
        }

        @Override public LobbyMap fromActiveGame(String id) {
            return fromActiveGame(id, MapProvider.DEFAULT_LOBBY);
        }

        @Override public <E extends LobbyMap> E getRandomMap(String gamemode, MapProvider<E> provider) {
            gamemode = gamemode.isBlank() ? Slime.getActiveGame().getId() : gamemode;

            var map = provider.emptyMap();

            var document = collection.aggregate(List.of(
                    Aggregates.match(Filters.eq("gamemode", gamemode)),
                    Aggregates.sample(1))).first();

            if(document == null) { return null; }
            map.load(document);

            return map;
        }

        @Override public LobbyMap getRandomMap(String gamemode) {
            gamemode = gamemode.isBlank() ? Slime.getActiveGame().getId() : gamemode;
            return getRandomMap(gamemode, MapProvider.DEFAULT_LOBBY);
        }

        @Override
        public boolean isInDatabase(String gamemode, String id) {
            return collection.countDocuments(
                    Filters.and(
                            Filters.eq("_id", id),
                            Filters.eq("gamemode", gamemode)
                    ), new CountOptions().limit(1)) > 0;
        }
    };

}
