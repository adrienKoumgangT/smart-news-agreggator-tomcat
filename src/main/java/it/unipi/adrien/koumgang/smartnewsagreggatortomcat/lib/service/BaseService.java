package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BaseService {

    protected static final Gson gson = new GsonBuilder().serializeNulls().create();

    protected static final long CACHE_TTL = 1800;  // 30 minutes

}
