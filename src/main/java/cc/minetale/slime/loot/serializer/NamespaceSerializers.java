package cc.minetale.slime.loot.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.minestom.server.utils.NamespaceID;

import java.io.IOException;

public final class NamespaceSerializers {

    public static class NamespaceSerializer extends StdSerializer<NamespaceID> {
        public NamespaceSerializer() {
            this(null);
        }

        public NamespaceSerializer(Class<NamespaceID> t) {
            super(t);
        }

        @Override public void serialize(NamespaceID value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(value.asString());
        }
    }

    public static class NamespaceDeserializer extends StdDeserializer<NamespaceID> {
        public NamespaceDeserializer() {
            this(null);
        }

        public NamespaceDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override public NamespaceID deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();
            String full = codec.readValue(jp, String.class);
            return NamespaceID.from(full);
        }
    }

    public static class NamespaceKeyDeserializer extends KeyDeserializer {
        @Override public Object deserializeKey(String s, DeserializationContext ctxt) {
            return NamespaceID.from(s);
        }
    }

}
