package cc.minetale.slime.core;

import cc.minetale.slime.Slime;
import cc.minetale.slime.map.GameMap;
import cc.minetale.slime.team.GameTeam;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.network.PlayerProvider;

import java.util.function.Supplier;

public abstract class GameExtension extends Extension {

    /** Parent instance for all lobbies' {@linkplain SharedInstance}s. */
    @Getter @Setter protected GameInstance lobbyInstance;

    public abstract String getId();
    public abstract String getName();

    public abstract PlayerProvider getPlayerProvider();
    public abstract Supplier<GameTeam> getTeamProvider();

    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();

    public abstract int getMaxGames();

    public abstract long getTimeLimit();

    /** Get a map for a lobby (usually from a pool of many) */
    public abstract GameMap getLobbyMap();

    /** Calls the default behavior before initialization of your own {@linkplain GameExtension} implementation. */
    public final void preInit() {
        if(Slime.TOOL_MANAGER.isEnabled())
            Slime.TOOL_MANAGER.addGame(this);
    }

    /** Calls the default behavior after initialization of your own {@linkplain GameExtension} implementation. */
    public final void postInit() {
        this.lobbyInstance = new GameInstance(getLobbyMap());
        Slime.CONNECTION_MANAGER.setPlayerProvider(getPlayerProvider());

        Slime.setActiveGame(this);
    }

}
