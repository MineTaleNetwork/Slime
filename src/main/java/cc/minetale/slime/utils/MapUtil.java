package cc.minetale.slime.utils;

import cc.minetale.slime.map.GameMap;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

public class MapUtil {

    public static boolean isInDatabase(String gamemode, String id) {
        return GameMap.getCollection().countDocuments(
                Filters.and(
                        Filters.eq("_id", id),
                        Filters.eq("gamemode", gamemode)
                ), new CountOptions().limit(1)) > 0;
    }

    public static Bson getFilter(String gamemode, String id) {
        return Filters.and(
                Filters.eq("_id", id),
                Filters.eq("gamemode", gamemode));
    }

    /**
     * Full ID of a map containing both map's gamemode and ID. <br>
     * It's only used for display purposes.
     */
    public static String getFullId(GameMap map) {
        return map.getGamemode() + ":" + map.getId();
    }

    public static boolean isSpawnIdAvailable(GameMap map, String spawnId) {
        return !map.getSpawnPoints().containsKey(spawnId);
    }

}
