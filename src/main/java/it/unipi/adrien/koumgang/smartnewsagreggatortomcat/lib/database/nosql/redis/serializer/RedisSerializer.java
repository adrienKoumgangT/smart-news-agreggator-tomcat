package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.serializer;

import com.google.gson.Gson;

public class RedisSerializer {


    private final Gson gson = new Gson();

    public <T> String serialize(T value) {
        return gson.toJson(value);
    }

    public <T> T deserialize(String data, Class<T> type) {
        return gson.fromJson(data, type);
    }

}
