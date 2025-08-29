package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.CacheSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.Map;
import java.util.Set;

public class CacheHandlerRedisCluster<V> implements CacheHandlerInterface<String, V> {

    private final JedisCluster jedisCluster;
    private final CacheSerializer serializer;

    public CacheHandlerRedisCluster(Set<HostAndPort> clusterNodes, CacheSerializer serializer) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[CACHE] [HANDLER REDIS CLUSTER] [CONSTRUCTOR] cluster nodes: " + clusterNodes.toString()
                        + " --- Serializer : " + serializer.toString()
        );

        this.jedisCluster = new JedisCluster(clusterNodes);
        this.serializer = serializer;

        timePrinter.log();
    }

    @Override
    public V get(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [GET] key: " + key);

        Map<String, String> data = jedisCluster.hgetAll(key);
        if (data == null || data.isEmpty()){timePrinter.log(); return null;}

        String typeName = data.get("type");
        String valueStr = data.get("value");

        try {
            Class<?> type = Class.forName(typeName);
            timePrinter.log();
            return (V) serializer.deserialize(valueStr, type);
        } catch (Exception e) {
            // throw new RuntimeException("Redis Cluster deserialization failed for key: " + key, e);
            timePrinter.error("Redis Cluster deserialization failed for key: " + key + " --- exception: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void set(String key, V value) {
        set(key, value, -1);
    }

    @Override
    public void set(String key, V value, long ttlInSeconds) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [SET] key: " + key);

        String valueStr = serializer.serialize(value);
        String typeName = value.getClass().getName();

        jedisCluster.hset(key, Map.of("type", typeName, "value", valueStr));

        if (ttlInSeconds > 0) {
            jedisCluster.expire(key, (int) ttlInSeconds);
        }

        timePrinter.log();
    }

    @Override
    public void delete(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [DELETE] key: " + key);

        jedisCluster.del(key);

        timePrinter.log();
    }

    @Override
    public boolean contains(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [CONTAINS] key: " + key);
        // timePrinter.log();
        return jedisCluster.exists(key);
    }

    public void close() {
        try {
            jedisCluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
