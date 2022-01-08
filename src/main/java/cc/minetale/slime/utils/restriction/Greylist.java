package cc.minetale.slime.utils.restriction;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Greylist<T> extends RestrictionList<T> {
    private final Set<T> blacklisted;
    private final Set<T> whitelisted;

    private Greylist(Set<T> blacklisted, Set<T> whitelisted) {
        this.blacklisted = Collections.synchronizedSet(new HashSet<>(blacklisted));
        this.whitelisted = Collections.synchronizedSet(new HashSet<>(whitelisted));
    }

    public static <T> Greylist<T> of(Set<T> blacklisted, Set<T> whitelisted) {
        return new Greylist<>(blacklisted, whitelisted);
    }

    public static <T> Greylist<T> of(T[] blacklisted, Set<T> whitelisted) {
        return new Greylist<>(Set.of(blacklisted), whitelisted);
    }

    public static <T> Greylist<T> of(Set<T> blacklisted, T[] whitelisted) {
        return new Greylist<>(blacklisted, Set.of(whitelisted));
    }

    public static <T> Greylist<T> of(T[] blacklisted, T[] whitelisted) {
        return new Greylist<>(Set.of(blacklisted), Set.of(whitelisted));
    }

    public static <T> Greylist<T> empty() {
        return new Greylist<>(Collections.emptySet(), Collections.emptySet());
    }

    @Override
    public boolean isRestricted(T value) {
        return !this.whitelisted.contains(value) || this.blacklisted.contains(value);
    }

    public @NotNull Set<T> getBlacklistedItems() {
        return this.blacklisted;
    }
    public @NotNull Set<T> getWhitelistedItems() {
        return this.whitelisted;
    }

    public @NotNull Sets.SetView<T> getAllowedItems() {
        synchronized (this.blacklisted) {
            synchronized (this.whitelisted) {
                return Sets.symmetricDifference(new HashSet<>(this.whitelisted), new HashSet<>(this.blacklisted));
            }
        }
    }

}
