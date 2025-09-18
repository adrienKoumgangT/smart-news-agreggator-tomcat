package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.provider;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.RedisConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.serializer.RedisSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

public class RedisProvider implements RedisProviderInterface<String> {

    private final JedisPool jedisPool;
    private final RedisSerializer serializer;

    public RedisProvider(RedisSerializer serializer) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[REDIS] [PROVIDER] [CONSTRUCTOR] "
                        + "Serializer : " + serializer.toString()
        );

        RedisConfiguration redisConfiguration = new RedisConfiguration();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfiguration.getRedisMaxTotal());
        poolConfig.setMaxIdle(redisConfiguration.getRedisMaxIdle());
        poolConfig.setMinIdle(redisConfiguration.getRedisMinIdle());
        poolConfig.setTestOnBorrow(redisConfiguration.isRedisTestOnBorrow());
        poolConfig.setTestOnReturn(redisConfiguration.isRedisTestOnReturn());
        poolConfig.setTestWhileIdle(redisConfiguration.isRedisTestWhileIdle());
        // poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        // poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(redisConfiguration.getRedisNumTestPerEvictionRun());
        poolConfig.setBlockWhenExhausted(redisConfiguration.isRedisBlockWhenExhausted());

        this.jedisPool = new JedisPool(poolConfig, redisConfiguration.getRedisHost(), redisConfiguration.getRedisPort());
        this.serializer = serializer;

        timePrinter.log();
    }


    public Set<String> keys(String pattern) {
        return keys(pattern, 1000); // default COUNT
    }

    public Set<String> keys(String pattern, int count) {
        Set<String> result = new HashSet<>();
        try (Jedis j = jedisPool.getResource()) {
            String cursor = ScanParams.SCAN_POINTER_START; // "0"
            ScanParams params = new ScanParams().match(pattern).count(count);
            do {
                ScanResult<String> scan = j.scan(cursor, params);
                result.addAll(scan.getResult());
                cursor = scan.getCursor();
            } while (!"0".equals(cursor));
        }
        return result;
    }



    @Override
    public <V> V get(String key, Class<V> type) {
        MineLog.TimePrinter tp = new MineLog.TimePrinter("[CACHE] [REDIS POOL] [GET] key: " + key);
        try (Jedis j = jedisPool.getResource()) {
            String data = j.get(key);
            if (data == null || data.isBlank()) { tp.missing(); return null; }
            try {
                V val = (V) serializer.deserialize(data, type);
                tp.log();
                return val;
            } catch (Exception e) {
                tp.error("Deserialization failed for key: " + key + " --- " + e.getMessage());
                return null;
            }
        }
    }

    @Override
    public <V> void set(String key, V value) {
        set(key, value, -1);
    }

    @Override
    public <V> void set(String key, V value, long ttlInSeconds) {
        MineLog.TimePrinter tp = new MineLog.TimePrinter("[CACHE] [REDIS POOL] [SET] key: " + key);
        try (Jedis j = jedisPool.getResource()) {
            String valueStr = serializer.serialize(value);
            j.set(key, valueStr);
            if (ttlInSeconds > 0) j.expire(key, (int) ttlInSeconds);
            tp.log();
        }
    }

    @Override
    public void delete(String key) {
        MineLog.TimePrinter tp = new MineLog.TimePrinter("[CACHE] [REDIS POOL] [DELETE] key: " + key);
        try (Jedis j = jedisPool.getResource()) {
            j.del(key, metaKey(key));
            tp.log();
        }
    }

    @Override
    public boolean contains(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.exists(key);
        }
    }

    public void close() {
        jedisPool.close();
    }

    /* ---------------- TTL & counters ---------------- */

    public boolean expire(String key, long ttlSeconds) {
        try (Jedis j = jedisPool.getResource()) {
            return j.expire(key, (int) ttlSeconds) == 1;
        }
    }

    public long ttl(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.ttl(key);
        }
    }

    public boolean persist(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.persist(key) == 1;
        }
    }

    public long incrBy(String key, long delta) {
        try (Jedis j = jedisPool.getResource()) {
            return j.incrBy(key, delta);
        }
    }

    /* ---------------- LIST operations ---------------- */

    public <V> long lpush(String key, V... values) {
        try (Jedis j = jedisPool.getResource()) {
            ensureMeta(j, key, "LIST", guessType(values));
            return j.lpush(key, serializeArray(values));
        }
    }

    public <V> long rpush(String key, V... values) {
        try (Jedis j = jedisPool.getResource()) {
            ensureMeta(j, key, "LIST", guessType(values));
            return j.rpush(key, serializeArray(values));
        }
    }

    @SuppressWarnings("unchecked")
    public <V> V lpop(String key) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            String raw = j.lpop(key);
            return raw == null ? null : (V) serializer.deserialize(raw, type);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> V rpop(String key) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            String raw = j.rpop(key);
            return raw == null ? null : (V) serializer.deserialize(raw, type);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> List<V> lrange(String key, long start, long stop) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            List<String> raw = j.lrange(key, start, stop);
            if (raw == null || raw.isEmpty()) return List.of();
            List<V> out = new ArrayList<>(raw.size());
            for (String s : raw) out.add((V) serializer.deserialize(s, type));
            return out;
        }
    }

    public long llen(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.llen(key);
        }
    }

    /* ---------------- SET operations ---------------- */

    public <V> long sadd(String key, V... members) {
        try (Jedis j = jedisPool.getResource()) {
            ensureMeta(j, key, "SET", guessType(members));
            return j.sadd(key, serializeArray(members));
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Set<V> smembers(String key, Class<V> type) {
        try (Jedis j = jedisPool.getResource()) {
            // Class<?> type = resolveElementType(j, key);
            Set<String> raw = j.smembers(key);
            if (raw == null || raw.isEmpty()) return Set.of();
            Set<V> out = new HashSet<>(raw.size());
            for (String s : raw) out.add((V) serializer.deserialize(s, type));
            return out;
        }
    }

    public <V> long srem(String key, V... members) {
        try (Jedis j = jedisPool.getResource()) {
            return j.srem(key, serializeArray(members));
        }
    }

    public <V> boolean sismember(String key, V member) {
        try (Jedis j = jedisPool.getResource()) {
            return j.sismember(key, serializer.serialize(member));
        }
    }

    public long scard(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.scard(key);
        }
    }

    /* ---------------- HASH operations ---------------- */

    public <V> long hset(String key, String field, V value) {
        try (Jedis j = jedisPool.getResource()) {
            ensureMeta(j, key, "HASH", value == null ? Object.class : value.getClass());
            return j.hset(key, field, serializer.serialize(value));
        }
    }

    @SuppressWarnings("unchecked")
    public <V> V hget(String key, String field) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            String raw = j.hget(key, field);
            return raw == null ? null : (V) serializer.deserialize(raw, type);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Map<String, V> hgetAll(String key) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            Map<String,String> raw = j.hgetAll(key);
            if (raw == null || raw.isEmpty()) return Map.of();
            Map<String,V> out = new LinkedHashMap<>(raw.size());
            for (Map.Entry<String,String> e : raw.entrySet()) {
                out.put(e.getKey(), e.getValue() == null ? null : (V) serializer.deserialize(e.getValue(), type));
            }
            return out;
        }
    }

    public long hdel(String key, String... fields) {
        try (Jedis j = jedisPool.getResource()) {
            return j.hdel(key, fields);
        }
    }

    public boolean hexists(String key, String field) {
        try (Jedis j = jedisPool.getResource()) {
            return j.hexists(key, field);
        }
    }

    public Set<String> hkeys(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.hkeys(key);
        }
    }

    public long hlen(String key) {
        try (Jedis j = jedisPool.getResource()) {
            return j.hlen(key);
        }
    }

    /* ---------------- ZSET operations ---------------- */

    public <V> long zadd(String key, double score, V member) {
        try (Jedis j = jedisPool.getResource()) {
            ensureMeta(j, key, "ZSET", member == null ? Object.class : member.getClass());
            return j.zadd(key, score, serializer.serialize(member));
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Set<V> zrange(String key, long start, long stop) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            List<String> raw = j.zrange(key, start, stop); // List in Jedis 5.x
            if (raw == null || raw.isEmpty()) return Collections.emptySet();
            LinkedHashSet<V> out = new LinkedHashSet<>(raw.size());
            for (String s : raw) out.add((V) serializer.deserialize(s, type));
            return out;
        }
    }

    @SuppressWarnings("unchecked")
    public <V> Set<Scored<V>> zrangeWithScores(String key, long start, long stop) {
        try (Jedis j = jedisPool.getResource()) {
            Class<?> type = resolveElementType(j, key);
            List<Tuple> raw = j.zrangeWithScores(key, start, stop); // List in Jedis 5.x
            if (raw == null || raw.isEmpty()) return Collections.emptySet();
            LinkedHashSet<Scored<V>> out = new LinkedHashSet<>(raw.size());
            for (Tuple t : raw) out.add(new Scored<>((V) serializer.deserialize(t.getElement(), type), t.getScore()));
            return out;
        }
    }

    public <V> long zrem(String key, V... members) {
        try (Jedis j = jedisPool.getResource()) {
            return j.zrem(key, serializeArray(members));
        }
    }

    public <V> Long zrank(String key, V member) {
        try (Jedis j = jedisPool.getResource()) {
            return j.zrank(key, serializer.serialize(member));
        }
    }

    public <V> Double zscore(String key, V member) {
        try (Jedis j = jedisPool.getResource()) {
            return j.zscore(key, serializer.serialize(member));
        }
    }

    /* ---------------- Internal helpers ---------------- */

    private String metaKey(String key) { return key + ":meta"; }

    private void ensureMeta(Jedis j, String key, String structure, Class<?> elementType) {
        Map<String,String> meta = new HashMap<>(2);
        meta.put("structure", structure);
        meta.put("elementType", elementType.getName());
        j.hset(metaKey(key), meta);
    }

    private Class<?> resolveElementType(Jedis j, String key) {
        String tn = j.hget(metaKey(key), "elementType");
        if (tn == null) return Object.class;
        try { return Class.forName(tn); }
        catch (ClassNotFoundException e) { return Object.class; }
    }

    private <V> String[] serializeArray(V[] values) {
        if (values == null || values.length == 0) return new String[0];
        String[] out = new String[values.length];
        for (int i = 0; i < values.length; i++) out[i] = serializer.serialize(values[i]);
        return out;
    }

    private <V> Class<?> guessType(V[] values) {
        if (values == null || values.length == 0 || values[0] == null) return Object.class;
        return values[0].getClass();
    }

}
