package cc.minetale.slime.player;

import cc.minetale.slime.perceive.PerceiveAction;
import cc.minetale.slime.perceive.PerceiveAction.Type;
import cc.minetale.slime.perceive.PerceiveTeam;
import cc.minetale.slime.rule.RuleEntry;
import cc.minetale.slime.rule.RuleSet;
import cc.minetale.slime.misc.ApplyStrategy;
import cc.minetale.slime.misc.restriction.Whitelist;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.util.TriState;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static cc.minetale.slime.rule.PlayerRule.*;

/**
 * If you wish to have additional states, you are free to do so, but you mustn't replace and still use these. <br>
 * You can modify the provided states if you feel like it doesn't fit your game's needs. <br>
 * <br>
 * {@linkplain PlayerState} has default values that are appropriate for {@linkplain #PLAY} state as this is what most states will be based upon. <br>
 * Check specific variables to see their default values and simply change them if the state you're creating needs different behavior. <br>
 * <br>
 * Use {@linkplain RuleSet#ofDefaults} to clear any previous rules and reset them to defaults. <br>
 * There's also {@linkplain RuleSet#ofDefaultsWith} if you wish to reset all rules, but override some of them. <br>
 * <br>
 * Below you can see the examples.
 */
public final class PlayerState {

    public static final RuleSet DEATH_RULES = RuleSet.ofDefaultsWith(ALL_RULES,
            RuleEntry.of(INTERACT, InteractSettings.restricted()),
            RuleEntry.of(PICKUP_ITEMS, false),
            RuleEntry.of(DROP_ITEMS, null),
            RuleEntry.of(INVENTORY_CLICK, Whitelist.empty()),
            RuleEntry.of(FLYING, new FlyingSettings(true, true, 0.05F)),
            RuleEntry.of(HUNGER, new HungerSettings(20, 20f, 0f)));

    public static final List<PerceiveAction> DEATH_PERCEIVE_STATE = List.of(
            new PerceiveAction(Type.START, PerceiveTeam.SPECTATOR, Integer.MAX_VALUE));

    public static @NotNull PlayerState LOBBY;
    public static @NotNull PlayerState PLAY;
    public static @NotNull PlayerState DEATHCAM;
    public static @NotNull PlayerState SPECTATE;

    static {
        LOBBY = new PlayerState()
                .setGamemode(GameMode.ADVENTURE)
                .showTeam(TriState.FALSE);

        PLAY = new PlayerState();

        DEATHCAM = new PlayerState()
                .setGamemode(GameMode.ADVENTURE)
                .setRuleSet(DEATH_RULES)
                .setPerceiveState(DEATH_PERCEIVE_STATE)
                .hideTabIf(other -> true);

        SPECTATE = new PlayerState()
                .setGamemode(GameMode.ADVENTURE)
                .setRuleSet(DEATH_RULES)
                .setPerceiveState(DEATH_PERCEIVE_STATE)
                .hideTabIf(other -> {
                    var state = other.getState();
                    return !Objects.equals(SPECTATE, state);
                })
                .setViewableRule(player -> {
                    var gamePlayer = GamePlayer.fromPlayer(player);
                    var state = gamePlayer.getState();
                    return Objects.equals(SPECTATE, state);
                });
    }

    @Getter private @Nullable GameMode gamemode = GameMode.SURVIVAL;

    @Getter @Accessors(fluent = true)
    private @NotNull TriState showTeam = TriState.TRUE;

    /** More info at {@linkplain PerceiveTeam}. */
    @Getter private @NotNull Collection<PerceiveAction> perceiveState = Collections.emptyList();

    /** This player will be hidden in tab for other players that pass the predicate. */
    @Getter private @Nullable Predicate<GamePlayer> hideTab = null;

    @Getter private @Nullable Predicate<Player> viewableRule = player -> true;
    @Getter private @Nullable Predicate<Entity> viewerRule = entity -> true;

    @Getter private @Nullable RuleSet ruleSet = RuleSet.ofDefaults(ALL_RULES);
    private @Nullable ApplyStrategy applyStrategy = ApplyStrategy.ALWAYS; //Cannot be null if ruleSet also isn't null

    @Getter @Accessors(fluent = true)
    private boolean rulesAffectChildren = false;

    public PlayerState setGamemode(@Nullable GameMode gamemode) {
        this.gamemode = gamemode;
        return this;
    }

    public PlayerState showTeam(@NotNull TriState showTeam) {
        this.showTeam = showTeam;
        return this;
    }

    public PlayerState setPerceiveState(@NotNull Collection<PerceiveAction> perceiveState) {
        this.perceiveState = perceiveState;
        return this;
    }

    public PlayerState hideTabIf(Predicate<GamePlayer> predicate) {
        this.hideTab = predicate;
        return this;
    }

    public PlayerState setViewableRule(@Nullable Predicate<Player> viewableRule) {
        this.viewableRule = viewableRule;
        return this;
    }

    public PlayerState setViewerRule(@Nullable Predicate<Entity> viewerRule) {
        this.viewerRule = viewerRule;
        return this;
    }

    public PlayerState setRuleSet(@Nullable RuleSet ruleSet) {
        this.ruleSet = ruleSet;
        return this;
    }

    public PlayerState setApplyStrategy(@Nullable ApplyStrategy applyStrategy) {
        this.applyStrategy = applyStrategy;
        return this;
    }

    public PlayerState setRulesAffectChildren(boolean rulesAffectChildren) {
        this.rulesAffectChildren = rulesAffectChildren;
        return this;
    }

    public ApplyStrategy getRulesApplyStrategy() {
        return this.applyStrategy;
    }

}