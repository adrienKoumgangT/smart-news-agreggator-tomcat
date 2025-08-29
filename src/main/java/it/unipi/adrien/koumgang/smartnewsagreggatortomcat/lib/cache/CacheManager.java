package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.factory.CacheFactory;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.cache.impl.CacheHandlerInterface;

public class CacheManager {

    private static CacheManager instance;

    public static CacheManager getInstance() throws Exception {
        if(instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }




    CacheHandlerInterface<String, Object> cacheHandler;

    public CacheManager() throws Exception {
        this.cacheHandler = CacheFactory.getHandler();
    }

    public CacheHandlerInterface<String, Object> getHandler() {
        return cacheHandler;
    }
}
