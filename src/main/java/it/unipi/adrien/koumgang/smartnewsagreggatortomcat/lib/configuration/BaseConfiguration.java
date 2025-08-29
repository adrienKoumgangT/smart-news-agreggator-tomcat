package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigFile;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.annotation.ConfigValue;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.config.ConfigurationReader;

import java.lang.reflect.Field;
import java.util.Optional;

@ConfigFile
public abstract class BaseConfiguration {

    public void init() throws Exception {
        ConfigurationReader configurationReader = new ConfigurationReader();

        ConfigFile configFileAnnotation = this.getClass().getAnnotation(ConfigFile.class);

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                field.setAccessible(true);
                ConfigValue annotation = field.getAnnotation(ConfigValue.class);
                String key = annotation.key();
                String defaultValue = annotation.defaultValue();

                String value = configurationReader.resolve(configFileAnnotation.fileName(), key, defaultValue.isEmpty() ? null : defaultValue);
                Class<?> fieldType = field.getType();
                Object typedValue;

                if (fieldType == Optional.class) {
                    Class<?> genericType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    typedValue = Optional.ofNullable(convert(value, genericType));
                } else {
                    typedValue = convert(value, fieldType);
                }

                field.set(this, typedValue);
            }
        }
    }

    private Object convert(String value, Class<?> type) {
        if (value == null) return null;
        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        return null;
    }

    /**
     * Converts a dotted configuration key like "mail.smtp.host"
     * into a human-readable string like "Mail Smtp Host".
     *
     * @param key the configuration key
     * @return a human-readable label
     */
    private static String convertKeyToLabel(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        String[] tokens = key.split("\\."); // Split on "."
        StringBuilder label = new StringBuilder();
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            // Capitalize the first letter and append the rest of the token.
            label.append(Character.toUpperCase(token.charAt(0)));
            if (token.length() > 1) {
                label.append(token.substring(1));
            }
            label.append(" ");
        }
        return label.toString().trim(); // Remove trailing space.
    }

    public void printConfig() throws IllegalAccessException {
        System.out.println();
        StringBuilder sb = new StringBuilder();

        for(Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                field.setAccessible(true);

                ConfigValue annotation = field.getAnnotation(ConfigValue.class);
                String key = annotation.key();
                String defaultValue = annotation.defaultValue();
                Object value = field.get(this);
                sb.append(convertKeyToLabel(key)).append(": ").append(value != null ? value : "null").append("\n");
            }
        }

        System.out.println(sb);
    }

}
