package cc.minetale.slime.utils.restriction;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Whitelist<T> extends RestrictionList<T> {
    private final Set<T> elements;

    private Whitelist(Set<T> elements) {
        this.elements = Collections.synchronizedSet(new HashSet<>(elements));
    }

    public static <T> Whitelist<T> of(Set<T> elements) {
        return new Whitelist<>(elements);
    }

    public static <T> Whitelist<T> of(T[] elements) {
        return new Whitelist<>(Set.of(elements));
    }

    public static <T> Whitelist<T> empty() {
        return new Whitelist<>(Collections.emptySet());
    }

    @Override
    public boolean isRestricted(T value) {
        return !this.elements.contains(value);
    }

    public @NotNull Set<T> getWhitelistedItems() {
        return this.elements;
    }

}
