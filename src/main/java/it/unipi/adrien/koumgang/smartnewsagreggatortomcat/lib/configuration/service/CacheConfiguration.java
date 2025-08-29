package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/cache.properties")
public class CacheConfiguration extends BaseConfiguration {

    @ConfigValue(key = "cache.serializer.type", defaultValue = "jackson")
    private String cacheSerializerType;

    @ConfigValue(key = "cache.handler", defaultValue = "map")
    private String cacheHandler;

    @ConfigValue(key = "cache.handler.map.persistence", defaultValue = "true")
    private boolean cacheHandlerMapPersistence;

    @ConfigValue(key = "cache.handler.map.persistence.path", defaultValue = "cache-store.json.gz")
    private String cacheHandlerMapPersistencePath;

    @ConfigValue(key = "cache.handler.redis.host", defaultValue = "localhost")
    private String cacheHandlerRedisHost;

    @ConfigValue(key = "cache.handler.redis.port", defaultValue = "6379")
    private int cacheHandlerRedisPort;

    @ConfigValue(key = "cache.handler.redis.max.total", defaultValue = "128")
    private int cacheHandlerRedisMaxTotal;

    @ConfigValue(key = "cache.handler.redis.max.idle", defaultValue = "128")
    private int cacheHandlerRedisMaxIdle;

    @ConfigValue(key = "cache.handler.redis.min.idle", defaultValue = "16")
    private int cacheHandlerRedisMinIdle;

    @ConfigValue(key = "cache.handler.redis.test.on.borrow", defaultValue = "true")
    private boolean cacheHandlerRedisTestOnBorrow;

    @ConfigValue(key = "cache.handler.redis.test.on.return", defaultValue = "true")
    private boolean cacheHandlerRedisTestOnReturn;

    @ConfigValue(key = "cache.handler.redis.test.while.idle", defaultValue = "true")
    private boolean cacheHandlerRedisTestWhileIdle;

    @ConfigValue(key = "cache.handler.redis.num.test.per.eviction.run", defaultValue = "3")
    private int cacheHandlerRedisNumTestPerEvictionRun;

    @ConfigValue(key = "cache.handler.redis.block.when.exhausted", defaultValue = "true")
    private boolean cacheHandlerRedisBlockWhenExhausted;

    @ConfigValue(key = "cache.handler.redis.cluster.port", defaultValue = "6379")
    private int cacheHandlerRedisClusterPort;

    @ConfigValue(key = "cache.handler.redis.cluster.host")
    private String cacheHandlerRedisClusterHost;

    @ConfigValue(key = "cache.handler.memcached.host", defaultValue = "localhost")
    private String cacheHandlerMemcachedHost;

    @ConfigValue(key = "cache.handler.memcached.port", defaultValue = "11211")
    private int cacheHandlerMemcachedPort;

    @ConfigValue(key = "cache.handler.dynamodb.table", defaultValue = "cache-table")
    private String cacheHandlerDynamoDbTable;

    @ConfigValue(key = "cache.handler.dynamodb.field.id", defaultValue = "id")
    private String cacheHandlerDynamoDbFieldId;

    @ConfigValue(key = "cache.handler.dynamodb.field.value", defaultValue = "value")
    private String cacheHandlerDynamoDbFieldValue;

    @ConfigValue(key = "cache.handler.dynamodb.field.type", defaultValue = "type")
    private String cacheHandlerDynamoDbFieldType;


    public CacheConfiguration() throws Exception {init();}


    public String getCacheSerializerType() {
        return cacheSerializerType;
    }

    public String getCacheHandler() {
        return cacheHandler;
    }

    public boolean isCacheHandlerMapPersistence() {
        return cacheHandlerMapPersistence;
    }

    public String getCacheHandlerMapPersistencePath() {
        return cacheHandlerMapPersistencePath;
    }

    public String getCacheHandlerRedisHost() {
        return cacheHandlerRedisHost;
    }

    public int getCacheHandlerRedisPort() {
        return cacheHandlerRedisPort;
    }

    public int getCacheHandlerRedisMaxTotal() {
        return cacheHandlerRedisMaxTotal;
    }

    public int getCacheHandlerRedisMaxIdle() {
        return cacheHandlerRedisMaxIdle;
    }

    public int getCacheHandlerRedisMinIdle() {
        return cacheHandlerRedisMinIdle;
    }

    public boolean isCacheHandlerRedisTestOnBorrow() {
        return cacheHandlerRedisTestOnBorrow;
    }

    public boolean isCacheHandlerRedisTestOnReturn() {
        return cacheHandlerRedisTestOnReturn;
    }

    public boolean isCacheHandlerRedisTestWhileIdle() {
        return cacheHandlerRedisTestWhileIdle;
    }

    public int getCacheHandlerRedisNumTestPerEvictionRun() {
        return cacheHandlerRedisNumTestPerEvictionRun;
    }

    public boolean isCacheHandlerRedisBlockWhenExhausted() {
        return cacheHandlerRedisBlockWhenExhausted;
    }

    public int getCacheHandlerRedisClusterPort() {
        return cacheHandlerRedisClusterPort;
    }

    public String getCacheHandlerRedisClusterHost() {
        return cacheHandlerRedisClusterHost;
    }

    public String getCacheHandlerMemcachedHost() {
        return cacheHandlerMemcachedHost;
    }

    public int getCacheHandlerMemcachedPort() {
        return cacheHandlerMemcachedPort;
    }

    public String getCacheHandlerDynamoDbTable() {
        return cacheHandlerDynamoDbTable;
    }

    public String getCacheHandlerDynamoDbFieldId() {
        return cacheHandlerDynamoDbFieldId;
    }

    public String getCacheHandlerDynamoDbFieldValue() {
        return cacheHandlerDynamoDbFieldValue;
    }

    public String getCacheHandlerDynamoDbFieldType() {
        return cacheHandlerDynamoDbFieldType;
    }
}
