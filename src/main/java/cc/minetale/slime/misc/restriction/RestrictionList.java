package cc.minetale.slime.misc.restriction;

public abstract class RestrictionList<T> {
    public abstract boolean isRestricted(T value);
}
