package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationReader {

    private static final String CONFIG_BASE_PATH = "/"; //  "/config/";
    private final Map<String, Properties> cache = new ConcurrentHashMap<>();

    public String getProperty(String fileName, String key) {
        if (!fileName.endsWith(".properties")) {
            throw new IllegalArgumentException("File name must end with '.properties'");
        }
        return get(fileName, key);
    }

    public String get(String fileName, String key) {
        Properties props = loadProperties(fileName);
        return props.getProperty(key);
    }

    public String resolve(String fileName, String fullKey, String defaultValue) {
        String envKey = fullKey.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        try {
            String fileValue = getProperty(fileName, fullKey);
            if (fileValue != null && !fileValue.isBlank()) {
                return fileValue;
            }
        } catch (Exception ignored) {}

        return defaultValue;
    }

    private Properties loadProperties(String fileName) {
        return cache.computeIfAbsent(fileName, name -> {
            Properties props = new Properties();
            try (InputStream input = getClass().getResourceAsStream(CONFIG_BASE_PATH + name)) {
                if (input == null) {
                    throw new RuntimeException("Configuration file not found: " + name);
                }
                props.load(input);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration: " + name, e);
            }
            return props;
        });
    }
}

