package cc.minetale.slime.core;

import cc.minetale.slime.attribute.Attribute;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;

public final class MainListener {

    static void registerEvents(GameManager gameManager) {
        EventNode<PlayerEvent> slimeNode = EventNode.type("slime", EventFilter.PLAYER);

        slimeNode.addListener(PlayerLoginEvent.class, event -> {
            var player = event.getPlayer();

            Game game = gameManager.findGameOrCreate();
            if(game == null) {
                player.kick("Couldn't join the server.");
                return;
            }

            var gamePlayer = game.createPlayer(player);

            game.addPlayer(gamePlayer);
        });

        slimeNode.addListener(PlayerDisconnectEvent.class, event -> {
            var handle = event.getPlayer();

            var gamePlayer = GamePlayer.getWrapper(handle);
            if(gamePlayer == null) { return; }

            var game = gamePlayer.getGame();
            if(game == null) { return; }

            game.removePlayer(gamePlayer);
        });

        slimeNode.addListener(PlayerDeathEvent.class, event -> {
            var handle = event.getPlayer();

            var gamePlayer = GamePlayer.getWrapper(handle);
            if(gamePlayer == null) { return; }

            var game = gamePlayer.getGame();
            if(game == null) { return; }

            if(gamePlayer.<Integer>getAttribute(Attribute.RESPAWN_TIME) == 0) { return; }

            gamePlayer.setState(PlayerState.SPECTATE);
        });

    }

}
