package cc.minetale.slime.rule;

import java.util.Objects;

public interface IRuleReadable {
    <R extends Rule<T>, T> T getRule(R rule);

    default <R extends Rule<T>, T> T getRuleOrDefault(R rule) {
        return Objects.requireNonNullElse(this.getRule(rule), rule.getDefaultValue());
    }

    default <R extends Rule<T>, T> T getRuleOrDefault(R rule, T defaultValue) {
        return Objects.requireNonNullElse(this.getRule(rule), defaultValue);
    }
}
