package cc.minetale.slime.core;

import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerRespawnEvent;

import static cc.minetale.slime.core.PlayerState.SPECTATE;

public final class MainListener {

    static void registerEvents(GameManager gameManager) {
        var mainNode = EventNode.all("slime");
        registerDefaultEvents(mainNode, gameManager);

        var global = MinecraftServer.getGlobalEventHandler();
        global.addChild(mainNode);
    }

    private static void registerDefaultEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("default");

        node.addListener(PlayerLoginEvent.class, event -> {
            var player = event.getPlayer();

            Game game = gameManager.findGameOrCreate();
            if(game == null) {
                player.kick("Couldn't join the server.");
                return;
            }

            var gamePlayer = game.createPlayer(player);

            game.addPlayer(gamePlayer);

            event.setSpawningInstance(game.getSpawnInstance(gamePlayer));
        });

        node.addListener(PlayerDisconnectEvent.class, event -> {
            var handle = event.getPlayer();

            var gamePlayer = GamePlayer.getWrapper(handle);
            if(gamePlayer == null) { return; }

            var game = gamePlayer.getGame();
            if(game == null) { return; }

            game.removePlayer(gamePlayer);
        });

        node.addListener(PlayerRespawnEvent.class, event -> {
            var player = event.getPlayer();
            var gamePlayer = GamePlayer.getWrapper(player);

            event.getPlayer().setRespawnPoint(new Pos(0, 64, 0));
//            event.getPlayer().scheduleNextTick(futurePlayer -> futurePlayer.teleport());
        });

        node.addListener(PlayerDeathEvent.class, event -> {
            var handle = event.getPlayer();

            var gamePlayer = GamePlayer.getWrapper(handle);
            if(gamePlayer == null) { return; }

            var game = gamePlayer.getGame();
            if(game == null) { return; }

            if(gamePlayer.<Boolean>getAttribute(Attribute.AUTO_LOSE_LIVES))
                gamePlayer.setLives(gamePlayer.getLives() - 1);

            var hasDied = gamePlayer.getLives() < 0;

            if(gamePlayer.<Integer>getAttribute(Attribute.RESPAWN_TIME) == 0) {
                if(gamePlayer.<Boolean>getAttribute(Attribute.AUTO_DEATH_SPECTATOR))
                    //TODO Automatic death spectator

                return;
            }

            if(gamePlayer.<Boolean>getAttribute(Attribute.AUTO_DEATH_SPECTATOR))
                gamePlayer.setState(SPECTATE);
        });

        mainNode.addChild(node);
    }

    private static void registerGamePlayerEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("gamePlayer");

        node.addListener(GamePlayerStateChangeEvent.class, event -> {
            var player = event.getPlayer();

            var gamePlayer = event.getGamePlayer();
            var newState = event.getNewState();

            if(SPECTATE.equals(newState)) {
                //TODO Set spectator
            }
        });

        mainNode.addChild(node);
    }

}
