package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationService {

    private static final String CONFIG_BASE_PATH = "/config/";
    private final Map<String, Map<String, Object>> cache = new HashMap<>();
    private final Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));

    public String resolve(String fullKey, String defaultValue) {
        String envKey = fullKey.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        String[] parts = fullKey.split("\\.", 2);
        if (parts.length != 2) return defaultValue;

        String fileKey = parts[0];         // mail
        String yamlPath = parts[1];        // smtp.host

        Object value = getFromYaml(fileKey + ".yml", yamlPath);
        return value != null ? value.toString() : defaultValue;
    }

    private Object getFromYaml(String fileName, String keyPath) {
        Map<String, Object> yamlData = cache.computeIfAbsent(fileName, this::loadYamlFile);
        if (yamlData == null) return null;

        String[] keys = keyPath.split("\\.");
        Object current = yamlData;

        for (String k : keys) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(k);
            if (current == null) return null;
        }

        return current;
    }

    private Map<String, Object> loadYamlFile(String fileName) {
        try (InputStream input = getClass().getResourceAsStream(CONFIG_BASE_PATH + fileName)) {
            if (input == null) {
                throw new RuntimeException("YAML config file not found: " + fileName);
            }
            return yaml.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML file: " + fileName, e);
        }
    }
}