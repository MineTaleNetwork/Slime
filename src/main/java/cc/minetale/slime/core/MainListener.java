package cc.minetale.slime.core;

import cc.minetale.commonlib.util.MC;
import cc.minetale.mlib.util.ProfileUtil;
import cc.minetale.slime.Slime;
import cc.minetale.slime.attribute.Attribute;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerRespawnEvent;

import static cc.minetale.slime.state.PlayerState.SPECTATE;

public final class MainListener {

    static void registerEvents(GameManager gameManager) {
        var mainNode = EventNode.all("slime").setPriority(Integer.MAX_VALUE);
        registerDefaultEvents(mainNode, gameManager);
        registerGamePlayerEvents(mainNode, gameManager);

        var global = MinecraftServer.getGlobalEventHandler();
        global.addChild(mainNode);
    }

    private static void registerDefaultEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("default");

        node.addListener(PlayerLoginEvent.class, event -> {
            var player = (GamePlayer) event.getPlayer();

            Game game = gameManager.findGameOrCreate();
            if(game == null) {
                player.kick("Couldn't join the server.");
                return;
            }

            if(game.addPlayer(player)) {
                var profile = ProfileUtil.getAssociatedProfile(player).getNow(null);
                if(profile == null) {
                    player.kick("Couldn't join the server.");
                    return;
                }

                game.sendMessage(Component.text("» ", MC.CC.YELLOW.getTextColor())
                        .append(profile.api().getChatFormat())
                        .append(Component.text(" has joined! (", MC.CC.GOLD.getTextColor()))
                        .append(Component.text(game.getPlayers().size(), MC.CC.YELLOW.getTextColor()))
                        .append(Component.text("/", MC.CC.GOLD.getTextColor()))
                        .append(Component.text(Slime.getActiveGame().getMaxPlayers(), MC.CC.YELLOW.getTextColor()))
                        .append(Component.text(")", MC.CC.GOLD.getTextColor())));
            }

            event.setSpawningInstance(game.getSpawnInstance(player));
        });

        node.addListener(PlayerDisconnectEvent.class, event -> {
            var player = (GamePlayer) event.getPlayer();

            var game = player.getGame();
            if(game == null) { return; }

            game.removePlayer(player);

            var profile = ProfileUtil.getAssociatedProfile(player).getNow(null);
            if(profile == null) { return; }

            game.sendMessage(Component.text("» ", MC.CC.YELLOW.getTextColor())
                    .append(profile.api().getChatFormat())
                    .append(Component.text(" has left! (", MC.CC.GOLD.getTextColor()))
                    .append(Component.text(game.getPlayers().size(), MC.CC.YELLOW.getTextColor()))
                    .append(Component.text("/", MC.CC.GOLD.getTextColor()))
                    .append(Component.text(Slime.getActiveGame().getMaxPlayers(), MC.CC.YELLOW.getTextColor()))
                    .append(Component.text(")", MC.CC.GOLD.getTextColor())));
        });

        node.addListener(PlayerRespawnEvent.class, event -> {
            var player = event.getPlayer();

            player.setRespawnPoint(new Pos(0, 64, 0));
//            event.getPlayer().scheduleNextTick(futurePlayer -> futurePlayer.teleport()); TODO
        });

        node.addListener(PlayerDeathEvent.class, event -> {
            var player = (GamePlayer) event.getPlayer();

            var game = player.getGame();
            if(game == null) { return; }

            if(player.<Boolean>getAttribute(Attribute.AUTO_LOSE_LIVES))
                player.setLives(player.getLives() - 1);

            //After this event the player will be dead
            var willDie = player.getLives() == 0;

            if(player.<Integer>getAttribute(Attribute.RESPAWN_TIME) == 0) {
                if(player.<Boolean>getAttribute(Attribute.AUTO_TEMP_SPECTATOR))
                    //TODO Automatic death spectator

                return;
            }

            if(player.<Boolean>getAttribute(Attribute.AUTO_PERM_SPECTATOR))
                player.setState(SPECTATE);
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
