package cc.minetale.slime.utils;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.Slime;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.spawn.MapSpawn;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.tools.TempMap;
import com.mongodb.client.model.Filters;
import lombok.experimental.UtilityClass;
import org.bson.conversions.Bson;

import java.util.*;

@UtilityClass
public final class MapUtil {

    public static boolean isMapInDatabase(AbstractMap.Type type, String gamemode, String id) {
        var game = Slime.getRegisteredGame(gamemode);
        if(game == null) { return false; }

        return type.getResolver(game).isInDatabase(gamemode, id);
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
    public static String getFullId(String gamemode, String id) {
        return gamemode + ":" + id;
    }

    /**
     * Full ID of a map containing both map's gamemode and ID. <br>
     * It's only used for display purposes.
     */
    public static String getFullId(AbstractMap map) {
        return getFullId(map.getGamemode(), map.getId());
    }

    public static String getFullId(TempMap tempMap) {
        return getFullId(tempMap.getHandle());
    }

    public static boolean isSpawnIdAvailable(GameMap map, String spawnId) {
        return !map.getSpawns().containsKey(spawnId);
    }

    /**
     * Checks if the {@linkplain TempMap} has at least x usable spawns for each team. <br>
     * Owned spawns can have different meanings per game and thus this method may not be always suitable for your use case. <br>
     * This is because as far as this method is concerned, an owned spawn can't be used by any other team other than the owners,
     * which may not be the cause for all gamemodes, also some gamemodes may change ownerships during the game. (like capturing spawns for your own team)<br>
     * <br>
     * See {@linkplain Requirement.Map#minSpawnsPerTeam(int)}
     * @param spawnsRequired How many spawns should each team have minimum (inclusive)
     */
    public static boolean allTeamsHaveSpawn(Collection<MapSpawn> spawns, List<ITeamType> teams, int spawnsRequired) {
        spawns = new ArrayList<>(spawns);

        Map<ITeamType, Integer> teamsLeft = new HashMap<>();
        CollectionsUtil.fill(teamsLeft, teams, spawnsRequired);

        //First "calculate" all teams that own any spawn
        for(final var it = spawns.iterator(); it.hasNext();) {
            var spawn = it.next();
            if(spawn.isOwned()) {
                Set<ITeamType> owners = spawn.getOwners();
                for(ITeamType owner : owners) {
                    if(teamsLeft.containsKey(owner)) {
                        teamsLeft.put(owner, teamsLeft.get(owner) - 1);
                    }
                }

                it.remove();
            }
        }

        //"Calculate" teams based on how many unowned spawns are left
        var spawnsLeft = spawns.size();
        for(Map.Entry<ITeamType, Integer> ent : teamsLeft.entrySet()) {
            int value = ent.getValue();
            if(value <= 0) { continue; }

            ent.setValue(value - 1);

            if(--spawnsLeft <= 0) { break; }
        }

        return teamsLeft.values().stream()
                .allMatch(spawnsMissing -> spawnsMissing == 0);
    }

}
