package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer;

import com.google.gson.Gson;

public class GsonSerializer implements CacheSerializer {

    private final Gson gson = new Gson();

    @Override
    public <T> String serialize(T value) {
        return gson.toJson(value);
    }

    @Override
    public <T> T deserialize(String data, Class<T> type) {
        return gson.fromJson(data, type);
    }
}
