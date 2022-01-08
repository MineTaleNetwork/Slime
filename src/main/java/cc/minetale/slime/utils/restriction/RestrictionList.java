package cc.minetale.slime.utils.restriction;

public abstract class RestrictionList<T> {
    public abstract boolean isRestricted(T value);
}
