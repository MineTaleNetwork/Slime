package cc.minetale.slime.loadout;

import lombok.Builder;
import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter @Builder
public class Loadout {

    public static final @NotNull Tag<String> CURRENT_LOADOUT_TAG = Tag.String("slime-currentLoadout");

    private static final @NotNull Map<String, Loadout> REGISTERED_LOADOUTS = Collections.synchronizedMap(new HashMap<>());
    private static final @NotNull Map<String, Loadout> REGISTERED_LOADOUTS_SAFE = Collections.unmodifiableMap(REGISTERED_LOADOUTS);

    private static final @NotNull Map<Loadout, List<Player>> ACTIVE_LOADOUTS = new ConcurrentHashMap<>();

    private final String id;

    private final String displayName;
    private final ItemStack displayItem;

    private final List<ItemStack> items = Collections.synchronizedList(new ArrayList<>(PlayerInventory.INVENTORY_SIZE));

    //Can be used to set events for the player/inventory/hotbar
    private final @Nullable Consumer<Player> onApply;
    private final @Nullable Consumer<Player> onRemove;

    private final @Nullable BiConsumer<Player, List<ItemStack>> modifier;

    public Loadout register() {
        REGISTERED_LOADOUTS.putIfAbsent(this.id, this);
        return this;
    }

    public boolean applyFor(Player player) {
        if(player.hasTag(CURRENT_LOADOUT_TAG)) { return false; }

        forceApplyFor(player);
        return true;
    }

    public void forceApplyFor(Player player) {
        removeIfAny(player);

        var inventory = player.getInventory();

        player.setTag(CURRENT_LOADOUT_TAG, this.id);
        
        if(this.modifier != null) { this.modifier.accept(player, this.items); }
        inventory.copyContents(this.items.toArray(new ItemStack[0]));

        if(this.onApply != null) { this.onApply.accept(player); }

        ACTIVE_LOADOUTS.compute(this, (key, value) -> {
            if(value == null)
                value = new ArrayList<>();

            value.add(player);
            return value;
        });
    }

    public boolean isUsing(Player player) {
        var currentLoadout = player.getTag(CURRENT_LOADOUT_TAG);
        return this.id.equals(currentLoadout);
    }

    public List<Player> getPlayersUsing() {
        return ACTIVE_LOADOUTS.getOrDefault(this, new ArrayList<>());
    }

    /**
     * Attempts to remove a loadout from the player if they have any and clears their inventory if so.
     */
    public static boolean removeIfAny(Player player) {
        if(!player.hasTag(CURRENT_LOADOUT_TAG)) { return false; }

        var currentLoadout = REGISTERED_LOADOUTS.get(player.getTag(CURRENT_LOADOUT_TAG));

        var onRemove = currentLoadout.onRemove;
        if(onRemove != null) { onRemove.accept(player); }

        player.removeTag(CURRENT_LOADOUT_TAG);
        player.getInventory().clear();

        List<Player> players = ACTIVE_LOADOUTS.get(currentLoadout);
        if(players != null)
            players.remove(player);

        return true;
    }

    public static Loadout getActiveLoadout(Player player) {
        if(!player.hasTag(CURRENT_LOADOUT_TAG)) { return null; }
        return REGISTERED_LOADOUTS.get(player.getTag(CURRENT_LOADOUT_TAG));
    }

    public static Map<String, Loadout> getRegisteredLoadouts() {
        return REGISTERED_LOADOUTS_SAFE;
    }

}
