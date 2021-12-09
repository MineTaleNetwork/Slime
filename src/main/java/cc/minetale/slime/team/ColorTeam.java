package cc.minetale.slime.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter @AllArgsConstructor
public enum ColorTeam implements ITeamType {
    RED(NamedTextColor.RED, "red", "Red", "R",
            Material.RED_WOOL,
            Material.RED_TERRACOTTA,
            Material.RED_CARPET,
            Material.RED_STAINED_GLASS,
            Material.RED_STAINED_GLASS_PANE,
            Material.RED_SHULKER_BOX,
            Material.RED_GLAZED_TERRACOTTA,
            Material.RED_CONCRETE,
            Material.RED_CONCRETE_POWDER,
            Material.RED_DYE,
            Material.RED_BANNER,
            Material.RED_BED,
            Material.REDSTONE,
            Material.TNT),
    BLUE(NamedTextColor.BLUE, "blue", "Blue", "B",
            Material.BLUE_WOOL,
            Material.BLUE_TERRACOTTA,
            Material.BLUE_CARPET,
            Material.BLUE_STAINED_GLASS,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.BLUE_SHULKER_BOX,
            Material.BLUE_GLAZED_TERRACOTTA,
            Material.BLUE_CONCRETE,
            Material.BLUE_CONCRETE_POWDER,
            Material.BLUE_DYE,
            Material.BLUE_BANNER,
            Material.BLUE_BED,
            Material.LAPIS_LAZULI,
            Material.LAPIS_BLOCK),
    GREEN(NamedTextColor.DARK_GREEN, "green", "Green", "G",
            Material.GREEN_WOOL,
            Material.GREEN_TERRACOTTA,
            Material.GREEN_CARPET,
            Material.GREEN_STAINED_GLASS,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.GREEN_SHULKER_BOX,
            Material.GREEN_GLAZED_TERRACOTTA,
            Material.GREEN_CONCRETE,
            Material.GREEN_CONCRETE_POWDER,
            Material.GREEN_DYE,
            Material.GREEN_BANNER,
            Material.GREEN_BED,
            Material.LILY_PAD,
            Material.DRIED_KELP_BLOCK),
    YELLOW(NamedTextColor.YELLOW, "yellow", "Yellow", "Y",
            Material.YELLOW_WOOL,
            Material.YELLOW_TERRACOTTA,
            Material.YELLOW_CARPET,
            Material.YELLOW_STAINED_GLASS,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.YELLOW_SHULKER_BOX,
            Material.YELLOW_GLAZED_TERRACOTTA,
            Material.YELLOW_CONCRETE,
            Material.YELLOW_CONCRETE_POWDER,
            Material.YELLOW_DYE,
            Material.YELLOW_BANNER,
            Material.YELLOW_BED,
            Material.GOLD_INGOT,
            Material.GOLD_BLOCK),
    LIGHT_BLUE(NamedTextColor.AQUA, "aqua", "Aqua", "A",
            Material.LIGHT_BLUE_WOOL,
            Material.LIGHT_BLUE_TERRACOTTA,
            Material.LIGHT_BLUE_CARPET,
            Material.LIGHT_BLUE_STAINED_GLASS,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.LIGHT_BLUE_DYE,
            Material.LIGHT_BLUE_BANNER,
            Material.LIGHT_BLUE_BED,
            Material.DIAMOND,
            Material.DIAMOND_BLOCK),
    WHITE(NamedTextColor.WHITE, "white", "White", "W",
            Material.WHITE_WOOL,
            Material.WHITE_TERRACOTTA,
            Material.WHITE_CARPET,
            Material.WHITE_STAINED_GLASS,
            Material.WHITE_STAINED_GLASS_PANE,
            Material.WHITE_SHULKER_BOX,
            Material.WHITE_GLAZED_TERRACOTTA,
            Material.WHITE_CONCRETE,
            Material.WHITE_CONCRETE_POWDER,
            Material.WHITE_DYE,
            Material.WHITE_BANNER,
            Material.WHITE_BED,
            Material.IRON_INGOT,
            Material.IRON_BLOCK),
    PINK(NamedTextColor.LIGHT_PURPLE, "pink", "Pink", "P",
            Material.PINK_WOOL,
            Material.PINK_TERRACOTTA,
            Material.PINK_CARPET,
            Material.PINK_STAINED_GLASS,
            Material.PINK_STAINED_GLASS_PANE,
            Material.PINK_SHULKER_BOX,
            Material.PINK_GLAZED_TERRACOTTA,
            Material.PINK_CONCRETE,
            Material.PINK_CONCRETE_POWDER,
            Material.PINK_DYE,
            Material.PINK_BANNER,
            Material.PINK_BED,
            Material.BRAIN_CORAL,
            Material.BRAIN_CORAL_BLOCK),
    LIGHT_GRAY(NamedTextColor.GRAY, "dark", "Dark", "D",
            Material.LIGHT_GRAY_WOOL,
            Material.LIGHT_GRAY_TERRACOTTA,
            Material.LIGHT_GRAY_CARPET,
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Material.LIGHT_GRAY_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_DYE,
            Material.LIGHT_GRAY_BANNER,
            Material.LIGHT_GRAY_BED,
            Material.CLAY_BALL,
            Material.SMOOTH_STONE),
    CYAN(NamedTextColor.DARK_AQUA, "cyan", "Cyan", "C",
            Material.CYAN_WOOL,
            Material.CYAN_TERRACOTTA,
            Material.CYAN_CARPET,
            Material.CYAN_STAINED_GLASS,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.CYAN_SHULKER_BOX,
            Material.CYAN_GLAZED_TERRACOTTA,
            Material.CYAN_CONCRETE,
            Material.CYAN_CONCRETE_POWDER,
            Material.CYAN_DYE,
            Material.CYAN_BANNER,
            Material.CYAN_BED,
            Material.PRISMARINE_CRYSTALS,
            Material.PRISMARINE_BRICKS),
    ORANGE(NamedTextColor.GOLD, "orange", "Orange", "O",
            Material.ORANGE_WOOL,
            Material.ORANGE_TERRACOTTA,
            Material.ORANGE_CARPET,
            Material.ORANGE_STAINED_GLASS,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.ORANGE_SHULKER_BOX,
            Material.ORANGE_GLAZED_TERRACOTTA,
            Material.ORANGE_CONCRETE,
            Material.ORANGE_CONCRETE_POWDER,
            Material.ORANGE_DYE,
            Material.ORANGE_BANNER,
            Material.ORANGE_BED,
            Material.HONEY_BOTTLE,
            Material.HONEYCOMB_BLOCK),
    PURPLE(NamedTextColor.DARK_PURPLE, "purple", "Purple", "PU",
            Material.PURPLE_WOOL,
            Material.PURPLE_TERRACOTTA,
            Material.PURPLE_CARPET,
            Material.PURPLE_STAINED_GLASS,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.PURPLE_SHULKER_BOX,
            Material.PURPLE_GLAZED_TERRACOTTA,
            Material.PURPLE_CONCRETE,
            Material.PURPLE_CONCRETE_POWDER,
            Material.PURPLE_DYE,
            Material.PURPLE_BANNER,
            Material.PURPLE_BED,
            Material.BUBBLE_CORAL,
            Material.BUBBLE_CORAL_BLOCK),
    LIME(NamedTextColor.GREEN, "lime", "Lime", "L",
            Material.LIME_WOOL,
            Material.LIME_TERRACOTTA,
            Material.LIME_CARPET,
            Material.LIME_STAINED_GLASS,
            Material.LIME_STAINED_GLASS_PANE,
            Material.LIME_SHULKER_BOX,
            Material.LIME_GLAZED_TERRACOTTA,
            Material.LIME_CONCRETE,
            Material.LIME_CONCRETE_POWDER,
            Material.LIME_DYE,
            Material.LIME_BANNER,
            Material.LIME_BED,
            Material.SUGAR_CANE,
            Material.MELON),
    MAGENTA(NamedTextColor.LIGHT_PURPLE, "magenta", "Magenta", "M",
            Material.MAGENTA_WOOL,
            Material.MAGENTA_TERRACOTTA,
            Material.MAGENTA_CARPET,
            Material.MAGENTA_STAINED_GLASS,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.MAGENTA_SHULKER_BOX,
            Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.MAGENTA_CONCRETE,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.MAGENTA_DYE,
            Material.MAGENTA_BANNER,
            Material.MAGENTA_BED,
            Material.POPPED_CHORUS_FRUIT,
            Material.PURPUR_BLOCK),
    GRAY(NamedTextColor.DARK_GRAY, "black", "Black", "B",
            Material.GRAY_WOOL,
            Material.GRAY_TERRACOTTA,
            Material.GRAY_CARPET,
            Material.GRAY_STAINED_GLASS,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.GRAY_SHULKER_BOX,
            Material.GRAY_GLAZED_TERRACOTTA,
            Material.GRAY_CONCRETE,
            Material.GRAY_CONCRETE_POWDER,
            Material.GRAY_DYE,
            Material.GRAY_BANNER,
            Material.GRAY_BED,
            Material.NETHERITE_INGOT,
            Material.NETHERITE_BLOCK);

