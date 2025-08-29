package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCreatedAt;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoUpdatedAt;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;

import java.lang.reflect.Field;
import java.util.Date;

public class DateTimeInitializer {

    public static void initializeTimestamps(Object obj) {
        if (obj == null) return;

        Class<?> clazz = obj.getClass();
        Date now = MongoAnnotationProcessor.getCurrentUTCDateTime();

        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                // Initialize @MongoCreatedAt fields if null
                if (field.isAnnotationPresent(MongoCreatedAt.class)) {
                    Object currentValue = field.get(obj);
                    if (currentValue == null) {
                        field.set(obj, now);
                    }
                }

                // Always update @MongoUpdatedAt fields
                if (field.isAnnotationPresent(MongoUpdatedAt.class)) {
                    field.set(obj, now);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to initialize timestamp field: " + field.getName(), e);
            }
        }
    }

    public static void updateTimestamps(Object obj) {
        if (obj == null) return;

        Class<?> clazz = obj.getClass();
        Date now = MongoAnnotationProcessor.getCurrentUTCDateTime();

        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                // Update @MongoUpdatedAt fields
                if (field.isAnnotationPresent(MongoUpdatedAt.class)) {
                    field.set(obj, now);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to update timestamp field: " + field.getName(), e);
            }
        }
    }

}
