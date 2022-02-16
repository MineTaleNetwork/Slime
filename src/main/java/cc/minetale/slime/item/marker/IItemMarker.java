package cc.minetale.slime.item.marker;

import cc.minetale.slime.item.base.IItem;
import cc.minetale.slime.item.category.ICategorizedItem;
import cc.minetale.slime.item.category.ICategory;
import cc.minetale.slime.item.category.IItemCategory;
import cc.minetale.slime.utils.IMarker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public interface IItemMarker<T extends IItem> extends IMarker<T> {
    static <T extends IItem> Empty<T> all() {
        return new Empty<T>(true);
    }

    static <T extends IItem> Empty<T> none() {
        return new Empty<T>(false);
    }

    static <T extends IItem> Single<T> of(T item) {
        return new Single<>(item);
    }

    @SafeVarargs
    static <T extends IItem> Multiple<T> of(T... items) {
        return new Multiple<>(new HashSet<>(Set.of(items)));
    }

    static <T extends IItem> Multiple<T> of(Collection<T> items) {
        return new Multiple<>(new HashSet<>(items));
    }

    static <T extends ICategorizedItem> Category<T> ofCategory(IItemCategory<T> category) {
        return new Category<>(category);
    }

    @SafeVarargs
    static <T extends ICategorizedItem> Categories<T> ofCategories(IItemCategory<T>... categories) {
        return new Categories<>(new HashSet<>(Set.of(categories)));
    }

    static <T extends ICategorizedItem> Categories<T> ofCategories(Collection<? extends IItemCategory<T>> categories) {
        return new Categories<>(new HashSet<>(categories));
    }

    static <T extends IItem> Complement<T> complement(IItemMarker<T> term) {
        return new Complement<>(term);
    }

    @SafeVarargs
    static <T extends IItem> Union<T> union(IItemMarker<T>... terms) {
        return new Union<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends IItem> Union<T> union(Collection<? extends IItemMarker<T>> terms) {
        return new Union<>(new HashSet<>(terms));
    }

    static <T extends IItem> Subtraction<T> subtraction(IItemMarker<T> minuend, IItemMarker<T> subtrahend) {
        return new Subtraction<>(minuend, new HashSet<>(Set.of(subtrahend)));
    }

    @SafeVarargs
    static <T extends IItem> Subtraction<T> subtraction(IItemMarker<T> minuend, IItemMarker<T>... subtrahends) {
        return new Subtraction<>(minuend, new HashSet<>(Set.of(subtrahends)));
    }

    static <T extends IItem> Subtraction<T> subtraction(IItemMarker<T> minuend, Collection<? extends IItemMarker<T>> subtrahends) {
        return new Subtraction<>(minuend, new HashSet<>(subtrahends));
    }

    @SafeVarargs
    static <T extends IItem> Intersection<T> intersection(IItemMarker<T>... terms) {
        return new Intersection<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends IItem> Intersection<T> intersection(Collection<? extends IItemMarker<T>> terms) {
        return new Intersection<>(new HashSet<>(terms));
    }

    @SafeVarargs
    static <T extends IItem> Exclusive<T> exclusive(IItemMarker<T>... terms) {
        return new Exclusive<>(new HashSet<>(Set.of(terms)));
    }

    static <T extends IItem> Exclusive<T> exclusive(Collection<? extends IItemMarker<T>> terms) {
        return new Exclusive<>(new HashSet<>(terms));
    }

    class Empty<T extends IItem> extends IMarker.Empty<T> implements IItemMarker<T> {
        public Empty(boolean containsAll) { super(containsAll); }
    }

    class Single<T extends IItem> extends IMarker.Single<T> implements IItemMarker<T> {
        public Single(T target) { super(target); }
    }

    class Multiple<T extends IItem> extends IMarker.Multiple<T> implements IItemMarker<T> {
        public Multiple(Set<T> targets) { super(targets); }
    }

    class Category<T extends ICategorizedItem> extends IMarker.Category<T> implements IItemMarker<T> {
        public Category(ICategory<T> category) { super(category); }
    }

    class Categories<T extends ICategorizedItem> extends IMarker.Categories<T> implements IItemMarker<T> {
        public Categories(Set<ICategory<T>> categories) { super(categories); }
    }

    class Complement<T extends IItem> extends IMarker.Complement<T> implements IItemMarker<T> {
        public Complement(IItemMarker<T> term) { super(term); }
    }

    class Union<T extends IItem> extends IMarker.Union<T> implements IItemMarker<T> {
        public Union(Set<? extends IItemMarker<T>> terms) { super(terms); }
    }

    class Subtraction<T extends IItem> extends IMarker.Subtraction<T> implements IItemMarker<T> {
        public Subtraction(IItemMarker<T> minuend, Set<? extends IItemMarker<T>> subtrahends) { super(minuend, subtrahends); }
    }

    class Intersection<T extends IItem> extends IMarker.Intersection<T> implements IItemMarker<T> {
        public Intersection(Set<? extends IItemMarker<T>> terms) { super(terms); }
    }

    class Exclusive<T extends IItem> extends IMarker.Exclusive<T> implements IItemMarker<T> {
        public Exclusive(Set<? extends IItemMarker<T>> terms) { super(terms); }
    }
}
