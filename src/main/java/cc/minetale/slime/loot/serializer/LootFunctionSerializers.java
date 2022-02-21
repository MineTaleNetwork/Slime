package cc.minetale.slime.loot.serializer;

import cc.minetale.slime.loot.function.FunctionType;
import cc.minetale.slime.loot.function.LootFunction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class LootFunctionSerializers {

    public static class Deserializer extends StdDeserializer<LootFunction> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override public LootFunction deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();
            final var node = codec.readTree(jp);

            var type = codec.treeToValue(node.get("function"), FunctionType.class);
            if(type == null || type.getFunctionClass() == null) { return null; }

            return codec.treeToValue(node, type.getFunctionClass());
        }
    }

}
