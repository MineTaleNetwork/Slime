package cc.minetale.slime.attribute;

public interface IAttributeWritable {
    <T> void setAttribute(Attribute<T> attr, T value);
}
