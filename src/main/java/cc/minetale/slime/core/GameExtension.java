package cc.minetale.slime.core;

import cc.minetale.slime.team.GameTeam;
import net.minestom.server.extensions.Extension;
import net.minestom.server.network.PlayerProvider;

import java.util.function.Supplier;

public abstract class GameExtension extends Extension {

    public abstract String getId();
    public abstract String getName();

    public abstract PlayerProvider getPlayerProvider();
    public abstract Supplier<? extends GameTeam> getTeamProvider();

    public abstract int getMinPlayers();
    public abstract int getMaxPlayers();

    public abstract int getMaxGames();

    public abstract long getTimeLimit();

}
