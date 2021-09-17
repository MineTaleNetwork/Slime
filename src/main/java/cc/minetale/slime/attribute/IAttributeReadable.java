package cc.minetale.slime.attribute;

import java.util.Objects;

public interface IAttributeReadable {
    <T> T getAttribute(Attribute attr);
    default <T> T getAttributeOrDefault(Attribute attr, T defaultValue) {
        return Objects.requireNonNullElse(this.getAttribute(attr), defaultValue);
    }
}
