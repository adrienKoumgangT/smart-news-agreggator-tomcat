package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.factory;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl.CacheHandlerInterface;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl.CacheHandlerMap;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl.CacheHandlerRedis;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl.CacheHandlerRedisCluster;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.CacheSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.GsonSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.serializer.JacksonSerializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.CacheConfiguration;
import redis.clients.jedis.HostAndPort;

import java.util.HashSet;
import java.util.Set;

public class CacheFactory {

    public static CacheHandlerInterface<String, Object> getHandler() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();

        CacheSerializer serializer = getSerializer();

        String cacheHandler = cacheConfiguration.getCacheHandler();

        return switch (cacheHandler.toLowerCase()) {
            case "map" -> new CacheHandlerMap<>(cacheConfiguration.getCacheHandlerMapPersistencePath(), serializer);
            case "redis" -> new CacheHandlerRedis<>(cacheConfiguration.getCacheHandlerRedisHost(), cacheConfiguration.getCacheHandlerRedisPort(), serializer);
            case "redis-cluster" -> {
                int port = cacheConfiguration.getCacheHandlerRedisClusterPort();
                String[] hosts = cacheConfiguration.getCacheHandlerRedisClusterHost().split(",");
                Set<HostAndPort> clusterNodes = new HashSet<>();
                for (String host : hosts) {
                    clusterNodes.add(new HostAndPort(host, port));
                }
                yield new CacheHandlerRedisCluster<>(clusterNodes, serializer);
            }
            default -> throw new IllegalArgumentException("Unknown cache handler: " + cacheHandler);
        };
    }

    public static CacheSerializer getSerializer() throws Exception {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        String serializerType = cacheConfiguration.getCacheSerializerType();

        return switch (serializerType.toLowerCase()) {
            case "gson" -> new GsonSerializer();
            case "jackson" -> new JacksonSerializer();
            default -> throw new IllegalArgumentException("Unknown serializer: " + serializerType);
        };
    }
}

