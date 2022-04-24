package cc.minetale.slime.rule;

import cc.minetale.slime.misc.ApplyStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class RuleSet implements Cloneable {

    private Map<Rule<?>, Object> rules;

    private RuleSet(Map<? extends Rule<?>, ?> rules) {
        this.rules = Collections.synchronizedMap(new HashMap<>(rules));
    }

    private RuleSet(RuleEntry<? extends Rule<?>, ?>[] entries) {
        Map<Rule<?>, Object> temp = new HashMap<>();
        for(RuleEntry<? extends Rule<?>, ?> entry : entries)
            temp.put(entry.getRule(), entry.getValue());

        this.rules = Collections.synchronizedMap(temp);
    }

    private RuleSet(Collection<RuleEntry<? extends Rule<?>, ?>> entries) {
        Map<Rule<?>, Object> temp = new HashMap<>();
        for(RuleEntry<? extends Rule<?>, ?> entry : entries)
            temp.put(entry.getRule(), entry.getValue());

        this.rules = Collections.synchronizedMap(temp);
    }

    private RuleSet() {
        this(Collections.emptyMap());
    }

    public static RuleSet of(RuleEntry<? extends Rule<?>, ?> entry) {
        return new RuleSet(Collections.singletonMap(entry.getRule(), entry.getValue()));
    }

    public static RuleSet of(Collection<RuleEntry<? extends Rule<?>, ?>> entries) {
        return new RuleSet(entries);
    }

    @SafeVarargs
    public static RuleSet of(RuleEntry<? extends Rule<?>, ?>... entries) {
        return new RuleSet(entries);
    }

    public static RuleSet ofDefaults(Collection<? extends Rule<?>> rules) {
        Map<Rule<?>, Object> defaults = new HashMap<>();
        for(Rule<?> rule : rules) {
            Map.Entry<? extends Rule<?>, ?> entry = Map.entry(rule, rule.getDefaultValue());
            defaults.put(entry.getKey(), entry.getValue());
        }

        return new RuleSet(defaults);
    }

    public static RuleSet ofDefaults(Rule<?>... rules) {
        return ofDefaults(List.of(rules));
    }

    public static RuleSet ofDefaultsWith(Collection<? extends Rule<?>> defaultRules, Collection<RuleEntry<? extends Rule<?>, ?>> override) {
        Map<Rule<?>, Object> rules = new HashMap<>();
        for(Rule<?> rule : defaultRules)
            rules.put(rule, rule.getDefaultValue());

        for(RuleEntry<? extends Rule<?>, ?> entry : override)
            rules.put(entry.getRule(), entry.getValue());

        return new RuleSet(rules);
    }

    @SafeVarargs
    public static RuleSet ofDefaultsWith(Collection<? extends Rule<?>> defaultRules, RuleEntry<? extends Rule<?>, ?>... override) {
        return ofDefaultsWith(defaultRules, List.of(override));
    }

    public static RuleSet empty() {
        return new RuleSet();
    }

    @SuppressWarnings("unchecked")
    public <R extends Rule<T>, T> T getRule(R rule) {
        return (T) this.rules.get(rule);
    }

    public <R extends Rule<T>, T> T getRuleOrDefault(R rule, T defaultValue) {
        return hasRule(rule) ? getRule(rule) : defaultValue;
    }

    public <R extends Rule<T>, T> T getRuleOrDefault(R rule) {
        return getRuleOrDefault(rule, rule.getDefaultValue());
    }

    public @NotNull @UnmodifiableView Map<Rule<?>, Object> getRules() {
        return Collections.unmodifiableMap(this.rules);
    }

    @SuppressWarnings("unchecked")
    public <R extends Rule<T>, T> @Nullable T setRule(RuleEntry<R, T> entry, ApplyStrategy strategy) {
        return (T) switch(strategy) {
            case ALWAYS -> this.rules.put(entry.getRule(), entry.getValue());
            case NOT_SET -> this.rules.putIfAbsent(entry.getRule(), entry.getValue());
        };
    }

    @SuppressWarnings("unchecked")
    public <R extends Rule<T>, T> T removeRule(R rule) {
        return (T) this.rules.remove(rule);
    }

    public void removeRules(Collection<? extends Rule<?>> rules) {
        rules.forEach(this::removeRule);
    }

    public void removeRules(Rule<?>... rules) {
        removeRules(List.of(rules));
    }

    public boolean hasRule(Rule<?> rule) {
        return getRules().containsKey(rule);
    }

    public void clearRules() {
        this.rules = Collections.synchronizedMap(new HashMap<>());
    }

    public RuleSet merge(RuleSet src, ApplyStrategy strategy) {
        Map<Rule<?>, Object> mergedRules = new HashMap<>(this.rules);

        Map<Rule<?>, Object> srcRules = src.getRules();
        for(Map.Entry<Rule<?>, Object> srcRule : srcRules.entrySet()) {
            switch(strategy) {
                case ALWAYS -> mergedRules.put(srcRule.getKey(), srcRule.getValue());
                case NOT_SET -> mergedRules.putIfAbsent(srcRule.getKey(), srcRule.getValue());
            }
        }

        this.rules = Collections.synchronizedMap(mergedRules);

        return this;
    }

    @Override
    public RuleSet clone() {
        try {
            RuleSet clone = (RuleSet) super.clone();
            clone.rules = Collections.synchronizedMap(new HashMap<>(this.rules));
            return clone;
        } catch(CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
