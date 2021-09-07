package cc.minetale.slime.loadout;

import cc.minetale.slime.event.loadout.LoadoutApplyEvent;
import cc.minetale.slime.event.loadout.LoadoutRemoveEvent;
import cc.minetale.slime.event.loadout.LoadoutReplaceEvent;
import lombok.Builder;
import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter
public class Loadout implements TagReadable, TagWritable {

    public static final @NotNull Tag<String> CURRENT_LOADOUT_TAG = Tag.String("slime-currentLoadout");

    private static final @NotNull Map<String, Loadout> REGISTERED_LOADOUTS = Collections.synchronizedMap(new HashMap<>());
    private static final @NotNull Map<String, Loadout> REGISTERED_LOADOUTS_SAFE = Collections.unmodifiableMap(REGISTERED_LOADOUTS);

    private static final @NotNull Map<Loadout, List<Player>> ACTIVE_LOADOUTS = new ConcurrentHashMap<>();

    private final String id;

    private final String displayName;
    private final ItemStack displayItem;

    private final List<ItemStack> items;

    //Can be used to set events for the player/inventory/hotbar
    private @Nullable Consumer<Player> onApply;
    private @Nullable Consumer<Player> onRemove;

    private final @Nullable BiConsumer<Player, List<ItemStack>> modifier;

    @Builder
    public Loadout(String id,
                   String displayName,
                   ItemStack displayItem,
                   List<ItemStack> items,
                   @Nullable Consumer<Player> onApply,
                   @Nullable Consumer<Player> onRemove,
                   @Nullable BiConsumer<Player, List<ItemStack>> modifier) {

        if(items.size() > PlayerInventory.INVENTORY_SIZE)
            throw new IllegalArgumentException("Loadout's items list size is too big for player's inventory");

        this.id = id;

        this.displayName = displayName;
        this.displayItem = displayItem;

        this.items = Collections.synchronizedList(new ArrayList<>(items));

        this.onApply = onApply;
        this.onRemove = onRemove;

        this.modifier = modifier;
    }

    public Loadout register() {
        REGISTERED_LOADOUTS.putIfAbsent(this.id, this);
        return this;
    }

    public boolean applyFor(Player player) {
        if(player.hasTag(CURRENT_LOADOUT_TAG)) { return false; }

        var oldLoadout = getActiveLoadout(player);
        if(oldLoadout == null) {
            var event = new LoadoutApplyEvent(player, this);
            EventDispatcher.call(event);

            if(event.isCancelled()) { return false; }

            var otherLoadout = event.getLoadout(); //The event might change the loadout to apply
            if(otherLoadout != this) {
                otherLoadout.forceApplyFor(player);
                return true;
            }
        } else {
            var event = new LoadoutReplaceEvent(player, oldLoadout, this);
            EventDispatcher.call(event);

            if(event.isCancelled()) { return false; }

            var otherLoadout = event.getNewLoadout(); //The event might change the loadout to apply
            if(otherLoadout != this) {
                otherLoadout.forceApplyFor(player);
                return true;
            }
        }
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
     * @return {@linkplain Loadout} that will be removed, {@code null} otherwise.
     */
    public static @Nullable Loadout removeIfAny(Player player) {
        if(!player.hasTag(CURRENT_LOADOUT_TAG)) { return null; }

        var currentLoadout = getActiveLoadout(player);
        if(currentLoadout == null) { return null; }

        var event = new LoadoutRemoveEvent(player, currentLoadout);
        EventDispatcher.call(event);

        if(event.isCancelled()) { return null; }

        var onRemove = currentLoadout.onRemove;
        if(onRemove != null) { onRemove.accept(player); }

        player.removeTag(CURRENT_LOADOUT_TAG);
        player.getInventory().clear();

        List<Player> players = ACTIVE_LOADOUTS.get(currentLoadout);
        if(players != null)
            players.remove(player);

        return currentLoadout;
    }

    public static Loadout getActiveLoadout(Player player) {
        if(!player.hasTag(CURRENT_LOADOUT_TAG)) { return null; }
        return REGISTERED_LOADOUTS.get(player.getTag(CURRENT_LOADOUT_TAG));
    }

    public static Map<String, Loadout> getRegisteredLoadouts() {
        return REGISTERED_LOADOUTS_SAFE;
    }

    //Tags
    private final NBTCompound nbtCompound = new NBTCompound();

    @Override public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(this.nbtCompound);
    }

    @Override public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(this.nbtCompound, value);
    }

}
