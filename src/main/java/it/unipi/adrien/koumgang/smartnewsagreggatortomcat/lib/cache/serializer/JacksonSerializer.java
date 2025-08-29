package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer implements CacheSerializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> String serialize(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Jackson serialization failed", e);
        }
    }

    @Override
    public <T> T deserialize(String data, Class<T> type) {
        try {
            return objectMapper.readValue(data, type);
        } catch (Exception e) {
            throw new RuntimeException("Jackson deserialization failed", e);
        }
    }
}