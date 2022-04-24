package cc.minetale.slime.rule;

import cc.minetale.slime.core.SlimeForwardingAudience;
import cc.minetale.slime.misc.ApplyStrategy;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public interface IRulable {

    RuleSet getRuleSet();

    //Reading

    default <R extends Rule<T>, T> T getRule(R rule) {
        return getRuleSet().getRule(rule);
    }

    default <R extends Rule<T>, T> T getRuleOrDefault(R rule) {
        return getRuleSet().getRuleOrDefault(rule);
    }

    default <R extends Rule<T>, T> T getRuleOrDefault(R rule, T defaultValue) {
        return getRuleSet().getRuleOrDefault(rule, defaultValue);
    }

    //Writing

    /** Use {@linkplain #setRule(RuleEntry, ApplyStrategy, boolean)} instead. */
    @ApiStatus.Internal
    private <T> void forceSetRule0(RuleEntry<? extends Rule<T>, T> entry, ApplyStrategy strategy, boolean affectChildren, boolean forceChildren) {
        Rule<T> rule = entry.getRule();
        T value = entry.getValue();

        var changed = !Objects.equals(getRule(rule), value);
        getRuleSet().setRule(entry, strategy);

        if(affectChildren) {
            forChildren0(true, rulable -> {
                if(forceChildren) {
                    rulable.forceSetRule0(entry, strategy, true, true);
                } else {
                    rulable.setRule(entry, strategy, true);
                }
            });
        }

        if(changed && rule.listensForRuleChange())
            rule.onRuleChange(this, value);
    }

    /**
     * Applies the consumer to this {@linkplain IRulable} object and its children. <br>
     * If <strong>affectNestedChildren</strong> is true, it'll also loop through this rulable's children's children and so forth.
     */
    @ApiStatus.Internal
    private void forSelfAndChildren0(boolean affectChildren, Consumer<IRulable> consumer) {
        consumer.accept(this);
        if(affectChildren)
            forChildren0(true, consumer);
    }

    /**
     * Unlike {@linkplain #forSelfAndChildren0(boolean, Consumer)} this only <br>
     * loops through this {@linkplain IRulable} object's children and apply the consumer to them, not this object. <br>
     * If <strong>affectChildren</strong> is true, it'll also loop through this rulable's children's children and so forth.
     */
    @ApiStatus.Internal
    private void forChildren0(boolean affectChildren, Consumer<IRulable> consumer) {
        if(this instanceof SlimeForwardingAudience parent) {
            for(var child : parent.audiences()) {
                if(!(child instanceof IRulable rulableChild)) { continue; }
                rulableChild.forSelfAndChildren0(affectChildren, consumer);
            }
        }
    }

    default <T> boolean setRule(RuleEntry<? extends Rule<T>, T> entry, ApplyStrategy strategy, boolean affectChildren) {
        var rule = entry.getRule();
        if(!isRuleApplicable(rule)) { return false; }

        forceSetRule0(entry, strategy, affectChildren, false);
        return true;
    }


    default void setRules(RuleSet ruleSet, ApplyStrategy strategy, boolean affectChildren) {
        forSelfAndChildren0(affectChildren, rulable -> {
            var previousRuleSet = rulable.getRuleSet().clone();
            var newRuleSet = rulable.getRuleSet().merge(ruleSet, strategy);

            for(var ent : newRuleSet.getRules().entrySet()) {
                Rule<Object> rule = (Rule<Object>) ent.getKey();
                if(!rule.listensForRuleChange()) { continue; }

                var oldValue = previousRuleSet.getRule(rule);
                var newValue = ent.getValue();
                if(!Objects.equals(oldValue, newValue))
                    rule.onRuleChange(this, newValue);
            }
        });
    }

    default <R extends Rule<T>, T> T removeRule(R rule, boolean affectChildren) {
        var result = getRuleSet().removeRule(rule);

        if(affectChildren)
            forChildren0(true, rulable -> rulable.getRuleSet().removeRule(rule));

        return result;
    }

    default void removeRules(boolean affectChildren, Collection<? extends Rule<?>> rules) {
        forSelfAndChildren0(affectChildren, rulable -> rulable.getRuleSet().removeRules(rules));
    }

    default void removeRules(boolean affectChildren, Rule<?>... rules) {
        forSelfAndChildren0(affectChildren, rulable -> rulable.getRuleSet().removeRules(rules));
    }

    default boolean hasRule(Rule<?> rule) {
        return getRuleSet().hasRule(rule);
    }

    default void clearRules(boolean affectChildren) {
        forSelfAndChildren0(affectChildren, rulable -> rulable.getRuleSet().clearRules());
    }

    //Other

    default <T> boolean isRuleApplicable(Rule<T> rule) {
        var type = rule.getType();
        return type.isApplicableFor(getClass());
    }

}
