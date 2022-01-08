package cc.minetale.slime.rule;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UniversalRule<T> extends Rule<T> {

    public static final Set<UniversalRule<?>> ALL_RULES = Collections.synchronizedSet(new HashSet<>());

    public UniversalRule(T defaultValue) {
        super(defaultValue);
        ALL_RULES.add(this);
    }

    //None for now, some game modes might use it

}
