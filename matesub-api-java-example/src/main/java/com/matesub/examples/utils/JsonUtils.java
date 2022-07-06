package com.matesub.examples.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Json utility class to retrieve an instance of the {@link ObjectMapper} configured to deserialize the response from the API.
 */
public final class JsonUtils {

    private JsonUtils() {
    }

    /**
     * Retrieve an instance of {@link ObjectMapper}.
     *
     * @return the object mapper configured with the needed serializer and deserializer to handle {@link Instant}.
     */
    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();

        module.addDeserializer( Instant.class, new JsonDeserializer<Instant>() {
            @Override
            public Instant deserialize( JsonParser jsonParser, DeserializationContext deserializationContext ) throws IOException, JacksonException {
                Instant parsed;
                try {
                    InstantContextVector vector = jsonParser.readValueAs( InstantContextVector.class );
                    parsed = Instant.ofEpochSecond( vector.seconds(), vector.nanos() );
                } catch ( Exception e ) {
                    try {
                        parsed = Instant.parse( jsonParser.getText() );
                    } catch ( Exception ex ) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
                        parsed = ZonedDateTime.parse( jsonParser.getText(), formatter ).toInstant();
                    }
                }
                return parsed;
            }

        } );

        module.addSerializer( Instant.class, new JsonSerializer<Instant>() {
            @Override
            public void serialize( Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider ) throws IOException {
                jsonGenerator.writeObject( new InstantContextVector( instant.getEpochSecond(), instant.getNano() ) );
            }
        } );

        objectMapper.registerModule( module );

        return objectMapper;
    }

    /**
     * Representation of the {@link Instant} as defined by the API.
     *
     * @param seconds
     * @param nanos
     */
    private record InstantContextVector(long seconds, int nanos) {
    }
}
