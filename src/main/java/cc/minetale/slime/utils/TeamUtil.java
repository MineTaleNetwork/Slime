package cc.minetale.slime.utils;

import cc.minetale.slime.team.ITeamType;

import java.util.Collection;

public class TeamUtil {

    public static <T extends ITeamType> T findById(Collection<T> teams, String id) {
        for(T team : teams) {
            if(team.getId().equals(id)) { return team; }
        }
        return null;
    }

}
