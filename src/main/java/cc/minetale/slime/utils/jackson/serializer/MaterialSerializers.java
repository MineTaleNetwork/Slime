package cc.minetale.slime.utils.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;

import java.io.IOException;

public class MaterialSerializers {
    public static class Serializer extends StdSerializer<Material> {
        public Serializer() {
            this(null);
        }

        public Serializer(Class<Material> t) {
            super(t);
        }

        @Override
        public void serialize(Material value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value.namespace());
        }
    }

    public static class Deserializer extends StdDeserializer<Material> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Material deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();
            return Material.fromNamespaceId(codec.readValue(jp, NamespaceID.class));
        }
    }
}
