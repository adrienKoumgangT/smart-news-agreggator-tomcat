package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/Configuration.properties")
public class ApiConfiguration extends BaseConfiguration {

    @ConfigValue(key = "prod", defaultValue = "false")
    private boolean prod;

    @ConfigValue(key = "debug", defaultValue = "true")
    private boolean debug;

    @ConfigValue(key = "base.api.url", defaultValue = "http://localhost:8080")
    private String baseApiUrl;

    @ConfigValue(key = "jwts.private.key")
    private String jwsPrivateKey;

    @ConfigValue(key = "jwts.public.key")
    private String jwsPublicKey;


    public ApiConfiguration() throws Exception {init();}

    public boolean isProd() {
        return prod;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getBaseApiUrl() {
        return baseApiUrl;
    }

    public String getJwsPrivateKey() {
        return jwsPrivateKey;
    }

    public String getJwsPublicKey() {
        return jwsPublicKey;
    }
}
