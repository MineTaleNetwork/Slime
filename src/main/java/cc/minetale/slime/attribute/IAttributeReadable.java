package cc.minetale.slime.attribute;

import java.util.Objects;

public interface IAttributeReadable {
    <T> T getAttribute(Attribute<T> attr);
    default <T> T getAttributeOrDefault(Attribute<T> attr, T defaultValue) {
        return Objects.requireNonNullElse(this.getAttribute(attr), defaultValue);
    }
}
