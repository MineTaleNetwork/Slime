package cc.minetale.slime.item;

import cc.minetale.slime.item.Tiers.ArmorTier;
import cc.minetale.slime.item.Tiers.ToolTier;
import cc.minetale.slime.item.base.IItem;
import cc.minetale.slime.item.category.ICategorized;
import cc.minetale.slime.item.category.ICategory;
import cc.minetale.slime.item.category.IItemCategory;
import cc.minetale.slime.item.category.ITieredCategory;
import cc.minetale.slime.item.crafting.ICraftable;
import cc.minetale.slime.item.enchant.EnchantmentCategory;
import cc.minetale.slime.item.enchant.IEnchantable;
import cc.minetale.slime.item.marker.ITieredMarker;
import cc.minetale.slime.item.tier.ITierItem;
import cc.minetale.slime.item.tier.ITieredItem;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.RecipeManager;
import net.minestom.server.recipe.ShapedRecipe;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagWritable;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static cc.minetale.slime.Slime.TAG_MANAGER;
import static net.minestom.server.gamedata.tags.Tag.BasicType;

public class ItemManager {
    public static final Tag<String> TAG = Tag.String("slimeItem");

    private final Map<NamespaceID, IItem> items = Collections.synchronizedMap(new HashMap<>());
    private final Map<NamespaceID, ITieredItem> tieredItems = Collections.synchronizedMap(new HashMap<>());

    private final Map<Material, Set<IItem>> itemsByMaterial = Collections.synchronizedMap(new HashMap<>());
    private final Map<ITier, Set<ITierItem>> itemsByTier = Collections.synchronizedMap(new HashMap<>());
    private final Map<Enchantment, Set<IItem>> itemsByEnchantment = Collections.synchronizedMap(new HashMap<>());

    private final Map<NamespaceID, ICategory<?>> categories = Collections.synchronizedMap(new HashMap<>());

    public void attachItemByTag(TagWritable taggable, IItem item) {
        taggable.setTag(TAG, item.getId().asString());
    }

    public void addItem(IItem item) {
        if(containsItem(item)) { return; }

        var material = item.getMaterial();
        this.itemsByMaterial.compute(material, (key, value) -> {
            value = Objects.requireNonNullElse(value, Collections.synchronizedSet(new HashSet<>()));
            value.add(item);

            return value;
        });

        if(item instanceof ITierItem tierItem) {
            var tier = tierItem.getTier();
            if(tier != null) {
                this.itemsByTier.compute(tier, (key, value) -> {
                    value = Objects.requireNonNullElse(value, Collections.synchronizedSet(new HashSet<>()));
                    value.add(tierItem);

                    return value;
                });
            }
        }

        if(item instanceof IEnchantable enchantable) {
            Set<Enchantment> enchantments = enchantable.getAllEnchantments();
            enchantments.forEach(enchantment -> {
                this.itemsByEnchantment.compute(enchantment, (key, value) -> {
                    value = Objects.requireNonNullElse(value, Collections.synchronizedSet(new HashSet<>()));
                    value.add(item);

                    return value;
                });
            });
        }

        this.items.put(item.getId(), item);
    }

    public void addItems(Collection<? extends IItem> items) {
        for(var item : items) {
            addItem(item);
        }
    }

    public void removeItem(NamespaceID id) {
        var item = this.items.get(id);
        if(item == null) { return; }

        var material = item.getMaterial();
        if(material != null) {
            this.itemsByMaterial.compute(material, (key, value) -> {
                if(value == null) { return null; }

                value.remove(item);
                if(value.isEmpty()) { return null; }

                return value;
            });
        }

        if(item instanceof ITierItem tierItem) {
            var tier = tierItem.getTier();
            if(tier != null) {
                this.itemsByTier.compute(tier, (key, value) -> {
                    if(value == null) { return null; }

                    value.remove(item);
                    if(value.isEmpty()) { return null; }

                    return value;
                });
            }
        }

        if(item instanceof IEnchantable enchantable) {
            Set<Enchantment> enchantments = enchantable.getAllEnchantments();
            enchantments.forEach(enchantment -> {
                this.itemsByEnchantment.compute(enchantment, (key, value) -> {
                    value = Objects.requireNonNullElse(value, Collections.synchronizedSet(new HashSet<>()));
                    value.add(item);

                    return value;
                });
            });
        }

        this.items.remove(id);
    }

