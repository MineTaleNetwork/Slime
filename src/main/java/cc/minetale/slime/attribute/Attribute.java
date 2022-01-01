package cc.minetale.slime.attribute;

import lombok.Getter;

public class Attribute<T> {
    @Getter private final T defaultValue;
    public Attribute(T defaultValue) {
        this.defaultValue = defaultValue;
        Attributes.ALL_ATTRIBUTES.add(this);
    }
}
