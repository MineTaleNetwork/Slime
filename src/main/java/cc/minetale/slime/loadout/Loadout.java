package cc.minetale.slime.loadout;

import cc.minetale.slime.event.loadout.LoadoutApplyEvent;
import cc.minetale.slime.event.loadout.LoadoutRemoveEvent;
import cc.minetale.slime.event.loadout.LoadoutReplaceEvent;
import lombok.Builder;
import lombok.Getter;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

import java.util.*;

@Getter
public final class Loadout implements TagReadable, TagWritable {

    private static final @NotNull Map<String, Loadout> REGISTERED_LOADOUTS = Collections.synchronizedMap(new HashMap<>());

    private final List<ILoadoutHolder> holders = Collections.synchronizedList(new ArrayList<>());

    private final String id;

    private final String displayName;
    private final ItemStack displayItem;

    private final List<ItemStack> items;

    private final LoadoutHandlers handlers;

    @Builder
    public Loadout(String id,
                   String displayName,
                   ItemStack displayItem,
                   List<ItemStack> items,
                   LoadoutHandlers handlers) {

        if(items.size() > PlayerInventory.INVENTORY_SIZE)
            throw new IllegalArgumentException("Loadout's items list size is too big for player's inventory");

        this.id = id;

        this.displayName = displayName;
        this.displayItem = displayItem;

        this.items = List.copyOf(items);

        this.handlers = Objects.requireNonNullElse(handlers, LoadoutHandlers.empty());
    }

    public Loadout register() {
        REGISTERED_LOADOUTS.putIfAbsent(this.id, this);
        return this;
    }

    private void applyToHolder(ILoadoutHolder holder) {
        List<ItemStack> items = this.handlers.dynamicallySupply(holder, new ArrayList<>(this.items));
        this.handlers.onApply(holder);

        holder.applyLoadout0(this, items);

        this.holders.add(holder);
    }

    /** Removes any previous loadout (if one is set) and sets this one for the provided loadout holder. */
    public boolean setFor(ILoadoutHolder holder) {
        if(holder.hasLoadout()) {
            return replaceFor(holder) != null;
        } else {
            return applyFor(holder);
        }
    }

    /** Sets this loadout for the provided loadout holder only if they don't have a loadout set. */
    public boolean applyFor(ILoadoutHolder holder) {
        if(holder.hasLoadout()) { return false; }

        var event = new LoadoutApplyEvent(holder, this);
        EventDispatcher.call(event);

        if(event.isCancelled()) { return false; }

        var loadout = event.getLoadout();
        loadout.applyToHolder(holder);

        return true;
    }

    /** Sets this loadout for the provided loadout holder only if they have a loadout set already. */
    public @Nullable Loadout replaceFor(ILoadoutHolder holder) {
        if(!holder.hasLoadout()) { return null; }

        var oldLoadout = holder.getLoadout();

        var event = new LoadoutReplaceEvent(holder, oldLoadout, this);
        EventDispatcher.call(event);

        if(event.isCancelled()) { return null; }

        removeIfAny(holder, false);

        var loadout = event.getNewLoadout(); //The event might change the loadout to apply
        loadout.applyToHolder(holder);

        return oldLoadout;
    }

    public boolean isUsing(ILoadoutHolder holder) {
        var loadout = holder.getLoadout();
        return Objects.equals(this.id, loadout.id);
    }

    /**
     * Attempts to remove a loadout from the holder if they have any and clears their inventory if so.
     * @return {@linkplain Loadout} that will be removed, {@code null} otherwise.
     */
    public static @Nullable Loadout removeIfAny(ILoadoutHolder holder) {
        return removeIfAny(holder, true);
    }

    private static @Nullable Loadout removeIfAny(ILoadoutHolder holder, boolean callEvent) {
        if(!holder.hasLoadout()) { return null; }

        var loadout = holder.getLoadout();

        if(callEvent) {
            var event = new LoadoutRemoveEvent(holder, loadout);
            EventDispatcher.call(event);

            if(event.isCancelled()) { return null; }
        }

        loadout.getHandlers().onRemove(holder);

        holder.removeLoadout0();

        loadout.holders.remove(holder);

        return loadout;
    }

    @Contract(pure = true)
    public @NotNull @UnmodifiableView List<ILoadoutHolder> getHolders() {
        return Collections.unmodifiableList(this.holders);
    }

    @Contract(pure = true)
    public static @NotNull @UnmodifiableView Map<String, Loadout> getRegisteredLoadouts() {
        return Collections.unmodifiableMap(REGISTERED_LOADOUTS);
    }

    //Tags
    private final MutableNBTCompound nbtCompound = new MutableNBTCompound();

    @Override public <T> @Nullable T getTag(@NotNull Tag<T> tag) {
        return tag.read(this.nbtCompound);
    }

    @Override public <T> void setTag(@NotNull Tag<T> tag, @Nullable T value) {
        tag.write(this.nbtCompound, value);
    }

}
