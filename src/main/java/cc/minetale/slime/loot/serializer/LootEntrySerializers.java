package cc.minetale.slime.loot.serializer;

import cc.minetale.slime.loot.entry.EntryType;
import cc.minetale.slime.loot.entry.LootEntry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class LootEntrySerializers {

    public static class LootEntryDeserializer extends StdDeserializer<LootEntry> {
        public LootEntryDeserializer() {
            this(null);
        }

        public LootEntryDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override public LootEntry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            final var codec = jp.getCodec();
            final var node = codec.readTree(jp);

            var type = codec.treeToValue(node.get("type"), EntryType.class);
            if(type == null || type.getEntryClass() == null) { return null; }

            return codec.treeToValue(node, type.getEntryClass());
        }
    }

}
