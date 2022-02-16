package cc.minetale.slime.loot.predicate.util;

import cc.minetale.slime.loot.util.IntegerRangeProvider;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Getter
public class PredicatePlayerProps {
    @Setter private @Nullable PredicateEntityProps lookingAt;
    private final Map<NamespaceID, NamespaceID> advancements;
    @Setter private @Nullable GameMode gamemode;
    @Setter private @Nullable IntegerRangeProvider level;
    private final Map<NamespaceID, Boolean> recipes; //Documentation's being confusing again, is this correct?
    private final List<PredicateStatistic> stats;

    //TODO Factory methods

    public PredicatePlayerProps(@Nullable PredicateEntityProps lookingAt,
                                @Nullable Map<NamespaceID, NamespaceID> advancements,
                                @Nullable GameMode gamemode,
                                @Nullable IntegerRangeProvider level,
                                @Nullable Map<NamespaceID, Boolean> recipes,
                                @Nullable List<PredicateStatistic> stats) {

        this.lookingAt = lookingAt;
        this.gamemode = gamemode;
        this.level = level;

        this.advancements = Collections.synchronizedMap(
                new HashMap<>(Objects.requireNonNullElse(advancements, Collections.emptyMap())));

        this.recipes = Collections.synchronizedMap(
                new HashMap<>(Objects.requireNonNullElse(recipes, Collections.emptyMap())));

        this.stats = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(stats, Collections.emptyList())));
    }

    public boolean test(Player player) {
        if(this.lookingAt != null) {
            var lookingAt = player.getLineOfSightEntity(64, null);
            if(!this.lookingAt.test(lookingAt, null)) { return false; }
        }

        //TODO Advancements

        if(this.gamemode != null && player.getGameMode() != gamemode)
            return false;

        if(this.level != null && this.level.isInRange(player.getLevel()))
            return false;

        //TODO Recipes

        //TODO Stats

        return true;
    }
}
