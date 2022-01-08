package cc.minetale.slime.rule;

import cc.minetale.slime.condition.EndCondition;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameRule<T> extends Rule<T> {

    public static final Set<GameRule<?>> ALL_RULES = Collections.synchronizedSet(new HashSet<>());

    public GameRule(T defaultValue) {
        super(defaultValue);
        ALL_RULES.add(this);
    }

    /**
     * {@linkplain Boolean} <br>
     * <br>
     * Automatically checks for {@linkplain EndCondition}.
     */
    public static final GameRule<Boolean> AUTO_END = new GameRule<>(true);

}
