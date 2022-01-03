package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.lobby.LobbyInstance;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.map.LobbyMap;
import cc.minetale.slime.map.MapProvider;
import cc.minetale.slime.map.MapResolver;
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

import static cc.minetale.slime.Slime.TOOL_MANAGER;

public abstract class GameExtension extends Extension {

    /** Parent instance for all lobbies' {@linkplain SharedInstance}s. */
    @Getter @Setter protected LobbyInstance lobbyInstance;

    public abstract String getId();
    public abstract String getName();

    public abstract PlayerProvider getPlayerProvider();
    public abstract TeamProvider getTeamProvider();

    public abstract <T extends GameMap> MapProvider<T> getGameMapProvider();
    public abstract <T extends GameMap> MapResolver<T> getGameMapResolver();

    public abstract <T extends LobbyMap> MapProvider<T> getLobbyMapProvider();
    public abstract <T extends LobbyMap> MapResolver<T> getLobbyMapResolver();

    public abstract Set<ITeamType> getTeamTypes();

    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();

    //TODO Use this in Game and move to GameManager
    public abstract long getTimeLimit();

    /** Get a map for next game. */
    public abstract GameMap getGameMap();
    /** Get a map for a lobby. */
    public abstract LobbyMap getLobbyMap();

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
        if(TOOL_MANAGER.isEnabled()) {
            TOOL_MANAGER.addGame(this);
            return false;
        }
        return true;
    }

    /** Calls the default behavior after initialization of your own {@linkplain GameExtension} implementation. */
    public final void postInit() {
        this.lobbyInstance = new LobbyInstance(getLobbyMap());
        Slime.CONNECTION_MANAGER.setPlayerProvider(getPlayerProvider());

        Slime.setActiveGame(this);
    }

}
