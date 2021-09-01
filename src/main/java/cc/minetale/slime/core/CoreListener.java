package cc.minetale.slime.core;

import cc.minetale.slime.events.GameJoinEvent;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;

public final class CoreListener<G extends Game<G,P,S>, P extends GamePlayer<P,S,G>, S extends GameState<S,P,G>> {

    public void registerEvents(GameManager<G,P,S> gameManager) {
        // Can only listen to player events
        EventNode<PlayerEvent> playerNode = EventNode.type("slime", EventFilter.PLAYER);

        playerNode.addListener(PlayerLoginEvent.class, event -> {
            var player = event.getPlayer();

            G game = gameManager.findGameOrCreate();
            if(game == null || !game.canJoin(player)) {
                player.kick("Couldn't join the server.");
                return;
            }

            var gamePlayer = game.createPlayer(player);

            game.getLobby().addPlayer(game, gamePlayer);
            EventDispatcher.call(new GameJoinEvent<>(game, gamePlayer));
        });

    }

}
