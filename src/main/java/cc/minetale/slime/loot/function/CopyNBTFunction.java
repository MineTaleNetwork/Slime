package cc.minetale.slime.loot.function;

import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.loot.predicate.LootPredicate;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

@Getter @Setter
public class CopyNBTFunction extends LootFunction {
    private Source source;
    private final List<Operation> operations;

    @JsonCreator
    protected CopyNBTFunction(Source source, List<Operation> operations, List<LootPredicate> conditions) {
        super(FunctionType.COPY_NAME, conditions);
        this.source = source;

        this.operations = Collections.synchronizedList(
                new ArrayList<>(Objects.requireNonNullElse(operations, Collections.emptyList())));
    }

    @Override public @Nullable List<ItemStack> apply(LootContext ctx, List<ItemStack> loot) {
        //TODO This will take a bit...?
        return loot;
    }

    @Getter @Setter @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Source {
        private Type type;
        private @Nullable Target target;
        private @Nullable NamespaceID source;

        public static Source ofContext(Target target) {
            return new Source(Type.CONTEXT, target, null);
        }

        public static Source ofStorage(NamespaceID source) {
            return new Source(Type.CONTEXT, null, source);
        }

        private enum Type {
            CONTEXT,
            STORAGE;

            @JsonValue
            private final String id = name().toLowerCase(Locale.ROOT);

            public String asId() {
                return this.id;
            }

            @JsonCreator
            public static Target fromId(String id) {
                return Arrays.stream(Target.values())
                        .filter(type -> Objects.equals(type.id, id))
                        .findFirst()
                        .orElse(null);
            }
        }

        private enum Target {
            BLOCK_ENTITY,
            THIS,
            KILLER,
            KILLER_PLAYER;

            @JsonValue
            private final String id = name().toLowerCase(Locale.ROOT);

            public String asId() {
                return this.id;
            }

            @JsonCreator
            public static Target fromId(String id) {
                return Arrays.stream(Target.values())
                        .filter(type -> Objects.equals(type.id, id))
                        .findFirst()
                        .orElse(null);
            }
        }
    }

    @Getter @Setter @AllArgsConstructor
    public static class Operation {
        private String source;
        private String target;
        @JsonProperty("op") private Type type;

        public enum Type {
            REPLACE,
            APPEND,
            MERGE;

            @JsonValue
            private final String id = name().toLowerCase(Locale.ROOT);

            public String asId() {
                return this.id;
            }

            @JsonCreator
            public static Source.Target fromId(String id) {
                return Arrays.stream(Source.Target.values())
                        .filter(type -> Objects.equals(type.id, id))
                        .findFirst()
                        .orElse(null);
            }
        }
    }

    public static class Serializers {

        public static class CopyNBTFunctionSerializer extends StdSerializer<CopyNBTFunction> {
            public CopyNBTFunctionSerializer() {
                this(null);
            }

            public CopyNBTFunctionSerializer(Class<CopyNBTFunction> t) {
                super(t);
            }

            @Override public void serialize(CopyNBTFunction value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeStartObject();

                final var source = value.getSource();

                final var type = source.getType();
                switch(type) {
                    case CONTEXT -> jgen.writeObjectField("source", source.getTarget());
                    case STORAGE -> jgen.writeObjectField("source", source.getSource());
                    default -> {}
                }

                jgen.writeObjectField("ops", value.getOperations());
                jgen.writeObjectField("conditions", value.getConditions());
                jgen.writeEndObject();
            }
        }

        public static class CopyNBTFunctionDeserializer extends StdDeserializer<CopyNBTFunction> {
            private static final TypeReference<List<Operation>> OPERATIONS_TYPE = new TypeReference<>() {};
            private static final TypeReference<List<LootPredicate>> CONDITIONS_TYPE = new TypeReference<>() {};

            public CopyNBTFunctionDeserializer() {
                this(null);
            }

            public CopyNBTFunctionDeserializer(Class<?> vc) {
                super(vc);
            }

            @Override public CopyNBTFunction deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                final var codec = jp.getCodec();

                final var node = codec.readTree(jp);

                final var operationsNode = node.get("ops");
                final var operations = codec.readValue(codec.treeAsTokens(operationsNode), OPERATIONS_TYPE);

                final var conditionsNode = node.get("conditions");
                List<LootPredicate> conditions;
                if(conditionsNode != null) {
                    conditions = codec.readValue(codec.treeAsTokens(conditionsNode), CONDITIONS_TYPE);
                } else {
                    conditions = null;
                }

                final var sourceNode = node.get("source");

                if(sourceNode.isValueNode()) {
                    final var target = codec.treeToValue(sourceNode, Source.Target.class);
                    final var source = Source.ofContext(target);

                    return new CopyNBTFunction(source, operations, conditions);
                }

                final CopyNBTFunction.Source source;
                final var type = codec.treeToValue(sourceNode.get("type"), Source.Type.class);
                switch(type) {
                    case CONTEXT -> {
                        final var target = codec.treeToValue(sourceNode.get("target"), Source.Target.class);
                        source = Source.ofContext(target);
                    }
                    case STORAGE -> {
                        final var src = codec.treeToValue(sourceNode.get("source"), NamespaceID.class);
                        source = Source.ofStorage(src);
                    }
                    default -> {
                        return null;
                    }
                }

                return new CopyNBTFunction(source, operations, conditions);
            }
        }

    }
}
