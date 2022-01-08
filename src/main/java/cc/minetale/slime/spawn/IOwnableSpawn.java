package cc.minetale.slime.spawn;

//TODO ? Figure out if you can merge methods using GameTeam and ITeamType so you can move them here
public interface IOwnableSpawn {
    void clearOwners();

    boolean isOwned();
    boolean isShared();
}
