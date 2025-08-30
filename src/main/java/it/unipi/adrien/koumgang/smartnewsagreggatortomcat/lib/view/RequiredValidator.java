package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequiredValidator {

    public static List<String> validateRequiredFields(Object obj) {
        List<String> missing = new ArrayList<>();
        if (obj == null) {
            missing.add("Object is null");
            return missing;
        }

        Class<?> clazz = obj.getClass();

        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(Required.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (value == null) {
                        missing.add(field.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return missing;
    }

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            Collections.addAll(fields, c.getDeclaredFields());
        }
        return fields;
    }
}

