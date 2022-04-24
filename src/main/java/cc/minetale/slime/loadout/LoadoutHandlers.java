package cc.minetale.slime.loadout;

import cc.minetale.slime.entity.IInventoryHolder;
import cc.minetale.slime.utils.MiscUtil;
import cc.minetale.slime.misc.restriction.RestrictionList;
import cc.minetale.slime.misc.restriction.Whitelist;
import lombok.AllArgsConstructor;
import net.kyori.adventure.util.TriState;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class LoadoutHandlers {

    //Can be used to set events for the player/inventory/hotbar
    private @Nullable Consumer<ILoadoutHolder> applyHandler;
    private @Nullable Consumer<ILoadoutHolder> removeHandler;

    private @Nullable List<EventHandler> eventHandlers;

    private @Nullable BiFunction<ILoadoutHolder, List<ItemStack>, List<ItemStack>> dynamicSupplier;

    public LoadoutHandlers(@Nullable Consumer<ILoadoutHolder> applyHandler,
                           @Nullable Consumer<ILoadoutHolder> removeHandler,

                           @Nullable List<EventHandler> eventHandlers,

                           @Nullable BiFunction<ILoadoutHolder, List<ItemStack>, List<ItemStack>> dynamicSupplier) {

        this.applyHandler = applyHandler;
        this.removeHandler = removeHandler;

        this.eventHandlers = (eventHandlers != null) ? new LinkedList<>(eventHandlers) : null;

        this.dynamicSupplier = dynamicSupplier;
    }

    public static LoadoutHandlers empty() {
        return new LoadoutHandlers(
                null, null,
                Collections.emptyList(),
                null);
    }

    public static LoadoutHandlers withEventHandlers(List<EventHandler> eventHandlers) {
        return new LoadoutHandlers(
                null, null,
                eventHandlers,
                null);
    }

    public static LoadoutHandlers withEventHandlers(EventHandler... eventHandlers) {
        return withEventHandlers(List.of(eventHandlers));
    }

    public static LoadoutHandlers withDynamicSupplier(BiFunction<ILoadoutHolder, List<ItemStack>, List<ItemStack>> dynamicSupplier) {
        return new LoadoutHandlers(
                null, null,
                null,
                dynamicSupplier);
    }

    public LoadoutHandlers setDynamicSupplier(BiFunction<ILoadoutHolder, List<ItemStack>, List<ItemStack>> dynamicSupplier) {
        this.dynamicSupplier = dynamicSupplier;
        return this;
    }

    public LoadoutHandlers handleApply(Consumer<ILoadoutHolder> applyHandler) {
        this.applyHandler = applyHandler;
        return this;
    }

    public LoadoutHandlers handleRemove(Consumer<ILoadoutHolder> removeHandler) {
        this.removeHandler = removeHandler;
        return this;
    }

    public LoadoutHandlers handleEvent(EventHandler eventHandler) {
        if(this.eventHandlers == null)
            this.eventHandlers = new LinkedList<>();

        this.eventHandlers.add(eventHandler);
        return this;
    }

    public void onApply(ILoadoutHolder holder) {
        if(this.applyHandler == null) { return; }
        this.applyHandler.accept(holder);
    }

    public void onRemove(ILoadoutHolder holder) {
        if(this.removeHandler == null) { return; }
        this.removeHandler.accept(holder);
    }

    /**
     * Delegate a click, drag or drop events to {@linkplain EventHandler}(s). <br>
     * These handlers can mutate the {@linkplain ItemStack}.
     */
    public EventHandler.Result onEvent(ILoadoutHolder holder, EventHandler.Action action, int slot, ItemStack itemStack) {
        if(this.eventHandlers == null) { return new EventHandler.Result(itemStack, TriState.NOT_SET); }
        var cancel = TriState.NOT_SET;
        for(var handler : this.eventHandlers) {
            var result = handler.handle(new EventHandler.Info(holder, action, slot, itemStack));
            itemStack = result.itemStack();

            var resultCancel = result.cancel();
            if(cancel == TriState.NOT_SET && resultCancel != TriState.NOT_SET) { cancel = resultCancel; }
        }
        return new EventHandler.Result(itemStack, cancel);
    }

    /** Dynamically gives items based on the holder's state, game's settings and anything you'd like. */
    public List<ItemStack> dynamicallySupply(ILoadoutHolder holder, List<ItemStack> items) {
        if(this.dynamicSupplier == null) { return items; }
        return this.dynamicSupplier.apply(holder, items);
    }

    @AllArgsConstructor
    public static class EventHandler {
        private @Nullable RestrictionList<Integer> requiredSlots;
        private @Nullable RestrictionList<Material> requiredMaterials;
        private @Nullable RestrictionList<Action> requiredActions;
        private @Nullable Map<Tag<?>, ?> requiredTags;

        private @Nullable Predicate<Info> otherRequirements;

        private @Nullable Function<Info, Result> action;

        public EventHandler(@NotNull Function<Info, Result> action) {
            this.action = action;
        }

        public static EventHandler empty() {
            return new EventHandler(
                    null, null, null, null,
                    null,
                    null);
        }

        public static EventHandler onSlotUse(int slot, @NotNull Function<Info, Result> action) {
            return new EventHandler(action)
                    .requireSlots(Whitelist.of(slot))
                    .requireAction(Whitelist.of(Action.INTERACT));
        }

        public EventHandler requireSlots(RestrictionList<Integer> requiredSlots) {
            this.requiredSlots = requiredSlots;
            return this;
        }

        public EventHandler requireMaterials(RestrictionList<Material> requiredMaterials) {
            this.requiredMaterials = requiredMaterials;
            return this;
        }

        public EventHandler requireAction(RestrictionList<Action> requiredActions) {
            this.requiredActions = requiredActions;
            return this;
        }

        public EventHandler requireTags(Map<Tag<?>, ?> required) {
            this.requiredTags = Collections.synchronizedMap(new HashMap<>(required));
            return this;
        }

        public EventHandler require(Predicate<Info> require) {
            this.otherRequirements = require;
            return this;
        }

        public boolean test(Info info) {
            var itemStack = info.itemStack();
            var holder = info.holder();
            if(this.requiredSlots != null && holder instanceof IInventoryHolder inventoryHolder) {
                var inventory = inventoryHolder.getInventory();
                var slot = MiscUtil.getIndexOfItemStack(inventory, itemStack);
                if(this.requiredSlots.isRestricted(slot)) { return false; }
            }

            if(this.requiredMaterials != null) {
                var material = itemStack.getMaterial();
                if(this.requiredMaterials.isRestricted(material)) { return false; }
            }

            if(this.requiredActions != null) {
                var action = info.action();
                if(this.requiredActions.isRestricted(action)) { return false; }
            }

            if(this.requiredTags != null) {
                for(var ent : this.requiredTags.entrySet()) {
                    var tag = ent.getKey();
                    var requiredValue = ent.getValue();

                    if(!Objects.equals(requiredValue, itemStack.getTag(tag))) { return false; }
                }
            }

            if(this.otherRequirements != null && !this.otherRequirements.test(info))
                return false;

            return true;
        }

        public Result handle(Info info) {
            if(this.action == null || !test(info)) { return Result.fromInfo(info); }
            return this.action.apply(info);
        }

        public record Info(ILoadoutHolder holder, Action action, int slot, ItemStack itemStack) { }

        public record Result(ItemStack itemStack, TriState cancel) {
            public static Result fromInfo(Info info, TriState cancel) {
                return new Result(info.itemStack(), cancel);
            }

            public static Result fromInfo(Info info) {
                return fromInfo(info, TriState.NOT_SET);
            }
        }

        public enum Action {
            CLICK,
            INTERACT,
            CHANGE,
            DROP
        }
    }

}
