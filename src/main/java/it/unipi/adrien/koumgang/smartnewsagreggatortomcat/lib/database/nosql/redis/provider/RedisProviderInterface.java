package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.redis.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generic cache handler interface for basic key-value operations.
 */
public interface RedisProviderInterface<K> {

    Set<String> keys(String pattern);

    Set<String> keys(String pattern, int count);

    //* ---------------- Basic KV ---------------- */

    <V> V get(K key, Class<V> type);

    <V> void set(K key, V value);

    <V> void set(K key, V value, long ttlInSeconds);

    void delete(K key);

    boolean contains(K key);

    /* ---------------- TTL / Counters ---------------- */

    boolean expire(K key, long ttlSeconds);

    long ttl(K key);

    boolean persist(K key);

    long incrBy(K key, long delta);

    /* ---------------- LIST operations ---------------- */

    /**
     * LPUSH adds a new element to the head of a list
     * */
    <V> long lpush(K key, V... values);

    /**
     * RPUSH adds to the tail
     * */
    <V> long rpush(K key, V... values);

    /**
     * LPOP removes and returns an element from the head of a list
     * */
    <V> V lpop(K key);

    /**
     * RPOP removes and returns an element from the tails of a list
     * */
    <V> V rpop(K key);

    /**
     * LRANGE extracts a range of elements from a list
     * */
    <V> List<V> lrange(K key, long start, long stop);

    /**
     * LLEN returns the length of a lis
     * */
    long llen(K key);

    /* ---------------- SET operations ---------------- */

    <V> long sadd(K key, V... members);

    <V> Set<V> smembers(K key, Class<V> type);

    <V> long srem(K key, V... members);

    <V> boolean sismember(K key, V member);

    long scard(K key);

    /* ---------------- HASH operations ---------------- */

    <V> long hset(K key, String field, V value);

    <V> V hget(K key, String field);

    <V> Map<String, V> hgetAll(K key);

    long hdel(K key, String... fields);

    boolean hexists(K key, String field);

    Set<String> hkeys(K key);

    long hlen(K key);

    /* ---------------- ZSET operations ---------------- */

    <V> long zadd(K key, double score, V member);

    <V> Set<V> zrange(K key, long start, long stop);

    <V> Set<Scored<V>> zrangeWithScores(K key, long start, long stop);

    <V> long zrem(K key, V... members);

    <V> Long zrank(K key, V member);

    <V> Double zscore(K key, V member);

    /* ---------------- Lifecycle ---------------- */

    void close();

    /* ---------------- Helper type ---------------- */

    /**
     * Wrapper for sorted-set members with their score.
     */
    final class Scored<T> {
        public final T member;
        public final double score;
        public Scored(T member, double score) {
            this.member = member;
            this.score = score;
        }
        @Override public String toString() {
            return "Scored{member=" + member + ", score=" + score + '}';
        }
    }


}
