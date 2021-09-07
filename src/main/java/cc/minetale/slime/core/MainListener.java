package cc.minetale.slime.core;

import cc.minetale.slime.event.player.GamePlayerJoinEvent;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;

public final class MainListener {

    public static void registerEvents(GameManager gameManager) {
        EventNode<PlayerEvent> playerNode = EventNode.type("slime", EventFilter.PLAYER);

        playerNode.addListener(PlayerLoginEvent.class, event -> {
            var player = event.getPlayer();

            Game game = gameManager.findGameOrCreate();
            if(game == null) {
                player.kick("Couldn't join the server.");
                return;
            }

            var gamePlayer = game.createPlayer(player);

            game.addPlayer(gamePlayer);
            EventDispatcher.call(new GamePlayerJoinEvent(game, gamePlayer));
        });

        playerNode.addListener(PlayerDisconnectEvent.class, event -> {
            var player = event.getPlayer();

        });

    }

}
