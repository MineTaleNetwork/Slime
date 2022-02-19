package cc.minetale.slime.utils;

import cc.minetale.slime.Slime;
import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.lobby.LobbyInstance;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.spawn.MapSpawn;
import lombok.experimental.UtilityClass;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static cc.minetale.slime.Slime.TOOL_MANAGER;

@UtilityClass
public class GameUtil {

    public static boolean initialize(@NotNull GameInfo info) {
        Slime.registerGame(info);
        return !TOOL_MANAGER.isEnabled();
    }

    public static void setPlayerProvider(@NotNull GameInfo info) {
        Slime.CONNECTION_MANAGER.setPlayerProvider(info.getPlayerProvider());
    }

    public static LobbyInstance createLobby(@NotNull GameInfo info) {
        final var gameManager = info.getGameManager();
        if(gameManager == null) { return null; }

        var lobbyMap = gameManager.getLobbyMap();
        return new LobbyInstance(lobbyMap); //TODO Set in GameInfo?
    }

    public static List<GameSpawn> simpleSpawnConversion(List<MapSpawn> mapSpawns, Instance instance) {
        List<GameSpawn> gameSpawns = new LinkedList<>();
        for(var mapSpawn : mapSpawns) {
            var gameSpawn = new GameSpawn(mapSpawn, instance);
            gameSpawns.add(gameSpawn);
        }
        return gameSpawns;
    }

}
