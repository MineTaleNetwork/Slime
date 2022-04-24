package cc.minetale.slime.utils;

import cc.minetale.commonlib.util.Colors;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.rule.PlayerRule;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.PlayerHeadMeta;

import java.util.List;

@UtilityClass
public class PlayerUtil {

    @UtilityClass
    public static final class Spectator {

        public static void openPlayerSelector(GamePlayer spectator) {
            var inventory = new Inventory(InventoryType.CHEST_6_ROW, Component.text("Players"));

            var spectatorSettings = spectator.getRuleOrDefault(PlayerRule.SPECTATOR);

            int i = 0;
            var game = spectator.getGame();
            for(var otherPlayer : game.getPlayers()) {
                if(!spectatorSettings.shouldShowPlayerInMenu(spectator, otherPlayer)) { continue; }

                var playerItem = ItemStack.of(Material.PLAYER_HEAD)
                        .withDisplayName(otherPlayer.getGameName(spectator))
                        .withLore(List.of(
                                Component.text(((int) otherPlayer.getHealth()) + " \u2764", Colors.RED),
                                Component.empty(),
                                Component.text("Left-click to ", NamedTextColor.GRAY)
                                        .append(Component.text("Teleport", Colors.PURPLE)),
                                Component.text("Right-click to ", NamedTextColor.GRAY)
                                        .append(Component.text("Track", Colors.BLUE))
                        ))
                        .withMeta(PlayerHeadMeta.class, meta -> meta.playerSkin(otherPlayer.getSkin()));

                inventory.setItemStack(i++, playerItem);
            }

            spectator.openInventory(inventory);
        }

        public static void openSettings(GamePlayer spectator) {
            spectator.openInventory(new Inventory(InventoryType.CHEST_6_ROW, Component.text("Spectator Settings")));
        }

        public static void openGamesMenu(GamePlayer spectator) {
            spectator.openInventory(new Inventory(InventoryType.CHEST_6_ROW, Component.text("Games")));
        }

    }

}
