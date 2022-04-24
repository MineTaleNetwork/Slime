package cc.minetale.slime.entity;

import cc.minetale.slime.loadout.LoadoutHandlers;
import net.minestom.server.inventory.AbstractInventory;

/**
 * Currently only used by {@linkplain LoadoutHandlers.EventHandler#test(LoadoutHandlers.EventHandler.Info)} <br>
 * so it can find an index of ItemStacks in custom entities' inventories.
 */
public interface IInventoryHolder {
    AbstractInventory getInventory();
}
