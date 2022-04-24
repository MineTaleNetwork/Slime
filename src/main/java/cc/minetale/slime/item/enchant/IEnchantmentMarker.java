package cc.minetale.slime.item.enchant;

import cc.minetale.slime.misc.IMarker;
import net.minestom.server.item.Enchantment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IEnchantmentMarker<T extends Enchantment> extends IMarker<T> {
    static <T extends Enchantment> Empty<T> all() {
        return new Empty<T>(true);
    }

    static <T extends Enchantment> Empty<T> none() {
        return new Empty<T>(false);
    }

    static <T extends Enchantment> Single<T> of(T item) {
        return new Single<>(item);
    }

    @SafeVarargs
    static <T extends Enchantment> Multiple<T> of(T... items) {
        return new Multiple<>(new HashSet<>(Set.of(items)));
    }

    static <T extends Enchantment> Multiple<T> of(Collection<T> items) {
        return new Multiple<>(new HashSet<>(items));
    }

    static <T extends Enchantment> Complement<T> complement(IEnchantmentMarker<T> term) {
        return new Complement<>(term);
    }

    @SafeVarargs
    static <T extends Enchantment> Union<T> union(IEnchantmentMarker<T>... terms) {
        return new Union<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends Enchantment> Union<T> union(Collection<? extends IEnchantmentMarker<T>> terms) {
        return new Union<>(new HashSet<>(terms));
    }

    static <T extends Enchantment> Subtraction<T> subtraction(IEnchantmentMarker<T> minuend, IEnchantmentMarker<T> subtrahend) {
        return new Subtraction<>(minuend, new HashSet<>(Set.of(subtrahend)));
    }

    @SafeVarargs
    static <T extends Enchantment> Subtraction<T> subtraction(IEnchantmentMarker<T> minuend, IEnchantmentMarker<T>... subtrahends) {
        return new Subtraction<>(minuend, new HashSet<>(Set.of(subtrahends)));
    }

    static <T extends Enchantment> Subtraction<T> subtraction(IEnchantmentMarker<T> minuend, Collection<? extends IEnchantmentMarker<T>> subtrahends) {
        return new Subtraction<>(minuend, new HashSet<>(subtrahends));
    }

    @SafeVarargs
    static <T extends Enchantment> Intersection<T> intersection(IEnchantmentMarker<T>... terms) {
        return new Intersection<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends Enchantment> Intersection<T> intersection(Collection<? extends IEnchantmentMarker<T>> terms) {
        return new Intersection<>(new HashSet<>(terms));
    }

    @SafeVarargs
    static <T extends Enchantment> Exclusive<T> exclusive(IEnchantmentMarker<T>... terms) {
        return new Exclusive<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends Enchantment> Exclusive<T> exclusive(Collection<? extends IEnchantmentMarker<T>> terms) {
        return new Exclusive<>(new HashSet<>(terms));
    }

    class Empty<T extends Enchantment> extends IMarker.Empty<T> implements IEnchantmentMarker<T> {
        public Empty(boolean containsAll) { super(containsAll); }
    }

    class Single<T extends Enchantment> extends IMarker.Single<T> implements IEnchantmentMarker<T> {
        public Single(T item) { super(item); }
    }

    class Multiple<T extends Enchantment> extends IMarker.Multiple<T> implements IEnchantmentMarker<T> {
        public Multiple(Set<T> items) { super(items); }
    }

    class Complement<T extends Enchantment> extends IMarker.Complement<T> implements IEnchantmentMarker<T> {
        public Complement(IEnchantmentMarker<T> term) { super(term); }
    }

    class Union<T extends Enchantment> extends IMarker.Union<T> implements IEnchantmentMarker<T> {
        public Union(Set<? extends IEnchantmentMarker<T>> terms) { super(terms); }
    }

    class Subtraction<T extends Enchantment> extends IMarker.Subtraction<T> implements IEnchantmentMarker<T> {
        public Subtraction(IEnchantmentMarker<T> minuend, Set<? extends IEnchantmentMarker<T>> subtrahends) { super(minuend, subtrahends); }
    }

    class Intersection<T extends Enchantment> extends IMarker.Intersection<T> implements IEnchantmentMarker<T> {
        public Intersection(Set<? extends IEnchantmentMarker<T>> terms) { super(terms); }
    }

    class Exclusive<T extends Enchantment> extends IMarker.Exclusive<T> implements IEnchantmentMarker<T> {
        public Exclusive(Set<? extends IEnchantmentMarker<T>> terms) { super(terms); }
    }
}
