package cc.minetale.slime.map;

import cc.minetale.buildingtools.Selection;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

public interface MapProvider {

    MapProvider DEFAULT = new MapProvider() {
        @Override
        public @NotNull GameMap createMap(String id, String name, String gamemode, NamespaceID dimension, Selection playArea) {
            return new GameMap(id, name, gamemode, dimension, playArea);
        }

        @Override
        public @NotNull GameMap emptyMap() {
            return new GameMap();
        }
    };

    @NotNull GameMap createMap(String id, String name, String gamemode, NamespaceID dimension, Selection playArea);
    @NotNull GameMap emptyMap();

}