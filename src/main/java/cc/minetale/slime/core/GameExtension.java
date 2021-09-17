package cc.minetale.slime.core;

import net.minestom.server.extensions.Extension;

public abstract class GameExtension extends Extension {

    public abstract String getId();
    public abstract String getName();

    public abstract int getMaxPlayers();
    public abstract int getMaxGames();

    public abstract long getTimeLimit();

}
