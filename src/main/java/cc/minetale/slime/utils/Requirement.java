package cc.minetale.slime.utils;

import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.tools.TempMap;
import cc.minetale.slime.spawn.BaseSpawn;
import cc.minetale.slime.team.ITeamType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

@AllArgsConstructor
public final class Requirement<T> {

    @Getter private String name;
    @Getter private String description;
    private Predicate<T> condition;

    //TODO Incompatible requirements

    public boolean doesMeetRequirement(T obj) {
        return this.condition.test(obj);
    }

    public static final class Map {

        private Map() {}

        /**
         * Creates a new MIN_SPAWN_X requirement.
         * @param min Minimum amount of spawns required (inclusive)
         */
        public static Requirement<TempMap> minSpawnsRequirement(int min) {
            return new Requirement<>(
                    "MIN_SPAWN_" + min,
                    "There should be at least " + min + " spawn(s).",
                    map -> {
                        if(!(map.getHandle() instanceof GameMap handle)) { return false; }
                        return handle.getSpawns().size() >= min;
                    });
        }

        /**
         * This is usually not required as normally spawns are picked automatically for public games <br>
         * and players can pick their own teams in private games. <br>
         * <br>
         * Otherwise, a {@linkplain Requirement.Map#ALL_SPAWNS_OWNED} might be a better alternative in certain cases.
         */
        public static Requirement<TempMap> minSpawnsPerTeam(int min) {
            return new Requirement<>(
                    "SPAWN_PER_TEAM_" + min,
                    "Each team should own at least " + min + " usable spawn(s). " +
                            "(Make sure to check if any spawns aren't unexpectedly owned by some team(s))",
                    map -> {
                        if(!(map.getHandle() instanceof GameMap handle)) { return false; }
                        Collection<BaseSpawn> spawns = handle.getSpawns().values();

                        var game = map.getGame();
                        Set<ITeamType> teams = game.getTeamTypes();

                        return MapUtil.allTeamsHaveSpawn(spawns, teams, min);
                    });
        }

        public static Requirement<TempMap> minUnownedSpawns(int min) {
            return new Requirement<>(
                            "MIN_UNOWNED_SPAWN_" + min,
                            "There should be at least " + min + " unowned spawn(s).",
                            map -> {
                                if(!(map.getHandle() instanceof GameMap handle)) { return false; }
                                Collection<BaseSpawn> spawns = handle.getSpawns().values();

                                int unowned = 0;
                                for(BaseSpawn spawn : spawns) {
                                    if(!spawn.isOwned()) { unowned++; }
                                }
                                return unowned >= min;
                            });
        }

        public static final Requirement<TempMap> ALL_SPAWNS_OWNED =
                new Requirement<>(
                        "ALL_SPAWNS_OWNED",
                        "All spawns should be owned by any team.",
                        map -> {
                            if(!(map.getHandle() instanceof GameMap handle)) { return false; }
                            Collection<BaseSpawn> spawns = handle.getSpawns().values();

                            return spawns.stream().allMatch(BaseSpawn::isOwned);
                        });

        /** Should most likely be used for gamemodes where teams are loose and what team gets what spawn doesn't matter. (e.g. WoolWars) */
        public static final Requirement<TempMap> ALL_SPAWNS_NOT_OWNED =
                new Requirement<>(
                        "ALL_SPAWNS_NOT_OWNED",
                        "All spawns shouldn't be owned by any team.",
                        map -> {
                            if(!(map.getHandle() instanceof GameMap handle)) { return false; }
                            Collection<BaseSpawn> spawns = handle.getSpawns().values();

                            return spawns.stream().noneMatch(BaseSpawn::isOwned);
                        });
    }

}
