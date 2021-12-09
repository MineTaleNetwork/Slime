package cc.minetale.slime.loadout;

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
                .onApply(player -> {}) //TODO Set inventory conditions
                .onRemove(player -> {}) //TODO Remove inventory conditions
                .build();

        items = new ArrayList<>(Collections.nCopies(PlayerInventory.INVENTORY_SIZE, ItemStack.AIR));

        //SPECTATOR
        items.set(0, ItemStack.of(Material.COMPASS));
        items.set(7, ItemStack.of(Material.BOOKSHELF));
        items.set(8, ItemStack.of(Material.RED_DYE));

        SPECTATOR = Loadout.builder()
                .id("spectator")
                .items(items)
                .onApply(player -> {}) //TOOD Set inventory conditions
                .onRemove(player -> {}) //TOOD Remove inventory conditions
                .build();
    }

}
