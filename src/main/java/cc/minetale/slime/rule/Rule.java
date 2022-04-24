package cc.minetale.slime.rule;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class Rule<T> {

    public static final Set<Rule<?>> ALL_RULES = Collections.synchronizedSet(new HashSet<>());

    @Getter private final T defaultValue;
    private final @Nullable BiConsumer<Object, T> onRuleChange;

    public Rule(T defaultValue) {
        this(defaultValue, null);
    }

    public Rule(T defaultValue, @Nullable BiConsumer<Object, T> onRuleChange) {
        this.defaultValue = defaultValue;
        this.onRuleChange = onRuleChange;

        ALL_RULES.add(this);
    }

    public abstract IRuleType getType();

    public void onRuleChange(Object affected, T newValue) {
        if(this.onRuleChange == null) { return; }
        this.onRuleChange.accept(affected, newValue);
    }

    public boolean listensForRuleChange() {
        return this.onRuleChange != null;
    }
}
