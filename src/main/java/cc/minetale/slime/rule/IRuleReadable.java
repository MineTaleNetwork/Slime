package cc.minetale.slime.rule;

import java.util.Objects;

public interface IRuleReadable {
    <T> T getRule(Rule<T> rule);
    default <T> T getRuleOrDefault(Rule<T> rule, T defaultValue) {
        return Objects.requireNonNullElse(this.getRule(rule), defaultValue);
    }
}
