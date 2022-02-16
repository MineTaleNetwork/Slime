package cc.minetale.slime.item;

import cc.minetale.slime.item.marker.ITieredMarker;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface ITier {
    ITier OTHER = new ITier() {
        @Getter @Setter private ITieredMarker<?> applicableItems = ITieredMarker.none();
        @Getter @Setter private Set<Material> materials = Collections.synchronizedSet(new HashSet<>());

        @Override public String getNoun() { return ""; }
        @Override public String getAdjective() { return ""; }
        @Override public int getLevel() { return 0; }

        @Override public int getEnchantability() { return 0; }
    };

    String getNoun();
    String getAdjective();

    int getLevel();

    int getEnchantability();

    ITieredMarker<?> getApplicableItems();
    Set<Material> getMaterials();
}
