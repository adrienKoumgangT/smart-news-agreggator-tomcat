package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/swagger.properties")
public class SwaggerConfiguration extends BaseConfiguration {

    @ConfigValue(key = "swagger.security.enable", defaultValue = "true")
    private boolean securityEnable;

    @ConfigValue(key = "swagger.security.ip.list", defaultValue = "localhost,0:0:0:0:0:0:0:1,127.0.0.1")
    private String securityIpList;

    public SwaggerConfiguration() throws Exception {init();}

    public boolean isSecurityEnable() {
        return securityEnable;
    }

    public String getSecurityIpList() {
        return securityIpList;
    }
}
