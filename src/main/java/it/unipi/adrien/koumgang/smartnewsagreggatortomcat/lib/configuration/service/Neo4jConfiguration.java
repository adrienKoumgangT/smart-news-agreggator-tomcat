package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/neo4j.properties")
public class Neo4jConfiguration extends BaseConfiguration {

    @ConfigValue(key = "neo4j.uri", defaultValue = "neo4j://localhost:7687")
    private String neo4jUri;

    @ConfigValue(key = "neo4j.username")
    private String neo4jUsername;

    @ConfigValue(key = "neo4j.password")
    private String neo4jPassword;


    public Neo4jConfiguration() throws Exception {init();}


    public String getNeo4jUri() {
        return neo4jUri;
    }

    public String getNeo4jUsername() {
        return neo4jUsername;
    }

    public String getNeo4jPassword() {
        return neo4jPassword;
    }
}