    public void removeItem(IItem item) {
        removeItem(item.getId());
    }

    public void removeItems(Collection<? extends IItem> items) {
        for(var item : items) {
            removeItem(item);
        }
    }

    public void removeItemsByIds(Collection<NamespaceID> ids) {
        for(var id : ids) {
            removeItem(id);
        }
    }

    public IItem getItem(NamespaceID id) {
        return this.items.get(id);
    }

    public IItem getItemFromTag(TagReadable taggable) {
        if(!isTagged(taggable)) { return null; }
        return getItem(NamespaceID.from(taggable.getTag(TAG)));
    }

    public Set<IItem> getItemsFromStack(ItemStack itemStack) {
        if(isTagged(itemStack))
            return Collections.singleton(getItemFromTag(itemStack));

        return getItemsByMaterial(itemStack.getMaterial());
    }

    public Set<IItem> getItemsByMaterial(Material material) {
        return this.itemsByMaterial.get(material);
    }

    public Set<ITierItem> getItemsByTier(ITier tier) {
        return this.itemsByTier.get(tier);
    }

    public Set<IItem> getItemsByCategory(EnchantmentCategory category) {
        return this.itemsByEnchantment.get(category);
    }

    public boolean containsItem(NamespaceID id) {
        return this.items.containsKey(id);
    }

    public boolean containsItem(IItem item) {
        return containsItem(item.getId());
    }

    //Tiered Items

    public void addTieredItem(ITieredItem item) {
        if(containsTieredItem(item)) { return; }

        item.getAllItems().forEach(this::addItem);

        this.tieredItems.put(item.getId(), item);
    }

    public void addTieredItems(Collection<? extends ITieredItem> items) {
        for(var item : items) {
            addTieredItem(item);
        }
    }

    public void removeTieredItem(NamespaceID id) {
        var item = this.tieredItems.get(id);
        if(item == null) { return; }

        item.getAllItems().forEach(this::removeItem);

        this.items.remove(id);
    }

    public void removeTieredItem(ITieredItem item) {
        removeItem(item.getId());
    }

    public void removeTieredItems(Collection<? extends ITieredItem> items) {
        for(var item : items) {
            removeTieredItem(item);
        }
    }

    public void removeTieredItemsByIds(Collection<NamespaceID> ids) {
        for(var id : ids) {
            removeItem(id);
        }
    }

    public ITieredItem getTieredItem(NamespaceID id) {
        return this.tieredItems.get(id);
    }

    public boolean containsTieredItem(NamespaceID id) {
        return this.tieredItems.containsKey(id);
    }

    public boolean containsTieredItem(ITieredItem item) {
        return containsItem(item.getId());
    }

    //Categories

    public <I extends ICategorized<?>> void addCategory(ICategory<I> category) {
        if(containsCategory(category)) { return; }

        Set<I> items = category.getAllItems();
        if(items != null && !items.isEmpty()) {
            for(var item : items) {
                if(item instanceof IItem simple) {
                    addItem(simple);
                } else if(item instanceof ITieredItem tiered) {
                    addTieredItem(tiered);
                }
            }
        }

        this.categories.put(category.getId(), category);
    }

    public <I extends ICategorized<?>> void addCategories(Collection<ICategory<I>> categories) {
        for(var category : categories) {
            addCategory(category);
        }
    }

    public void removeCategory(NamespaceID id) {
        var category = this.categories.get(id);
        if(category == null) { return; }

        if(category instanceof IItemCategory<?> itemCategory) {
            Set<? extends IItem> items = itemCategory.getAllItems();
            if(items != null && !items.isEmpty()) {
                removeItems(items);
            }
        } else if(category instanceof ITieredCategory<?> tieredCategory) {
            Set<? extends ITieredItem> items = tieredCategory.getAllItems();
            if(items != null && !items.isEmpty()) {
                removeTieredItems(items);
            }
        }

        this.removeCategory(category);
    }

