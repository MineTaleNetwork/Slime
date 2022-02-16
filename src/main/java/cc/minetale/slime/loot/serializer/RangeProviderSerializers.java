package cc.minetale.slime.loot.serializer;

import cc.minetale.slime.loot.util.DoubleRangeProvider;
import cc.minetale.slime.loot.util.FloatRangeProvider;
import cc.minetale.slime.loot.util.IntegerRangeProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public final class RangeProviderSerializers {

    public static class IntegerRange {

        public static class Serializer extends StdSerializer<IntegerRangeProvider> {
            public Serializer() {
                this(null);
            }

            public Serializer(Class<IntegerRangeProvider> t) {
                super(t);
            }

            @Override public void serialize(IntegerRangeProvider value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if(value.requiresExact()) {
                    jgen.writeNumber(value.getMax());
                    return;
                }

                jgen.writeStartObject();

                final var min = value.getMin();
                if(min != Integer.MIN_VALUE)
                    jgen.writeNumberField("min", min);

                final var max = value.getMax();
                if(max != Integer.MAX_VALUE)
                    jgen.writeNumberField("max", max);

                jgen.writeEndObject();
            }
        }

        public static class Deserializer extends StdDeserializer<IntegerRangeProvider> {
            public Deserializer() {
                this(null);
            }

            public Deserializer(Class<?> vc) {
                super(vc);
            }

            @Override public IntegerRangeProvider deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                final var codec = jp.getCodec();

                if(jp.getCurrentToken() != JsonToken.START_OBJECT) {
                    return new IntegerRangeProvider(codec.readValue(jp, int.class));
                }

                final var node = codec.readTree(jp);

                final var minNode = node.get("min");
                final var maxNode = node.get("max");

                final int min;
                if(minNode != null) {
                    min = codec.treeToValue(minNode, int.class);
                } else {
                    min = Integer.MIN_VALUE;
                }

                final int max;
                if(maxNode != null) {
                    max = codec.treeToValue(maxNode, int.class);
                } else {
                    max = Integer.MAX_VALUE;
                }

                return new IntegerRangeProvider(min, max);
            }
        }

    }

    public static class FloatRange {

        public static class Serializer extends StdSerializer<FloatRangeProvider> {
            public Serializer() {
                this(null);
            }

            public Serializer(Class<FloatRangeProvider> t) {
                super(t);
            }

            @Override public void serialize(FloatRangeProvider value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if(value.requiresExact()) {
                    jgen.writeNumber(value.getMax());
                    return;
                }

                jgen.writeStartObject();

                final var min = value.getMin();
                if(min != Float.MIN_VALUE)
                    jgen.writeNumberField("min", min);

                final var max = value.getMax();
                if(max != Float.MAX_VALUE)
                    jgen.writeNumberField("max", max);

                jgen.writeEndObject();
            }
        }

        public static class Deserializer extends StdDeserializer<FloatRangeProvider> {
            public Deserializer() {
                this(null);
            }

            public Deserializer(Class<?> vc) {
                super(vc);
            }

            @Override public FloatRangeProvider deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                final var codec = jp.getCodec();

                if(jp.getCurrentToken() != JsonToken.START_OBJECT) {
                    return new FloatRangeProvider(codec.readValue(jp, float.class));
                }

                final var node = codec.readTree(jp);

                final var minNode = node.get("min");
                final var maxNode = node.get("max");

                final float min;
                if(minNode != null) {
                    min = codec.treeToValue(minNode, int.class);
                } else {
                    min = Float.MIN_VALUE;
                }

                final float max;
                if(maxNode != null) {
                    max = codec.treeToValue(maxNode, int.class);
                } else {
                    max = Float.MAX_VALUE;
                }

                return new FloatRangeProvider(min, max);
            }
        }

    }

    public static class DoubleRange {

        public static class Serializer extends StdSerializer<DoubleRangeProvider> {
            public Serializer() {
                this(null);
            }

            public Serializer(Class<DoubleRangeProvider> t) {
                super(t);
            }

            @Override public void serialize(DoubleRangeProvider value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if(value.requiresExact()) {
                    jgen.writeNumber(value.getMax());
                    return;
                }

                jgen.writeStartObject();

                final var min = value.getMin();
                if(min != Double.MIN_VALUE)
                    jgen.writeNumberField("min", min);

                final var max = value.getMax();
                if(max != Double.MAX_VALUE)
                    jgen.writeNumberField("max", max);

                jgen.writeEndObject();
            }
        }

        public static class Deserializer extends StdDeserializer<DoubleRangeProvider> {
            public Deserializer() {
                this(null);
            }

            public Deserializer(Class<?> vc) {
                super(vc);
            }

            @Override public DoubleRangeProvider deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                final var codec = jp.getCodec();

                if(jp.getCurrentToken() != JsonToken.START_OBJECT) {
                    return new DoubleRangeProvider(codec.readValue(jp, double.class));
                }

                final var node = codec.readTree(jp);

                final var minNode = node.get("min");
                final var maxNode = node.get("max");

                final double min;
                if(minNode != null) {
                    min = codec.treeToValue(minNode, double.class);
                } else {
                    min = Double.MIN_VALUE;
                }

                final double max;
                if(maxNode != null) {
                    max = codec.treeToValue(maxNode, double.class);
                } else {
                    max = Double.MAX_VALUE;
                }

                return new DoubleRangeProvider(min, max);
            }
        }

    }
}
