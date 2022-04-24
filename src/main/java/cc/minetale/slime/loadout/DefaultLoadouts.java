package cc.minetale.slime.loadout;

import cc.minetale.commonlib.util.Colors;
import cc.minetale.slime.loadout.LoadoutHandlers.EventHandler;
import cc.minetale.slime.loadout.LoadoutHandlers.EventHandler.Result;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.rule.PlayerRule;
import cc.minetale.slime.utils.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.color.Color;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.firework.FireworkEffect;
import net.minestom.server.item.firework.FireworkEffectType;
import net.minestom.server.item.metadata.FireworkEffectMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public final class DefaultLoadouts {

    public static final Loadout LOBBY;
    public static final Loadout SPECTATOR;

    static {
        //LOBBY
        List<ItemStack> items = new ArrayList<>(Collections.nCopies(PlayerInventory.INVENTORY_SIZE, ItemStack.AIR));

        items.set(3, ItemStack.of(Material.EMERALD));
        items.set(4, ItemStack.of(Material.NOTE_BLOCK));
        items.set(5, ItemStack.of(Material.FIREWORK_STAR).withMeta(FireworkEffectMeta.class, meta -> {
            List<Color> color = Collections.singletonList(new Color(0, 255, 0));
            meta.effect(new FireworkEffect(
                    false,
                    false,
                    FireworkEffectType.SMALL_BALL,
                    color,
                    color));
            meta.hideFlag(ItemHideFlag.HIDE_POTION_EFFECTS);
        }));
        items.set(8, ItemStack.of(Material.RED_DYE));

        LOBBY = Loadout.builder()
                .id("lobby")
                .items(items)
                .build();

        //SPECTATOR
        items = new ArrayList<>(Collections.nCopies(PlayerInventory.INVENTORY_SIZE, ItemStack.AIR));

        BiFunction<ILoadoutHolder, List<ItemStack>, List<ItemStack>> dynamicSupplier = (holder, dynamicItems) -> {
            if(!(holder instanceof GamePlayer player)) { return dynamicItems; }

            var spectatorSettings = player.getRuleOrDefault(PlayerRule.SPECTATOR);

            int settingsSlot = 1;
            if(spectatorSettings.enablePlayersMenu()) {
                dynamicItems.set(0, ItemStack.of(Material.COMPASS)
                        .withDisplayName(Component.text()
                                .append(
                                        Component.text("Players", Colors.BLUE),
                                        Component.space(),
                                        Component.text("(Right-click)", NamedTextColor.DARK_GRAY))
                                .build()));
            } else {
                settingsSlot = 0;
            }

            if(spectatorSettings.enableSettingsMenu()) {
                dynamicItems.set(settingsSlot, ItemStack.of(Material.ENDER_EYE)
                        .withDisplayName(Component.text()
                                .append(
                                        Component.text("Spectator Settings", Colors.GREEN),
                                        Component.space(),
                                        Component.text("(Right-click)", NamedTextColor.DARK_GRAY))
                                .build()));
            }

            if(spectatorSettings.enableGamesMenu()) {
                dynamicItems.set(7, ItemStack.of(Material.BOOKSHELF)
                        .withDisplayName(Component.text()
                                .append(
                                        Component.text("Games", Colors.PURPLE),
                                        Component.space(),
                                        Component.text("(Right-click)", NamedTextColor.DARK_GRAY))
                                .build()));
            }

            dynamicItems.set(8, ItemStack.of(Material.RED_DYE)
                    .withDisplayName(Component.text()
                            .append(
                                    Component.text("Leave", Colors.RED),
                                    Component.space(),
                                    Component.text("(Right-click)", NamedTextColor.DARK_GRAY))
                            .build()));

            return dynamicItems;
        };

        SPECTATOR = Loadout.builder()
                .id("spectator")
                .items(items)
                .handlers(
                        LoadoutHandlers.withEventHandlers(
                                EventHandler.onSlotUse(0, info -> {
                                    if(!(info.holder() instanceof GamePlayer player)) { return Result.fromInfo(info); }
                                    PlayerUtil.Spectator.openPlayerSelector(player);

                                    return Result.fromInfo(info);
                                }),
                                EventHandler.onSlotUse(1, info -> {
                                    if(!(info.holder() instanceof GamePlayer player)) { return Result.fromInfo(info); }
                                    PlayerUtil.Spectator.openSettings(player);

                                    return Result.fromInfo(info);
                                }),
                                EventHandler.onSlotUse(7, info -> {
                                    if(!(info.holder() instanceof GamePlayer player)) { return Result.fromInfo(info); }
                                    PlayerUtil.Spectator.openGamesMenu(player);

                                    return Result.fromInfo(info);
                                }),
                                EventHandler.onSlotUse(8, info -> {
                                    if(!(info.holder() instanceof GamePlayer player)) { return Result.fromInfo(info); }
                                    player.kick(Component.empty());

                                    return Result.fromInfo(info);
                                })
                        ).setDynamicSupplier(dynamicSupplier))
                .build();
    }

}
