package cc.minetale.slime.team;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

/**
 * The default implementation is {@linkplain ColorTeam}. <br>
 * Using an interface when specifying for teams allows gamemodes to specify their own team, <br>
 * for example "Zombies" and "Civilians".
 */
public interface ITeamType {
    NamedTextColor getColor();

    String getId();

    String getFullName();
    default String getShortName() { return null; }

    default Material getWoolMaterial() { return null; }
    default Material getTerracottaMaterial() { return null; }
    default Material getCarpetMaterial() { return null; }
    default Material getGlassMaterial() { return null; }
    default Material getGlassPaneMaterial() { return null; }
    default Material getShulkerMaterial() { return null; }
    default Material getGlazedTerracottaMaterial() { return null; }
    default Material getConcreteMaterial() { return null; }
    default Material getPowderMaterial() { return null; }
    default Material getDyeMaterial() { return null; }
    default Material getBannerMaterial() { return null; }
    default Material getBedMaterial() { return null; }

    default Material getTeamItem() { return null; }
    default Material getTeamBlock() { return null; }

}
