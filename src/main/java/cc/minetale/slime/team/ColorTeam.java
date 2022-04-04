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
    GREEN(NamedTextColor.GREEN, "green", "Green", "G",
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
    GRAY(NamedTextColor.GRAY, "gray", "Gray", "S",
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
