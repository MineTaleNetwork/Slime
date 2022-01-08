package cc.minetale.slime.rule;

import lombok.Getter;

public abstract class Rule<T> {
    @Getter private final T defaultValue;
    public Rule(T defaultValue) {
        this.defaultValue = defaultValue;
    }
}
