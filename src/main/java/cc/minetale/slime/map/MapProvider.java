package cc.minetale.slime.map;

import cc.minetale.buildingtools.Selection;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public interface MapProvider<T extends AbstractMap> {

    T createMap(String id,
                String name,
                String gamemode,
                NamespaceID dimension,
                Selection selection);

    T createMap(String id,
                String name,
                String gamemode,
                NamespaceID dimension,
                Vec minPos, Vec maxPos);

    T emptyMap();

    MapProvider<GameMap> DEFAULT_GAME = new MapProvider<>() {
        @Override
        public @NotNull GameMap createMap(String id,
                                          String name,
                                          String gamemode,
                                          NamespaceID dimension,
                                          Selection selection) {

            return createMap(id, name, gamemode, dimension, selection.getMinPos(), selection.getMaxPos());
        }

        @Override
        public @NotNull GameMap createMap(String id,
                                          String name,
                                          String gamemode,
                                          NamespaceID dimension,
                                          Vec minPos, Vec maxPos) {

            return new GameMap(id, name, gamemode, dimension, minPos, maxPos);
        }

        @Override
        public @NotNull GameMap emptyMap() {
            return new GameMap();
        }
    };

    MapProvider<LobbyMap> DEFAULT_LOBBY = new MapProvider<>() {
        @Override
        public @NotNull LobbyMap createMap(String id,
                                           String name,
                                           String gamemode,
                                           NamespaceID dimension,
                                           Selection selection) {

            return new LobbyMap(id, gamemode, dimension, selection.getMinPos(), selection.getMaxPos());
        }

        @Override
        public @NotNull LobbyMap createMap(String id,
                                           String name,
                                           String gamemode,
                                           NamespaceID dimension,
                                           Vec minPos, Vec maxPos) {

            return new LobbyMap(id, gamemode, dimension, minPos, maxPos);
        }

        @Override
        public @NotNull LobbyMap emptyMap() {
            return new LobbyMap();
        }
    };

}