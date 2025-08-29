package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer;

public interface CacheSerializer {

    <T> String serialize(T value);

    <T> T deserialize(String data, Class<T> type);

}
