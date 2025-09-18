package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/redis.properties")
public class RedisConfiguration extends BaseConfiguration {

    @ConfigValue(key = "redis.type", defaultValue = "sentinel")
    private String redisType;




    @ConfigValue(key = "redis.host", defaultValue = "localhost")
    private String redisHost;

    @ConfigValue(key = "redis.port", defaultValue = "6379")
    private int redisPort;

    @ConfigValue(key = "redis.max.total", defaultValue = "128")
    private int redisMaxTotal;

    @ConfigValue(key = "redis.max.idle", defaultValue = "128")
    private int redisMaxIdle;

    @ConfigValue(key = "redis.min.idle", defaultValue = "16")
    private int redisMinIdle;

    @ConfigValue(key = "redis.test.on.borrow", defaultValue = "true")
    private boolean redisTestOnBorrow;

    @ConfigValue(key = "redis.test.on.return", defaultValue = "true")
    private boolean redisTestOnReturn;

    @ConfigValue(key = "redis.test.while.idle", defaultValue = "true")
    private boolean redisTestWhileIdle;

    @ConfigValue(key = "redis.num.test.per.eviction.run", defaultValue = "3")
    private int redisNumTestPerEvictionRun;

    @ConfigValue(key = "redis.block.when.exhausted", defaultValue = "true")
    private boolean redisBlockWhenExhausted;


    @ConfigValue(key = "redis.cluster.host")
    private String redisClusterHost;


    public RedisConfiguration() throws Exception {init();}

    public String getRedisType() {
        return redisType;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public int getRedisMaxTotal() {
        return redisMaxTotal;
    }

    public int getRedisMaxIdle() {
        return redisMaxIdle;
    }

    public int getRedisMinIdle() {
        return redisMinIdle;
    }

    public boolean isRedisTestOnBorrow() {
        return redisTestOnBorrow;
    }

    public boolean isRedisTestOnReturn() {
        return redisTestOnReturn;
    }

    public boolean isRedisTestWhileIdle() {
        return redisTestWhileIdle;
    }

    public int getRedisNumTestPerEvictionRun() {
        return redisNumTestPerEvictionRun;
    }

    public boolean isRedisBlockWhenExhausted() {
        return redisBlockWhenExhausted;
    }

    public String getRedisClusterHost() {
        return redisClusterHost;
    }
}
