package cc.minetale.slime.rule;

import cc.minetale.slime.utils.ApplyStrategy;

public interface IRuleWritable {
    <T> void setRule(Rule<T> rule, T value, ApplyStrategy strategy, boolean affectChildren);
}
