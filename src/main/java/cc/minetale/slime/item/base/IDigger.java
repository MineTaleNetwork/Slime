package cc.minetale.slime.item.base;

import net.minestom.server.instance.block.Block;

public interface IDigger extends IItem {
    float getMiningSpeedMultiplierFor(Block block);
    boolean canBreakBlock(Block block);
}
