package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.BaseConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;

@ConfigFile(fileName = "config/mysql.properties")
public class MySQLConfiguration extends BaseConfiguration {

    @ConfigValue(key = "database.sql.mysql.driver", defaultValue = "com.mysql.cj.jdbc.Driver")
    private String mysqlDriver;

    @ConfigValue(key = "database.sql.mysql.pool", defaultValue = "30")
    private int mysqlPool;

    @ConfigValue(key = "database.sql.mysql.connection", defaultValue = "single")
    private String mysqlConnection;

    @ConfigValue(key = "database.sql.mysql.host", defaultValue = "localhost")
    private String mysqlHost;

    @ConfigValue(key = "database.sql.mysql.port", defaultValue = "3306")
    private int mysqlPort;

    @ConfigValue(key = "database.sql.mysql.name")
    private String mysqlName;

    @ConfigValue(key = "database.sql.mysql.user")
    private String mysqlUser;

    @ConfigValue(key = "database.sql.mysql.password")
    private String mysqlPassword;

    @ConfigValue(key = "database.sql.mysql.property.cache.prepare.statement", defaultValue = "true")
    private boolean mysqlPropertyCachePrepareStatement;

    @ConfigValue(key = "database.sql.mysql.property.cache.prepare.statement.cache.size", defaultValue = "250")
    private long mysqlPropertyCachePrepareStatementCacheSize;

    @ConfigValue(key = "database.sql.mysql.property.cache.prepare.statement.cache.sql.limit", defaultValue = "2048")
    private long mysqlPropertyCachePrepareStatementSqlLimit;


    @ConfigValue(key = "database.sql.mysql.schema")
    private String mysqlSchema;

    @ConfigValue(key = "database.sql.mysql.definer")
    private String mysqlDefiner;

    @ConfigValue(key = "database.sql.mysql.prefix")
    private String mysqlPrefix;


    public MySQLConfiguration() throws Exception {init();}


    public String getMysqlDriver() {
        return mysqlDriver;
    }

    public int getMysqlPool() {
        return mysqlPool;
    }

    public String getMysqlConnection() {
        return mysqlConnection;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlName() {
        return mysqlName;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public boolean isMysqlPropertyCachePrepareStatement() {
        return mysqlPropertyCachePrepareStatement;
    }

    public long getMysqlPropertyCachePrepareStatementCacheSize() {
        return mysqlPropertyCachePrepareStatementCacheSize;
    }

    public long getMysqlPropertyCachePrepareStatementSqlLimit() {
        return mysqlPropertyCachePrepareStatementSqlLimit;
    }

    public String getMysqlSchema() {
        return mysqlSchema;
    }

    public String getMysqlDefiner() {
        return mysqlDefiner;
    }

    public String getMysqlPrefix() {
        return mysqlPrefix;
    }
}
