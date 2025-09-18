package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.provider;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.RedisConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.serializer.RedisSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import redis.clients.jedis.*;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Tuple;

import java.util.*;

public class RedisClusterProvider implements RedisProviderInterface<String> {


    private final JedisCluster jedisCluster;
    private final RedisSerializer serializer;

    public RedisClusterProvider(RedisSerializer serializer) throws Exception {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[REDIS CLUSTER] [PROVIDER] [CONSTRUCTOR] "
                        + "Serializer : " + serializer.toString()
        );

        Set<HostAndPort> clusterNodes = getHostAndPortSet();
        System.out.println("[REDIS] [CLUSTER NODES: " + clusterNodes);

        this.jedisCluster = new JedisCluster(clusterNodes);
        this.serializer = serializer;

        timePrinter.log();
    }

    private static Set<HostAndPort> getHostAndPortSet() throws Exception {
        RedisConfiguration redisConfiguration = new RedisConfiguration();

        String hostAndPortList = redisConfiguration.getRedisClusterHost();
        String[] hostPorts = hostAndPortList.split(",");
        Set<HostAndPort> clusterNodes = new HashSet<>();
        for (String hostPort : hostPorts) {
            String[] hostAndPortArray = hostPort.split(":");
            clusterNodes.add(new HostAndPort(hostAndPortArray[0], Integer.parseInt(hostAndPortArray[1])));
        }

        return clusterNodes;
    }



    public Set<String> keys(String pattern) {
        return keys(pattern, 1000); // default COUNT per node
    }

    public Set<String> keys(String pattern, int count) {
        Set<String> keys = new HashSet<>();

        for (String node : jedisCluster.getClusterNodes().keySet()) {
            HostAndPort hap = parseHostAndPort(node);
            try (Jedis j = new Jedis(hap)) {
                String cursor = ScanParams.SCAN_POINTER_START;
                ScanParams params = new ScanParams().match(pattern).count(count);
                do {
                    ScanResult<String> res = j.scan(cursor, params);
                    keys.addAll(res.getResult());
                    cursor = res.getCursor();
                } while (!"0".equals(cursor));
            } catch (Exception e) {
                // optional: log and continue
                // System.err.println("SCAN failed on node " + node + ": " + e.getMessage());
            }
        }
        return keys;
    }

    /** Helper to parse host:port from cluster node key */
    private static HostAndPort parseHostAndPort(String nodeKey) {
        // nodeKey examples:
        //  - "10.1.1.17:6379"
        //  - "10.1.1.17:6379@16379" (client:busPort format)
        String hostPort = nodeKey;
        int atIdx = nodeKey.indexOf('@');
        if (atIdx > 0) hostPort = nodeKey.substring(0, atIdx);
        int colonIdx = hostPort.lastIndexOf(':');
        String host = hostPort.substring(0, colonIdx);
        int port = Integer.parseInt(hostPort.substring(colonIdx + 1));
        return new HostAndPort(host, port);
    }


    // =========================
    // Basic KV
    // =========================

    public <V> V get(String key, Class<V> type) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [GET] key: " + key);

        String data = jedisCluster.get(key);
        if (data == null || data.isBlank()){ timePrinter.missing(); return null; }

        try {
            timePrinter.log();
            return serializer.deserialize(data, type);
        } catch (Exception e) {
            timePrinter.error("Redis Cluster deserialization failed for key: " + key + " --- exception: " + e.getMessage());
            return null;
        }
    }


    public <V> void set(String key, V value) {
        set(key, value, -1);
    }


    public <V> void set(String key, V value, long ttlInSeconds) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [SET] key: " + key);

        String valueStr = serializer.serialize(value);
        String typeName = value.getClass().getName();

        jedisCluster.hset(key, Map.of("type", typeName, "value", valueStr));
        if (ttlInSeconds > 0) {
            jedisCluster.expire(key, (int) ttlInSeconds);
        }
        timePrinter.log();
    }


    public void delete(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [DELETE] key: " + key);
        jedisCluster.del(key, metaKey(key)); // also delete meta
        timePrinter.log();
    }


    public boolean contains(String key) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[CACHE] [HANDLER REDIS CLUSTER] [CONTAINS] key: " + key);
        return jedisCluster.exists(key);
    }

    public void close() {
        try {
            jedisCluster.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // TTL / utility helpers
    // =========================

    public boolean expire(String key, long ttlSeconds) {
        return jedisCluster.expire(key, (int) ttlSeconds) == 1;
    }

    public long ttl(String key) {
        return jedisCluster.ttl(key);
    }

    public boolean persist(String key) {
        return jedisCluster.persist(key) == 1;
    }

    public long incrBy(String key, long delta) {
        // store as numeric string; useful for counters
        return jedisCluster.incrBy(key, delta);
    }

    // ==================================================
    // LIST operations (store serialized elements as list)
    // ==================================================

    public <V> long lpush(String key, V... values) {
        ensureMeta(key, "LIST", guessType(values));
        String[] payload = serializeArray(values);
        return jedisCluster.lpush(key, payload);
    }

    public <V> long rpush(String key, V... values) {
        ensureMeta(key, "LIST", guessType(values));
        String[] payload = serializeArray(values);
        return jedisCluster.rpush(key, payload);
    }

    @SuppressWarnings("unchecked")
    public <V> V lpop(String key) {
        Class<?> type = resolveElementType(key);
        String raw = jedisCluster.lpop(key);
        return raw == null ? null : (V) serializer.deserialize(raw, type);
    }

    @SuppressWarnings("unchecked")
    public <V> V rpop(String key) {
        Class<?> type = resolveElementType(key);
        String raw = jedisCluster.rpop(key);
        return raw == null ? null : (V) serializer.deserialize(raw, type);
    }

    @SuppressWarnings("unchecked")
    public <V> List<V> lrange(String key, long start, long stop) {
        Class<?> type = resolveElementType(key);
        List<String> raw = jedisCluster.lrange(key, start, stop);
        if (raw == null) return List.of();
        List<V> out = new ArrayList<>(raw.size());
        for (String s : raw) out.add((V) serializer.deserialize(s, type));
        return out;
    }

    public long llen(String key) {
        return jedisCluster.llen(key);
    }

    // ===============================================
    // SET operations (distinct members, no ordering)
    // ===============================================

    public <V> long sadd(String key, V... members) {
        ensureMeta(key, "SET", guessType(members));
        String[] payload = serializeArray(members);
        return jedisCluster.sadd(key, payload);
    }

    @SuppressWarnings("unchecked")
    public <V> Set<V> smembers(String key, Class<V> type) {
        // Class<?> type = resolveElementType(key);
        Set<String> raw = jedisCluster.smembers(key);
        if (raw == null || raw.isEmpty()) return Set.of();
        Set<V> out = new HashSet<>(raw.size());
        for (String s : raw) out.add((V) serializer.deserialize(s, type));
        return out;
    }

    public <V> long srem(String key, V... members) {
        String[] payload = serializeArray(members);
        return jedisCluster.srem(key, payload); // no cast needed
    }

    public <V> boolean sismember(String key, V member) {
        return jedisCluster.sismember(key, serializer.serialize(member));
    }

    public long scard(String key) {
        return jedisCluster.scard(key);
    }

    // ===============================================
    // HASH operations (field -> serialized V)
    // ===============================================

    public <V> long hset(String key, String field, V value) {
        ensureMeta(key, "HASH", value == null ? Object.class : value.getClass());
        return jedisCluster.hset(key, field, serializer.serialize(value));
    }

    @SuppressWarnings("unchecked")
    public <V> V hget(String key, String field) {
        Class<?> type = resolveElementType(key);
        String raw = jedisCluster.hget(key, field);
        return raw == null ? null : (V) serializer.deserialize(raw, type);
    }

    @SuppressWarnings("unchecked")
    public <V> Map<String, V> hgetAll(String key) {
        Class<?> type = resolveElementType(key);
        Map<String, String> raw = jedisCluster.hgetAll(key);
        if (raw == null || raw.isEmpty()) return Map.of();
        Map<String, V> out = new LinkedHashMap<>(raw.size());
        for (Map.Entry<String, String> e : raw.entrySet()) {
            out.put(e.getKey(), e.getValue() == null ? null : (V) serializer.deserialize(e.getValue(), type));
        }
        return out;
    }

    public long hdel(String key, String... fields) {
        return jedisCluster.hdel(key, fields);
    }

    public boolean hexists(String key, String field) {
        return jedisCluster.hexists(key, field);
    }

    public Set<String> hkeys(String key) {
        return jedisCluster.hkeys(key);
    }

    public long hlen(String key) {
        return jedisCluster.hlen(key);
    }

    // ======================================================
    // ZSET operations (sorted set: member -> score)
    // ======================================================

    public <V> long zadd(String key, double score, V member) {
        ensureMeta(key, "ZSET", member == null ? Object.class : member.getClass());
        return jedisCluster.zadd(key, score, serializer.serialize(member));
    }

    @SuppressWarnings("unchecked")
    public <V> Set<V> zrange(String key, long start, long stop) {
        Class<?> type = resolveElementType(key);
        List<String> raw = jedisCluster.zrange(key, start, stop); // List, not Set
        if (raw == null || raw.isEmpty()) return Collections.emptySet();

        LinkedHashSet<V> out = new LinkedHashSet<>(raw.size());
        for (String s : raw) {
            out.add((V) serializer.deserialize(s, type));
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public <V> Set<Scored<V>> zrangeWithScores(String key, long start, long stop) {
        Class<?> type = resolveElementType(key);
        List<Tuple> raw = jedisCluster.zrangeWithScores(key, start, stop); // List, not Set
        if (raw == null || raw.isEmpty()) return Collections.emptySet();

        LinkedHashSet<Scored<V>> out = new LinkedHashSet<>(raw.size());
        for (Tuple t : raw) {
            out.add(new Scored<>((V) serializer.deserialize(t.getElement(), type), t.getScore()));
        }
        return out;
    }

    public <V> long zrem(String key, V... members) {
        String[] payload = serializeArray(members);
        return jedisCluster.zrem(key, payload);
    }

    public <V> Long zrank(String key, V member) {
        return jedisCluster.zrank(key, serializer.serialize(member));
    }

    public <V> Double zscore(String key, V member) {
        return jedisCluster.zscore(key, serializer.serialize(member));
    }

    // =========================
    // Internal helpers
    // =========================

    private String metaKey(String key) { return key + ":meta"; }

    private void ensureMeta(String key, String structure, Class<?> elementType) {
        String mKey = metaKey(key);
        // set once; harmless if overwritten with same values
        Map<String, String> meta = new HashMap<>(2);
        meta.put("structure", structure);
        meta.put("elementType", elementType.getName());
        jedisCluster.hset(mKey, meta);
    }

    private Class<?> resolveElementType(String key) {
        String mKey = metaKey(key);
        String tn = jedisCluster.hget(mKey, "elementType");
        if (tn == null) {
            // fallback to Object to avoid NPE; callers should populate meta before use
            return Object.class;
        }
        try {
            return Class.forName(tn);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

    private <V> String[] serializeArray(V[] values) {
        if (values == null || values.length == 0) return new String[0];
        String[] out = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = serializer.serialize(values[i]);
        }
        return out;
    }

    private <V> Class<?> guessType(V[] values) {
        if (values == null || values.length == 0 || values[0] == null) return Object.class;
        return values[0].getClass();
    }


}