    private NamedTextColor color;

    private String id;

    private String fullName;
    private String shortName;

    private Material woolMaterial;
    private Material terracottaMaterial;
    private Material carpetMaterial;
    private Material glassMaterial;
    private Material glassPaneMaterial;
    private Material shulkerMaterial;
    private Material glazedTerracottaMaterial;
    private Material concreteMaterial;
    private Material powderMaterial;
    private Material dyeMaterial;
    private Material bannerMaterial;
    private Material bedMaterial;

    private Material teamItem;
    private Material teamBlock;

    public static <T extends GameTeam> List<T> getRequiredTeams(int amount, Supplier<T> teamSupplier) {
        final var values = values();

        List<T> teams = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            T team = teamSupplier.get();
            team.setType(values[i]);
            teams.add(team);
        }

        return teams;
    }

    public TextComponent getFormattedDisplay(String string) {
        return Component.text()
                .append(getFormattedTag())
                .append(Component.space())
                .append(getFormattedName(string))
                .build();
    }

    public TextComponent getFormattedTag() {
        return Component.text().append(
                Component.text("[", NamedTextColor.GRAY),
                Component.text(this.shortName, this.color),
                Component.text("]", NamedTextColor.GRAY))
                .build();
    }

    public TextComponent getFormattedName(String string) {
        return Component.text(string, this.color);
    }

}
