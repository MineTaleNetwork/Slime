package cc.minetale.slime.rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class UniversalRule<T> extends Rule<T> {

    public static final Set<UniversalRule<?>> ALL_UNIVERSAL_RULES = Collections.synchronizedSet(new HashSet<>());

    public static final IRuleType TYPE = new RuleType(true);

    public UniversalRule(T defaultValue) {
        super(defaultValue);
        ALL_UNIVERSAL_RULES.add(this);
    }

    public UniversalRule(T defaultValue, BiConsumer<Object, T> onRuleChange) {
        super(defaultValue, onRuleChange);
        ALL_UNIVERSAL_RULES.add(this);
    }

    @Override
    public IRuleType getType() {
        return TYPE;
    }

    //None for now, some game modes might use it

}
