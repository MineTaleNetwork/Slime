package cc.minetale.slime.utils;

import cc.minetale.slime.map.GameMap;
import com.mongodb.client.model.Filters;

public class MapUtil {

    public static boolean isInDatabase(String id, String gamemode) {
        return GameMap.getCollection().countDocuments(Filters.and(
                Filters.eq("_id", id),
                Filters.eq("gamemode", gamemode))) > 0;
    }

}
