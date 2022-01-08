package cc.minetale.slime.utils.restriction;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Blacklist<T> extends RestrictionList<T> {
    private final Set<T> elements;

    private Blacklist(Set<T> elements) {
        this.elements = Collections.synchronizedSet(new HashSet<>(elements));
    }

    public static <T> Blacklist<T> of(Set<T> elements) {
        return new Blacklist<>(elements);
    }

    public static <T> Blacklist<T> of(T[] elements) {
        return new Blacklist<>(Set.of(elements));
    }

    public static <T> Blacklist<T> empty() {
        return new Blacklist<>(Collections.emptySet());
    }

    @Override
    public boolean isRestricted(T value) {
        return this.elements.contains(value);
    }

    public @NotNull Set<T> getBlacklistedItems() {
        return this.elements;
    }

}