    public void removeCategory(ICategory<?> category) {
        removeCategory(category.getId());
    }

    public void removeCategories(Collection<? extends ICategory<?>> categories) {
        for(var category : categories) {
            removeCategory(category);
        }
    }

    public void removeCategoriesByIds(Collection<NamespaceID> ids) {
        for(var id : ids) {
            removeCategory(id);
        }
    }

    public ICategory<?> getCategory(NamespaceID id) {
        return this.categories.get(id);
    }

    public boolean containsCategory(NamespaceID id) {
        return this.categories.containsKey(id);
    }

    public boolean containsCategory(ICategory<?> category) {
        return containsCategory(category.getId());
    }

    public boolean isTagged(TagReadable taggable) {
        return taggable.hasTag(TAG);
    }

    public void addVanillaFeatures() {
        addCategory(new ToolCategory());
        addCategory(new ArmorCategory());
        addVanillaTiers();
        addVanillaRecipes();
        //TODO More
    }

    public void addVanillaTiers() {
        var toolCategory = ToolCategory.getInstance();
        var applicableItems = ITieredMarker.ofCategory(toolCategory);
        ToolTier.WOOD.setApplicableItems(applicableItems);
        ToolTier.STONE.setApplicableItems(applicableItems);
        ToolTier.IRON.setApplicableItems(applicableItems);
        ToolTier.GOLD.setApplicableItems(applicableItems);
        ToolTier.DIAMOND.setApplicableItems(applicableItems);
        ToolTier.NETHERITE.setApplicableItems(applicableItems);

        var armorCategory = ArmorCategory.getInstance();
        applicableItems = ITieredMarker.ofCategory(armorCategory);
        ArmorTier.LEATHER.setApplicableItems(applicableItems);
        ArmorTier.GOLD.setApplicableItems(applicableItems);
        ArmorTier.CHAINMAIL.setApplicableItems(applicableItems);
        ArmorTier.IRON.setApplicableItems(applicableItems);
        ArmorTier.TURTLE.setApplicableItems(ITieredMarker.of(armorCategory.getHelmet()));
        ArmorTier.DIAMOND.setApplicableItems(applicableItems);
        ArmorTier.NETHERITE.setApplicableItems(applicableItems);

        var tag = TAG_MANAGER.getTag(BasicType.ITEMS, "minecraft:planks");
        if(tag != null) {
            Set<NamespaceID> ids = tag.getValues();
            Set<Material> materials = new HashSet<>();
            for(var id : ids) {
                materials.add(Material.fromNamespaceId(id));
            }
            ToolTier.WOOD.getMaterials().addAll(materials);
        }

        tag = TAG_MANAGER.getTag(BasicType.ITEMS, "minecraft:stone_tool_materials");
        if(tag != null) {
            Set<NamespaceID> ids = tag.getValues();
            Set<Material> materials = new HashSet<>();
            for(var id : ids) {
                materials.add(Material.fromNamespaceId(id));
            }
            ToolTier.STONE.getMaterials().addAll(materials);
        }
    }

    public void addVanillaRecipes() {
        final var recipeManager = MinecraftServer.getRecipeManager();

        addCategoryRecipes(recipeManager, ToolCategory.getInstance());
        addCategoryRecipes(recipeManager, ArmorCategory.getInstance());
    }

    private void addCategoryRecipes(RecipeManager manager, ICategory<? extends ICraftable> category) {
        Set<? extends ICraftable> items = category.getAllItems();
        items.forEach(item -> addItemRecipe(manager, item));
    }

    private void addItemRecipe(RecipeManager manager, ICraftable craftable) {
        craftable.getRecipes(recipeInfo -> new ShapedRecipe(
                recipeInfo.id(),
                recipeInfo.width(), recipeInfo.height(),
                recipeInfo.group(),
                recipeInfo.ingredients(), recipeInfo.result()) {
            @Override public boolean shouldShow(@NotNull Player player) {
                return true;
            }
        }).forEach(manager::addRecipe);
    }
}
