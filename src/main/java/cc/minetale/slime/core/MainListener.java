package cc.minetale.slime.core;

import cc.minetale.commonlib.util.Colors;
import cc.minetale.slime.entity.IInventoryHolder;
import cc.minetale.slime.event.player.GamePlayerStateChangeEvent;
import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.loadout.DefaultLoadouts;
import cc.minetale.slime.loadout.ILoadoutHolder;
import cc.minetale.slime.loadout.LoadoutHandlers.EventHandler.Action;
import cc.minetale.slime.perceive.PerceiveTeam;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import cc.minetale.slime.rule.PlayerRule;
import cc.minetale.slime.utils.MiscUtil;
import cc.minetale.slime.misc.restriction.RestrictionList;
import io.github.bloepiloepi.pvp.PvpExtension;
import io.github.bloepiloepi.pvp.events.EntityPreDeathEvent;
import io.github.bloepiloepi.pvp.events.PlayerExhaustEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryItemChangeEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.inventory.click.ClickType;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static cc.minetale.slime.Slime.INSTANCE_MANAGER;
import static cc.minetale.slime.player.PlayerState.SPECTATE;

public final class MainListener {

    public static void registerEvents(GameManager gameManager) {
        var mainNode = EventNode.all("slime").setPriority(Integer.MAX_VALUE);
        registerDefaultEvents(mainNode, gameManager);
        registerGamePlayerEvents(mainNode, gameManager);
        registerPvpEvents(mainNode, gameManager);
        registerLoadoutEvents(mainNode, gameManager);

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

            //Perceive Teams
            PerceiveTeam.REGISTERED.forEach(team -> {
                var packet = team.getTeamCreationPacket();
                player.sendPacket(packet);
            });

            var state = game.getState();
            if(state.inLobby())
                player.setState(PlayerState.LOBBY);

            for(var otherPlayer : game.getPlayers()) {
                if(player == otherPlayer) { continue; }
                player.refreshTarget(otherPlayer);
            }

            game.sendMessage(Component.text().append(
                    Component.text("\u00bb ", Colors.BLUE),
                    profile.getChatFormat(),
                    Component.text(" has joined! (", Colors.DARK_BLUE),
                    Component.text(game.getPlayers().size(), Colors.BLUE),
                    Component.text("/", Colors.DARK_BLUE),
                    Component.text(game.getMaxPlayers(), Colors.BLUE),
                    Component.text(")", Colors.DARK_BLUE))
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
                    Component.text("\u00bb ", Colors.BLUE),
                    profile.getChatFormat(),
                    Component.text(" has left! (", Colors.DARK_BLUE),
                    Component.text(game.getPlayers().size(), Colors.BLUE),
                    Component.text("/", Colors.DARK_BLUE),
                    Component.text(game.getMaxPlayers(), Colors.BLUE),
                    Component.text(")", Colors.DARK_BLUE))
            );
        });

        node.addListener(PlayerDeathEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var game = player.getGame();
            if(game == null || game.getState().inLobby()) { return; }

            if(player.getRuleOrDefault(PlayerRule.AUTO_LOSE_LIVES))
                player.setLives(player.getLives() - 1);

            //Will the player will be set to spectator?
            var eliminated = player.getLives() < 0;

            if(!eliminated) {
                //Check if it's not instant, then check and set deathcam
                if(!player.getRuleOrDefault(PlayerRule.RESPAWN_TIME).isZero()) {
                    if(player.getRuleOrDefault(PlayerRule.AUTO_DEATHCAM)) {
//                        player.setState(PlayerState.DEATHCAM);
                    }
                    //TODO Automatic deathcam
                }
            } else {
                var spectatorSettings = player.getRuleOrDefault(PlayerRule.SPECTATOR);
                if(spectatorSettings.autoEnable()) {
//                    player.setState(PlayerState.SPECTATE);
                    player.setRespawnPoint(player.getPosition());
                }
                //TODO Automatic spectator
            }

            if(player.getRuleOrDefault(PlayerRule.DROP_ITEMS_ON_DEATH) != null) {
                MiscUtil.dropItemsByDeath(player);
            }

            if(!player.getRuleOrDefault(PlayerRule.KEEP_INVENTORY)) {
                player.getInventory().clear();
            }

            player.setLastDeathTime(System.currentTimeMillis());
        });

        //TODO Was used for setting the respawn point previously
        node.addListener(PlayerRespawnEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var respawnTime = player.getRuleOrDefault(PlayerRule.RESPAWN_TIME);
            var timeElapsed = player.getLastDeathTime() + System.currentTimeMillis();
            if(!respawnTime.isZero() && respawnTime.toMillis() > timeElapsed) {
                player.setState(PlayerState.DEATHCAM);
            }

            var spectatorSettings = player.getRuleOrDefault(PlayerRule.SPECTATOR);

            if(!player.isAlive() && spectatorSettings.autoEnable()) {
                player.setState(PlayerState.SPECTATE);
                DefaultLoadouts.SPECTATOR.setFor(player);
            }
        });

        node.addListener(EntityPreDeathEvent.class, event -> {
            event.setCancelled(true);
            if(event.getEntity() instanceof GamePlayer player) {
                player.sendMessage("kys now");
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

        node.addListener(ItemDropEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            var instance = player.getInstance();
            if (instance == null) { return; }

            var settings = player.getRuleOrDefault(PlayerRule.DROP_ITEMS);
            if(settings == null) {
                event.setCancelled(true);
                return;
            }

            var droppedItem = event.getItemStack();
            if(settings.modifiesDrop())
                droppedItem = settings.modifyDrop(player, droppedItem);

            var itemEntity = settings.getDropItemEntity(player, droppedItem);
            var dropPosition = settings.getDropPosition(player, droppedItem);
            itemEntity.setInstance(instance, dropPosition);
            var dropVelocity = settings.getDropVelocity(player, droppedItem);
            itemEntity.setVelocity(dropVelocity);
            var dropPickupDelay = settings.getDropPickupDelay(player, droppedItem);
            itemEntity.setPickupDelay(dropPickupDelay);
        });

        node.addListener(EventListener.builder(PickupItemEvent.class)
                .filter(event -> event.getEntity() instanceof GamePlayer)
                .handler(event -> {
                    var player = (GamePlayer) event.getEntity();

                    if(!player.getRuleOrDefault(PlayerRule.PICKUP_ITEMS)) {
                        event.setCancelled(true);
                        return;
                    }

                    var inventory = player.getInventory();
                    var couldAdd = inventory.addItemStack(event.getItemStack());
                    event.setCancelled(!couldAdd);
                })
                .build()
        );

        node.addListener(InventoryPreClickEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            player.updateViewableRule();

            RestrictionList<ClickType> restricted = player.getRuleOrDefault(PlayerRule.INVENTORY_CLICK);
            if(restricted.isRestricted(event.getClickType())) { event.setCancelled(true); }
        });

        //TODO Also disable entity interactions?
        node.addListener(PlayerBlockInteractEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            var settings = player.getRuleOrDefault(PlayerRule.INTERACT);
            if(!settings.canInteractWith(event.getBlock())) {
                event.setCancelled(true);
                return;
            }

            var hand = event.getHand();
            var item = player.getItemInHand(hand);
            if(!settings.canInteractUsing(item.getMaterial()))
                event.setCancelled(true);
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

    private static void registerPvpEvents(EventNode<Event> mainNode, GameManager gameManager) {
        PvpExtension.init();

        var node = EventNode.all("pvp");
        node.addChild(PvpExtension.events());

        node.addListener(PlayerExhaustEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());

            var settings = player.getRuleOrDefault(PlayerRule.HUNGER);

            var hungerMultiplier = settings.getHungerMultiplier();
            if(hungerMultiplier == 0) {
                event.setCancelled(true);
                return;
            }

            event.setAmount(event.getAmount() * hungerMultiplier);
        });

        mainNode.addChild(node);
    }

    private static void registerLoadoutEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("loadout");

        node.addListener(InventoryPreClickEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            var loadout = player.getLoadout();
            var result = loadout.getHandlers().onEvent(player, Action.CLICK, event.getSlot(), event.getClickedItem());

            event.setClickedItem(result.itemStack());

            var cancel = result.cancel();
            if(cancel != TriState.NOT_SET) { event.setCancelled(cancel == TriState.TRUE); }
        });

        node.addListener(PlayerUseItemEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            var hand = event.getHand();
            var oldItemStack = player.getItemInHand(hand);

            var loadout = player.getLoadout();
            var result = loadout.getHandlers().onEvent(player, Action.INTERACT, player.getHeldSlot(), oldItemStack);

            var newItemStack = result.itemStack();
            if(!Objects.equals(oldItemStack, newItemStack))
                player.setItemInHand(hand, newItemStack);

            var cancel = result.cancel();
            if(cancel != TriState.NOT_SET) { event.setCancelled(cancel == TriState.TRUE); }
        });

        node.addListener(PlayerEntityInteractEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            var hand = event.getHand();
            var oldItemStack = player.getItemInHand(hand);

            var loadout = player.getLoadout();
            var result = loadout.getHandlers().onEvent(player, Action.INTERACT, player.getHeldSlot(), oldItemStack);

            var newItemStack = result.itemStack();
            if(!Objects.equals(oldItemStack, newItemStack))
                player.setItemInHand(hand, newItemStack);

            //TODO What about e.g. flint and steel on creepers?
//            event.setCancelled(result.cancel());
        });

        node.addListener(InventoryItemChangeEvent.class, event -> {
            var inventory = event.getInventory();
            if(inventory == null) { return; }

            ILoadoutHolder owner = null;
            for(var instance : INSTANCE_MANAGER.getInstances()) {
                for(var entity : instance.getEntities()) {
                    if(!(entity instanceof IInventoryHolder inventoryHolder &&
                         entity instanceof ILoadoutHolder loadoutHolder)) { continue; }

                    if(!loadoutHolder.hasLoadout()) { continue; }

                    if(Objects.equals(inventory, inventoryHolder.getInventory())) {
                        owner = loadoutHolder;
                        break;
                    }
                }
            }
            if(owner == null) { return; }

            var loadout = owner.getLoadout();
            var result = loadout.getHandlers().onEvent(owner, Action.CHANGE, event.getSlot(), event.getNewItem());

            //TODO Test
            var itemStack = result.itemStack();
            if(itemStack != null)
                inventory.setItemStack(event.getSlot(), itemStack);
        });

        //TODO Make a custom event for custom entities?
        node.addListener(ItemDropEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            var loadout = player.getLoadout();
            var result = loadout.getHandlers().onEvent(player, Action.DROP, -1, event.getItemStack());

            var cancel = result.cancel();

            var oldItemStack = event.getItemStack();
            var newItemStack = result.itemStack();
            if(cancel == TriState.FALSE && !Objects.equals(oldItemStack, newItemStack)) {
                event.setCancelled(true);
                player.dropItem(newItemStack); //TODO Recursion?
            } else if(cancel != TriState.NOT_SET) {
                event.setCancelled(cancel == TriState.TRUE);
            }
        });

        mainNode.addChild(node);
    }

    private static void registerPerceiveEvents(EventNode<Event> mainNode, GameManager gameManager) {
        var node = EventNode.all("perceive");

        node.addListener(InventoryPreClickEvent.class, event -> {
            var player = GamePlayer.fromPlayer(event.getPlayer());
            if(!player.hasLoadout()) { return; }

            var loadout = player.getLoadout();
            var result = loadout.getHandlers().onEvent(player, Action.CLICK, event.getSlot(), event.getClickedItem());

            event.setClickedItem(result.itemStack());

            var cancel = result.cancel();
            if(cancel != TriState.NOT_SET) { event.setCancelled(cancel == TriState.TRUE); }
        });

        mainNode.addChild(node);
    }

}
