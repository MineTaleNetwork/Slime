package cc.minetale.slime.rule;

import cc.minetale.slime.utils.ApplyStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class RuleSet {

    private final Map<Rule<?>, Object> rules;

    private RuleSet(Map<? extends Rule<?>, ?> rules) {
        this.rules = Collections.synchronizedMap(new HashMap<>(rules));
    }

    private RuleSet(Entry<? extends Rule<?>, ?>[] entries) {
        Map<Rule<?>, Object> temp = new HashMap<>();
        for(Entry<? extends Rule<?>, ?> entry : entries)
            temp.put(entry.rule(), entry.value());

        this.rules = Collections.synchronizedMap(temp);
    }

    private RuleSet(Collection<Entry<? extends Rule<?>, ?>> entries) {
        Map<Rule<?>, Object> temp = new HashMap<>();
        for(Entry<? extends Rule<?>, ?> entry : entries)
            temp.put(entry.rule(), entry.value());

        this.rules = Collections.synchronizedMap(temp);
    }

    private RuleSet() {
        this(Collections.emptyMap());
    }

    public static RuleSet of(Entry<? extends Rule<?>, ?> entry) {
        return new RuleSet(Collections.singletonMap(entry.rule(), entry.value()));
    }

    public static RuleSet of(Collection<Entry<? extends Rule<?>, ?>> entries) {
        return new RuleSet(entries);
    }

    @SafeVarargs
    public static RuleSet of(Entry<? extends Rule<?>, ?>... entries) {
        return new RuleSet(entries);
    }

    public static RuleSet empty() {
        return new RuleSet();
    }

    @SuppressWarnings("unchecked")
    public <R extends Rule<T>, T> @Nullable T setRule(R rule, T value) {
        return (T) this.rules.put(rule, value);
    }

    @SuppressWarnings("unchecked")
    public <R extends Rule<T>, T> T getRule(R rule) {
        return (T) this.rules.get(rule);
    }

    public <R extends Rule<T>, T> T getRuleOrDefault(R rule, T defaultValue) {
        return Objects.requireNonNullElse(getRule(rule), defaultValue);
    }

    public <R extends Rule<T>, T> T getRuleOrDefault(R rule) {
        return getRuleOrDefault(rule, rule.getDefaultValue());
    }

    public @NotNull @UnmodifiableView Map<? extends Rule<?>, ?> getRules() {
        return Collections.unmodifiableMap(this.rules);
    }

    @SuppressWarnings("unchecked")
    public <R extends Rule<T>, T> void applyFor(IRuleWritable writable, ApplyStrategy strategy, boolean affectChildren) {
        for(Map.Entry<Rule<?>, Object> ent : this.rules.entrySet()) {
            var rule = (R) ent.getKey();
            var value = (T) ent.getValue();

            writable.setRule(rule, value, strategy, affectChildren);
        }
    }

    public record Entry<R extends Rule<T>, T>(R rule, T value) { }

}
