package cc.minetale.slime.player;

import cc.minetale.slime.rule.RuleSet;
import cc.minetale.slime.utils.ApplyStrategy;
import net.kyori.adventure.util.TriState;
import net.minestom.server.entity.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPlayerState {
    @Nullable GameMode getGamemode();
    @NotNull TriState showTeam();
    @Nullable RuleSet getRuleSet();
    @Nullable ApplyStrategy getRulesApplyStrategy();
    boolean getRulesAffectChildren();
}
