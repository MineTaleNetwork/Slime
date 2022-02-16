package cc.minetale.slime.player;

import cc.minetale.slime.rule.RuleSet;
import cc.minetale.slime.utils.ApplyStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.util.TriState;
import net.minestom.server.entity.GameMode;

/**
 * The default implementation for the {@linkplain IPlayerState}. <br>
 * If you wish to have additional states, you are free to do so, but you mustn't replace and still use these. <br>
 */
@AllArgsConstructor
public enum PlayerState implements IPlayerState {

    LOBBY(GameMode.ADVENTURE, TriState.FALSE),

    PLAY(GameMode.SURVIVAL, TriState.TRUE),
    DEATHCAM(GameMode.ADVENTURE, TriState.TRUE),
    SPECTATE(GameMode.ADVENTURE, TriState.TRUE);

    @Getter private final GameMode gamemode;

    @Getter @Accessors(fluent = true)
    private final TriState showTeam;

    @Override
    public RuleSet getRuleSet() {
        return null;
    }

    @Override
    public ApplyStrategy getRulesApplyStrategy() {
        return null;
    }

    @Override
    public boolean getRulesAffectChildren() {
        return false;
    }

}