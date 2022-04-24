package cc.minetale.slime.rule;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.Set;

@AllArgsConstructor
public class RuleType implements IRuleType {
    private final Set<@NotNull Class<?>> applicableTypes;
    private final boolean allowSubtypes;

    public RuleType(boolean allowSubtypes, Set<@NotNull Class<?>> applicableTypes) {
        this.allowSubtypes = allowSubtypes;
        this.applicableTypes = Set.copyOf(applicableTypes);
    }

    public RuleType(boolean allowSubtypes, Class<?>... applicableTypes) {
        this(allowSubtypes, Set.of(applicableTypes));
    }

    @Override
    public @UnmodifiableView Set<@NotNull Class<?>> getApplicableTypes() {
        return Collections.unmodifiableSet(this.applicableTypes);
    }

    @Override
    public boolean allowsSubtypes() {
        return this.allowSubtypes;
    }
}
