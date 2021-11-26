package cc.minetale.slime.utils;

import cc.minetale.slime.map.GameMap;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;

public class MapUtil {

    public static boolean isInDatabase(String gamemode, String id) {
        return GameMap.getCollection().countDocuments(
                Filters.and(
                        Filters.eq("_id", id),
                        Filters.eq("gamemode", gamemode)
                ), new CountOptions().limit(1)) > 0;
    }

}
