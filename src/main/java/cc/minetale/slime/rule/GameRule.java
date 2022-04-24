package cc.minetale.slime.rule;

import cc.minetale.slime.condition.IEndCondition;
import cc.minetale.slime.game.Game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class GameRule<T> extends Rule<T> {

    public static final Set<GameRule<?>> ALL_GAME_RULES = Collections.synchronizedSet(new HashSet<>());

    public static final IRuleType TYPE = new RuleType(true, Game.class);

    public GameRule(T defaultValue) {
        super(defaultValue);
        ALL_GAME_RULES.add(this);
    }

    public GameRule(T defaultValue, BiConsumer<Game, T> onRuleChange) {
        super(defaultValue, (BiConsumer) onRuleChange);
        ALL_GAME_RULES.add(this);
    }

    @Override
    public IRuleType getType() {
        return TYPE;
    }

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * Automatically checks for {@linkplain IEndCondition} that's currently set in {@linkplain Game}.
     */
    public static final GameRule<Boolean> AUTO_END = new GameRule<>(true);

}
