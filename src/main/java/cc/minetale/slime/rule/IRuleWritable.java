package cc.minetale.slime.rule;

public interface IRuleWritable {
    <T> void setRule(Rule<T> rule, T value, boolean affectChildren);
}
