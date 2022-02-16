package cc.minetale.slime.utils;

import cc.minetale.slime.item.category.ICategorized;
import cc.minetale.slime.item.category.ICategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface IMarker<T> {
    Set<T> getMarked();
    boolean isMarked(T value);

    @AllArgsConstructor
    class Empty<T> implements IMarker<T> {
        private final boolean containsAll;

        @Override
        public Set<T> getMarked() {
            return Collections.emptySet();
        }

        @Override
        public boolean isMarked(T item) {
            return this.containsAll;
        }
    }

    @AllArgsConstructor
    class Single<T> implements IMarker<T> {
        private final T marked;

        @Override
        public Set<T> getMarked() {
            return Collections.singleton(this.marked);
        }

        @Override
        public boolean isMarked(T value) {
            return this.marked.equals(value);
        }
    }

    @Getter @AllArgsConstructor
    class Multiple<T> implements IMarker<T> {
        private final Set<T> marked;

        @Override
        public boolean isMarked(T value) {
            return this.marked.contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Category<T extends ICategorized<?>> implements IMarker<T> {
        private final ICategory<T> category;

        @Override
        public Set<T> getMarked() {
            return this.category.getAllItems();
        }

        @Override
        public boolean isMarked(T value) {
            return getMarked().contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Categories<T extends ICategorized<?>> implements IMarker<T> {
        private final Set<ICategory<T>> categories;

        @Override
        public Set<T> getMarked() {
            return this.categories.stream()
                    .map(ICategory::getAllItems)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }

        @Override
        public boolean isMarked(T value) {
            return getMarked().contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Complement<T> implements IMarker<T> {
        private final IMarker<T> term;

        @Override
        public Set<T> getMarked() {
            return this.term.getMarked();
        }

        @Override
        public boolean isMarked(T value) {
            return !getMarked().contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Union<T> implements IMarker<T> {
        private final Set<? extends IMarker<T>> terms;

        @Override
        public Set<T> getMarked() {
            return this.terms.stream()
                    .map(IMarker::getMarked)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        }

        @Override
        public boolean isMarked(T value) {
            return getMarked().contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Subtraction<T> implements IMarker<T> {
        private final IMarker<T> minuend;
        private final Set<? extends IMarker<T>> subtrahends;

        @Override
        public Set<T> getMarked() {
            Set<T> diff = new HashSet<>(this.minuend.getMarked());
            for(var subtrahend : this.subtrahends) {
                diff.removeAll(subtrahend.getMarked());
            }
            return diff;
        }

        @Override
        public boolean isMarked(T value) {
            return getMarked().contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Intersection<T> implements IMarker<T> {
        private final Set<? extends IMarker<T>> terms;

        @Override
        public Set<T> getMarked() {
            Set<T> result = new HashSet<>();

            for(var term : this.terms) {
                Set<T> targets = term.getMarked();

                for(var diffTerm : this.terms) {
                    if(diffTerm == term) { continue; }

                    Set<T> diffTargets = term.getMarked();
                    targets.retainAll(diffTargets);
                }

                result.addAll(targets);
            }

            return result;
        }

        @Override
        public boolean isMarked(T value) {
            return getMarked().contains(value);
        }
    }

    @Getter @AllArgsConstructor
    class Exclusive<T> implements IMarker<T> {
        private final Set<? extends IMarker<T>> terms;

        @Override
        public Set<T> getMarked() {
            Set<T> result = new HashSet<>();

            for(var term : this.terms) {
                Set<T> targets = term.getMarked();

                for(var diffTerm : this.terms) {
                    if(diffTerm == term) { continue; }

                    Set<T> diffTargets = term.getMarked();
                    targets.removeAll(diffTargets);
                }

                result.addAll(targets);
            }

            return result;
        }

        @Override
        public boolean isMarked(T value) {
            return getMarked().contains(value);
        }
    }
}
