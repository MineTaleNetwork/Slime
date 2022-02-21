package cc.minetale.slime.loot.serializer;

import cc.minetale.slime.loot.predicate.LootPredicate;
import cc.minetale.slime.loot.predicate.PredicateType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class LootPredicateSerializers {

    public static class Deserializer extends StdDeserializer<LootPredicate> {
        public Deserializer() {
            this(null);
        }

        public Deserializer(Class<?> vc) {
            super(vc);
        }

        @Override public LootPredicate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();
            final var node = codec.readTree(jp);

            var type = codec.treeToValue(node.get("condition"), PredicateType.class);
            if(type == null || type.getPredicateClass() == null) { return null; }

            return codec.treeToValue(node, type.getPredicateClass());
        }
    }

}
