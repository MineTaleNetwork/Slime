package cc.minetale.slime.map;

import cc.minetale.buildingtools.Selection;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public interface MapProvider<T extends GameMap> {

    MapProvider<GameMap> DEFAULT = new MapProvider<>() {
        @Override
        public @NotNull GameMap createMap(String id, String name, String gamemode, NamespaceID dimension, Selection playArea) {
            return new GameMap(id, name, gamemode, dimension, playArea);
        }

        @Override
        public @NotNull GameMap emptyMap() {
            return new GameMap();
        }
    };

    @NotNull T createMap(String id, String name, String gamemode, NamespaceID dimension, Selection playArea);
    @NotNull T emptyMap();

}