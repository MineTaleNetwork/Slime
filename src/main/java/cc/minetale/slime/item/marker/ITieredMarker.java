package cc.minetale.slime.item.marker;

import cc.minetale.slime.item.category.ICategorizedTiered;
import cc.minetale.slime.item.category.ICategory;
import cc.minetale.slime.item.category.ITieredCategory;
import cc.minetale.slime.item.tier.ITieredItem;
import cc.minetale.slime.utils.IMarker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface ITieredMarker<T extends ITieredItem> extends IMarker<T> {
    static <T extends ITieredItem> Empty<T> all() {
        return new Empty<T>(true);
    }

    static <T extends ITieredItem> Empty<T> none() {
        return new Empty<T>(false);
    }

    static <T extends ITieredItem> Single<T> of(T item) {
        return new Single<>(item);
    }

    @SafeVarargs
    static <T extends ITieredItem> Multiple<T> of(T... items) {
        return new Multiple<>(new HashSet<>(Set.of(items)));
    }

    static <T extends ITieredItem> Multiple<T> of(Collection<T> items) {
        return new Multiple<>(new HashSet<>(items));
    }

    static <T extends ICategorizedTiered> Category<T> ofCategory(ITieredCategory<T> category) {
        return new Category<>(category);
    }

    @SafeVarargs
    static <T extends ICategorizedTiered> Categories<T> ofCategories(ITieredCategory<T>... categories) {
        return new Categories<>(new HashSet<>(Set.of(categories)));
    }

    static <T extends ICategorizedTiered> Categories<T> ofCategories(Collection<? extends ITieredCategory<T>> categories) {
        return new Categories<>(new HashSet<>(categories));
    }

    static <T extends ITieredItem> Complement<T> complement(ITieredMarker<T> term) {
        return new Complement<>(term);
    }

    @SafeVarargs
    static <T extends ITieredItem> Union<T> union(ITieredMarker<T>... terms) {
        return new Union<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends ITieredItem> Union<T> union(Collection<? extends ITieredMarker<T>> terms) {
        return new Union<>(new HashSet<>(terms));
    }

    static <T extends ITieredItem> Subtraction<T> subtraction(ITieredMarker<T> minuend, ITieredMarker<T> subtrahend) {
        return new Subtraction<>(minuend, new HashSet<>(Set.of(subtrahend)));
    }

    @SafeVarargs
    static <T extends ITieredItem> Subtraction<T> subtraction(ITieredMarker<T> minuend, ITieredMarker<T>... subtrahends) {
        return new Subtraction<>(minuend, new HashSet<>(Set.of(subtrahends)));
    }

    static <T extends ITieredItem> Subtraction<T> subtraction(ITieredMarker<T> minuend, Collection<? extends ITieredMarker<T>> subtrahends) {
        return new Subtraction<>(minuend, new HashSet<>(subtrahends));
    }

    @SafeVarargs
    static <T extends ITieredItem> Intersection<T> intersection(ITieredMarker<T>... terms) {
        return new Intersection<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends ITieredItem> Intersection<T> intersection(Collection<? extends ITieredMarker<T>> terms) {
        return new Intersection<>(new HashSet<>(terms));
    }

    @SafeVarargs
    static <T extends ITieredItem> Exclusive<T> exclusive(ITieredMarker<T>... terms) {
        return new Exclusive<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends ITieredItem> Exclusive<T> exclusive(Collection<? extends ITieredMarker<T>> terms) {
        return new Exclusive<>(new HashSet<>(terms));
    }

    class Empty<T extends ITieredItem> extends IMarker.Empty<T> implements ITieredMarker<T> {
        public Empty(boolean containsAll) { super(containsAll); }
    }

    class Single<T extends ITieredItem> extends IMarker.Single<T> implements ITieredMarker<T> {
        public Single(T target) { super(target); }
    }

    class Multiple<T extends ITieredItem> extends IMarker.Multiple<T> implements ITieredMarker<T> {
        public Multiple(Set<T> targets) { super(targets); }
    }

    class Category<T extends ICategorizedTiered> extends IMarker.Category<T> implements ITieredMarker<T> {
        public Category(ICategory<T> category) { super(category); }
    }

    class Categories<T extends ICategorizedTiered> extends IMarker.Categories<T> implements ITieredMarker<T> {
        public Categories(Set<ICategory<T>> categories) { super(categories); }
    }

    class Complement<T extends ITieredItem> extends IMarker.Complement<T> implements ITieredMarker<T> {
        public Complement(ITieredMarker<T> term) { super(term); }
    }

    class Union<T extends ITieredItem> extends IMarker.Union<T> implements ITieredMarker<T> {
        public Union(Set<? extends ITieredMarker<T>> terms) { super(terms); }
    }

    class Subtraction<T extends ITieredItem> extends IMarker.Subtraction<T> implements ITieredMarker<T> {
        public Subtraction(ITieredMarker<T> minuend, Set<? extends ITieredMarker<T>> subtrahends) { super(minuend, subtrahends); }
    }

    class Intersection<T extends ITieredItem> extends IMarker.Intersection<T> implements ITieredMarker<T> {
        public Intersection(Set<? extends ITieredMarker<T>> terms) { super(terms); }
    }

    class Exclusive<T extends ITieredItem> extends IMarker.Exclusive<T> implements ITieredMarker<T> {
        public Exclusive(Set<? extends ITieredMarker<T>> terms) { super(terms); }
    }
}
