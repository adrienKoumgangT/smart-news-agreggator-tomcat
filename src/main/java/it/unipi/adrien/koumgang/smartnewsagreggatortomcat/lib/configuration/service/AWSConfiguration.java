package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/aws.properties")
public class AWSConfiguration extends BaseConfiguration {

    @ConfigValue(key = "aws.access.key", defaultValue = "")
    private String accessKey;

    @ConfigValue(key = "aws.secret.key", defaultValue = "")
    private String secretKey;

    @ConfigValue(key = "aws.regions", defaultValue = "")
    private String regions;

    public AWSConfiguration() throws Exception {init();}

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegions() {
        return regions;
    }
}
