package cc.minetale.slime.rule;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public class RuleEntry<R extends Rule<T>, T> {
    private final R rule;
    @Setter private T value;

    private RuleEntry(R rule, T value) {
        this.rule = rule;
        this.value = value;
    }

    public static <R extends Rule<T>, T> RuleEntry<R, T> of(R rule, T value) {
        return new RuleEntry<>(rule, value);
    }

    public static <R extends Rule<T>, T> RuleEntry<R, T> ofDefault(R rule) {
        return of(rule, rule.getDefaultValue());
    }

    public Map.Entry<R, T> asMapEntry() {
        return Map.entry(this.rule, this.value);
    }
}
