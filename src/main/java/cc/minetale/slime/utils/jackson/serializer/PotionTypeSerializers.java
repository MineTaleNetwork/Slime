package cc.minetale.slime.utils.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minestom.server.potion.PotionType;
import net.minestom.server.utils.NamespaceID;

import java.io.IOException;

public class PotionTypeSerializers {
    public static class Serializer extends StdSerializer<PotionType> {
        public Serializer() {
            this(null);
        }

        public Serializer(Class<PotionType> t) {
            super(t);
        }

        @Override
        public void serialize(PotionType value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeObject(value.namespace());
        }
    }

    public static class Deserializer extends StdDeserializer<PotionType> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public PotionType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();
            return PotionType.fromNamespaceId(codec.readValue(jp, NamespaceID.class));
        }
    }
}
