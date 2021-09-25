package cc.minetale.slime.core;

import net.minestom.server.extensions.Extension;
import net.minestom.server.network.PlayerProvider;

public abstract class GameExtension extends Extension {

    public abstract String getId();
    public abstract String getName();

    public abstract PlayerProvider getPlayerProvider();

    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();

    public abstract int getMaxGames();

    public abstract long getTimeLimit();

}
