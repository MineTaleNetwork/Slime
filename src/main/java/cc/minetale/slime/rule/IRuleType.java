package cc.minetale.slime.rule;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The default implementation is {@linkplain RuleType}. <br>
 * Allows you to restrict usage of {@linkplain Rule}s to certain types.
 */
public interface IRuleType {
    Set<@NotNull Class<?>> getApplicableTypes();

    boolean allowsSubtypes();

    default boolean isApplicableFor(Class<?> type) {
        Set<@NotNull Class<?>> applicableTypes = getApplicableTypes();
        if(applicableTypes.isEmpty()) { return true; }

        for(Class<?> applicableType : applicableTypes) {
            if(applicableType.isAssignableFrom(type)) { return true; }
        }

        return false;
    }
}
