package cc.minetale.slime.core;

import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import cc.minetale.slime.rule.PlayerRule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static cc.minetale.slime.player.PlayerState.SPECTATE;

public final class MainListener {

    public static void registerEvents(GameManager gameManager) {
        var mainNode = EventNode.all("slime").setPriority(Integer.MAX_VALUE);
        registerDefaultEvents(mainNode, gameManager);
        registerGamePlayerEvents(mainNode, gameManager);

        var global = MinecraftServer.getGlobalEventHandler();
        global.addChild(mainNode);
    }

    private static void registerDefaultEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("default");

        node.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            try {
                var game = gameManager.findGameOrCreate().get(15000, TimeUnit.MILLISECONDS);
                if(game == null) {
                    player.kick("Couldn't join the server.");
                    return;
                }

                game.addPlayer(player);
            } catch(InterruptedException | ExecutionException | TimeoutException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();

                player.kick("Couldn't join the server.");
            }
        });

        node.addListener(PlayerLoginEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var game = player.getGame();
            if(game == null) {
                player.kick("Couldn't join the server.");
                return;
            }

            event.setSpawningInstance(game.getSpawnInstance(player));
            player.setRespawnPoint(new Pos(0, 64, 0));
//            event.getPlayer().scheduleNextTick(futurePlayer -> futurePlayer.teleport()); TODO
        });
        
        node.addListener(PlayerSpawnEvent.class, event -> {
            if(!event.isFirstSpawn()) { return; }
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var profile = player.getProfile();
            if(profile == null) {
                player.kick("Couldn't join the server.");
                return;
            }
            
            var game = player.getGame();
            if(game == null) {
                player.kick("Couldn't join the server.");
                return;
            }

            game.sendMessage(Component.text().append(
                    Component.text("\u00bb ", NamedTextColor.YELLOW),
                    profile.getChatFormat(),
                    Component.text(" has joined! (", NamedTextColor.GOLD),
                    Component.text(game.getPlayers().size(), NamedTextColor.YELLOW),
                    Component.text("/", NamedTextColor.GOLD),
                    Component.text(game.getMaxPlayers(), NamedTextColor.YELLOW),
                    Component.text(")", NamedTextColor.GOLD))
            );
        });

        node.addListener(PlayerDisconnectEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var game = player.getGame();
            if(game == null) { return; }

            game.removePlayer(player);

            var profile = player.getProfile();
            if(profile == null) { return; }

            game.sendMessage(Component.text().append(
                    Component.text("\u00bb ", NamedTextColor.YELLOW),
                    profile.getChatFormat(),
                    Component.text(" has left! (", NamedTextColor.GOLD),
                    Component.text(game.getPlayers().size(), NamedTextColor.YELLOW),
                    Component.text("/", NamedTextColor.GOLD),
                    Component.text(game.getMaxPlayers(), NamedTextColor.YELLOW),
                    Component.text(")", NamedTextColor.GOLD))
            );
        });

        //TODO Not needed? (Previously used for setting respawn point)
        node.addListener(PlayerRespawnEvent.class, event -> {
            var player = event.getPlayer();
        });

        node.addListener(PlayerDeathEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var game = player.getGame();
            if(game == null) { return; }

            if(player.getRuleOrDefault(PlayerRule.AUTO_LOSE_LIVES))
                player.setLives(player.getLives() - 1);

            //Will the player will be set to spectator?
            var eliminated = player.getLives() < 0;

            if(!eliminated) {
                if(player.getRuleOrDefault(PlayerRule.RESPAWN_TIME) > 0) {
                    if(player.getRuleOrDefault(PlayerRule.AUTO_DEATHCAM))
                        player.setState(PlayerState.DEATHCAM);
                    //TODO Automatic deathcam

                    return;
                }
            } else {
                if(player.getRuleOrDefault(PlayerRule.AUTO_SPECTATOR))
                    player.setState(PlayerState.SPECTATE);
                //TODO Automatic spectator
            }
        });

        node.addListener(PlayerMoveEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            var freezeType = player.getRuleOrDefault(PlayerRule.FROZEN);

            final var oldPos = player.getPosition();
            final var newPos = event.getNewPosition();

            switch(freezeType) {
                case BOTH -> event.setCancelled(true);
                case POSITION -> event.setNewPosition(oldPos.withView(newPos.yaw(), newPos.pitch()));
                case ANGLES -> event.setNewPosition(newPos.withView(oldPos.yaw(), oldPos.pitch()));
                case NONE -> {}
            }
        });

        mainNode.addChild(node);
    }

    private static void registerGamePlayerEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("gamePlayer");

        node.addListener(GamePlayerStateChangeEvent.class, event -> {
            var player = event.getGamePlayer();

            var gamePlayer = event.getGamePlayer();
            var newState = event.getNewState();

            if(SPECTATE.equals(newState)) {
                //TODO Set spectator
            }
        });

        mainNode.addChild(node);
    }

}
