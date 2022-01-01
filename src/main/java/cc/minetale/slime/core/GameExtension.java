package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.MapProvider;
import cc.minetale.slime.map.tools.TempMap;
import cc.minetale.slime.team.ITeamType;
import cc.minetale.slime.team.TeamProvider;
import cc.minetale.slime.utils.Requirement;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.network.PlayerProvider;

import java.util.Set;

public abstract class GameExtension extends Extension {

    /** Parent instance for all lobbies' {@linkplain SharedInstance}s. */
    @Getter @Setter protected GameInstance lobbyInstance;

    public abstract String getId();
    public abstract String getName();

    public abstract PlayerProvider getPlayerProvider();
    public abstract TeamProvider getTeamProvider();
    public abstract MapProvider getMapProvider();

    public abstract Set<ITeamType> getTeamTypes();

    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();

    //TODO Use this in Game and move to GameManager
    public abstract long getTimeLimit();

    /** Get a map for next game. */
    public abstract GameMap getGameMap();
    /** Get a map for a lobby. */
    public abstract GameMap getLobbyMap();

    /**
     * Requirements a {@linkplain TempMap} must meet so it can be saved. <br>
     * You can create your own requirements if you feel like it, for example if you want zones, objectives, spawns with specific settings like IDs. <br>
     * Or use presets from {@linkplain Requirement.Map}.
     */
    public abstract Set<Requirement<TempMap>> getMapRequirements();

    /**
     * Calls the default behavior before initialization of your own {@linkplain GameExtension} implementation.
     * @return Whether to continue initializing this {@linkplain GameExtension} or not.
     */
    public final boolean preInit() {
        if(Slime.TOOL_MANAGER.isEnabled()) {
            Slime.TOOL_MANAGER.addGame(this);
            return false;
        }
        return true;
    }

    /** Calls the default behavior after initialization of your own {@linkplain GameExtension} implementation. */
    public final void postInit() {
        this.lobbyInstance = new GameInstance(getLobbyMap());
        Slime.CONNECTION_MANAGER.setPlayerProvider(getPlayerProvider());

        Slime.setActiveGame(this);
    }

}
