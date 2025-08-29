package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.factory;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.CacheSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.GsonSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.JacksonSerializer;

public class SerializerFactory {

    public static CacheSerializer get(String type) {
        return switch (type.toLowerCase()) {
            case "gson" -> new GsonSerializer();
            case "jackson" -> new JacksonSerializer();
            default -> throw new IllegalArgumentException("Unknown serializer: " + type);
        };
    }

}
