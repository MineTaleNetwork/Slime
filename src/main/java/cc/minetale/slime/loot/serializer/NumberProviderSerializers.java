package cc.minetale.slime.loot.serializer;

import cc.minetale.slime.loot.util.NumberProvider;
import cc.minetale.slime.loot.util.NumberProviderType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class NumberProviderSerializers {

    public static class Modifier extends BeanSerializerModifier {
        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
            if (NumberProvider.class.isAssignableFrom(desc.getBeanClass())) {
                return new NumberProviderSerializer((JsonSerializer<NumberProvider>) serializer);
            }
            return serializer;
        }
    }

    public static class NumberProviderSerializer extends StdSerializer<NumberProvider> {
        private JsonSerializer<NumberProvider> defaultSerializer;

        public NumberProviderSerializer() {
            this((Class<NumberProvider>) null);
        }

        public NumberProviderSerializer(Class<NumberProvider> t) {
            super(t);
            defaultSerializer = null;
        }

        public NumberProviderSerializer(JsonSerializer<NumberProvider> defaultSerializer) {
            this();
            this.defaultSerializer = defaultSerializer;
        }

        @Override public void serialize(NumberProvider value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if(value.getType() == null) {
                jgen.writeNumber(value.get());
                return;
            }

            defaultSerializer.serialize(value, jgen, provider);
        }
    }

    public static class Deserializer extends StdDeserializer<NumberProvider> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override public NumberProvider deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();

            if(jp.getCurrentToken() != JsonToken.START_OBJECT) {
                return new NumberProvider.Constant(jp.readValueAs(float.class), false);
            }

            final var node = codec.readTree(jp);

            var type = codec.treeToValue(node.get("type"), NumberProviderType.class);
            if(type == null || type.getProviderClass() == null) { return null; }

            return codec.treeToValue(node, type.getProviderClass());
        }
    }

}
