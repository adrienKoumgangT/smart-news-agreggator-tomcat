package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.CacheSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.CacheConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

public class CacheHandlerRedis<V> implements CacheHandlerInterface<String, V> {

    private final JedisPool jedisPool;
    private final CacheSerializer serializer;

    public CacheHandlerRedis(String host, int port, CacheSerializer serializer) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[CACHE] [HANDLER REDIS] [CONSTRUCTOR] host: " + host
                        + " --- port : " + port
                        + " --- Serializer : " + serializer.toString()
        );

        JedisPoolConfig poolConfig = buildPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, host, port);
        this.serializer = serializer;

        timePrinter.log();
    }

    private JedisPoolConfig buildPoolConfig() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(cacheConfiguration.getCacheHandlerRedisMaxTotal());
        poolConfig.setMaxIdle(cacheConfiguration.getCacheHandlerRedisMaxIdle());
        poolConfig.setMinIdle(cacheConfiguration.getCacheHandlerRedisMinIdle());
        poolConfig.setTestOnBorrow(cacheConfiguration.isCacheHandlerRedisTestOnBorrow());
        poolConfig.setTestOnReturn(cacheConfiguration.isCacheHandlerRedisTestOnReturn());
        poolConfig.setTestWhileIdle(cacheConfiguration.isCacheHandlerRedisTestWhileIdle());
        // poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        // poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(cacheConfiguration.getCacheHandlerRedisNumTestPerEvictionRun());
        poolConfig.setBlockWhenExhausted(cacheConfiguration.isCacheHandlerRedisBlockWhenExhausted());
        return poolConfig;
    }

    @Override
    public V get(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS] [GET] key: " + key);

        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> data = jedis.hgetAll(key);
            if (data == null || data.isEmpty()){timePrinter.log(); return null;}

            String typeName = data.get("type");
            String valueStr = data.get("value");

            try {
                Class<?> type = Class.forName(typeName);
                timePrinter.log();
                return (V) serializer.deserialize(valueStr, type);
            } catch (Exception e) {
                // throw new RuntimeException("Redis deserialization failed for key: " + key, e);
                timePrinter.error("Redis deserialization failed for key: " + key + " --- exception: " + e.getMessage());
                return null;
            }
        }
    }

    @Override
    public void set(String key, V value) {
        set(key, value, -1);
    }

    @Override
    public void set(String key, V value, long ttlInSeconds) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS] [SET] key: " + key);

        try (Jedis jedis = jedisPool.getResource()) {
            String valueStr = serializer.serialize(value);
            String typeName = value.getClass().getName();

            jedis.hset(key, Map.of("type", typeName, "value", valueStr));

            if (ttlInSeconds > 0) {
                jedis.expire(key, (int) ttlInSeconds);
            }
        }

        timePrinter.log();
    }

    @Override
    public void delete(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS] [DELETE] key: " + key);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }

        timePrinter.log();
    }

    @Override
    public boolean contains(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS] [CONTAINS] key: " + key);
        try (Jedis jedis = jedisPool.getResource()) {
            // timePrinter.log();
            return jedis.exists(key);
        }
    }

    public void close() {
        jedisPool.close();
    }
}
