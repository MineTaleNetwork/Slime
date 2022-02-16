package cc.minetale.slime.utils;

import lombok.experimental.UtilityClass;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@UtilityClass
public class TagUtil {
    //Shared tags
    private static final Tag<String> DISPLAY_NAME = Tag.String("DisplayName");

    public static ItemStack copyDisplayName(Block src, ItemStack target) {
        return Items.withDisplayName(target, Blocks.getDisplayName(src));
    }

    public static Block copyDisplayName(ItemStack src, Block target) {
        return Blocks.withDisplayName(target, Items.getDisplayName(src));
    }

    public static class Items {
        private static final Tag<Short> BLOCK_STATE = Tag.Short("BlockState");

        public static ItemStack withState(ItemStack item, short state) {
            return item.withTag(BLOCK_STATE, state);
        }

        public static ItemStack withState(ItemStack item, Block block, Map<String, String> properties) {
            var pure = block.registry().material().block();

            var state = pure.withProperties(properties).stateId();
            return withState(item, state);
        }

        public static ItemStack copyState(ItemStack src, ItemStack target) {
            return withState(target, getState(src));
        }

        public static short getState(ItemStack item) {
            var block = item.getMaterial().block();
            if(block == null) { return 0; }

            return Objects.requireNonNullElse(item.getTag(BLOCK_STATE), block.stateId());
        }

        public static @Nullable Block getBlockWithState(ItemStack item) {
            return Block.fromStateId(getState(item));
        }

        public static ItemStack withDisplayName(ItemStack item, String displayName) {
            return item.withTag(DISPLAY_NAME, displayName);
        }

        public static ItemStack copyDisplayName(ItemStack src, ItemStack target) {
            return withDisplayName(target, getDisplayName(src));
        }

        public static String getDisplayName(ItemStack item) {
            return item.getTag(DISPLAY_NAME);
        }
    }

    public static class Blocks {
        public static Block withDisplayName(Block block, String displayName) {
            return block.withTag(DISPLAY_NAME, displayName);
        }

        public static Block copyDisplayName(Block src, Block target) {
            return withDisplayName(target, getDisplayName(src));
        }

        public static String getDisplayName(Block block) {
            return block.getTag(DISPLAY_NAME);
        }
    }
}
