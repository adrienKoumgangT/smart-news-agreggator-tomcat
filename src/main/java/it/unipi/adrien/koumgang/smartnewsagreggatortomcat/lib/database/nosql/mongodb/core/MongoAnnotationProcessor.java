package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.IdConverter;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.*;

public class MongoAnnotationProcessor {

    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final DateTimeFormatter UTC_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(UTC_ZONE);

    // Cache for field metadata to improve performance
    private static final Map<Class<?>, List<FieldMetadata>> fieldMetadataCache = new HashMap<>();

    private static class FieldMetadata {
        Field field;
        String fieldName;
        boolean ignore;
        Class<?> fieldType;
        Class<?> genericType;

        FieldMetadata(Field field, String fieldName, boolean ignore, Class<?> fieldType, Class<?> genericType) {
            this.field = field;
            this.fieldName = fieldName;
            this.ignore = ignore;
            this.fieldType = fieldType;
            this.genericType = genericType;
        }
    }

    public static String getCollectionName(Class<?> clazz) {
        MongoCollectionName annotation = clazz.getAnnotation(MongoCollectionName.class);
        return annotation != null ? annotation.value() :
                clazz.getSimpleName().toLowerCase() + "s";
    }

    public static Document toDocument(Object obj) {
        if (obj == null) return null;

        Document document = new Document();
        Class<?> clazz = obj.getClass();
        List<FieldMetadata> fieldMetadataList = getFieldMetadata(clazz);

        for (FieldMetadata metadata : fieldMetadataList) {
            if (metadata.ignore) {
                continue;
            }

            try {
                metadata.field.setAccessible(true);
                Object value = metadata.field.get(obj);

                // Handle automatic timestamp generation
                if (value == null) {
                    if (metadata.field.isAnnotationPresent(MongoCreatedAt.class)) {
                        value = getCurrentUTCDateTime();
                    }
                }

                if (metadata.field.isAnnotationPresent(MongoUpdatedAt.class)) {
                    value = getCurrentUTCDateTime();
                }

                // Handle ID generation
                if (metadata.field.isAnnotationPresent(MongoIdString.class) && value == null) {
                    MongoIdString annotation = metadata.field.getAnnotation(MongoIdString.class);
                    if (annotation.generateOnCreate()) {
                        value = generateObjectId().toHexString();
                    }
                }

                if (value != null) {
                    value = convertValueForStorage(metadata, value);
                    document.put(metadata.fieldName, value);
                } else if(!metadata.field.isAnnotationPresent(MongoId.class)) {
                    document.put(metadata.fieldName, value);
                }


            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field: " + metadata.field.getName(), e);
            }
        }

        return document;
    }

    public static <T> T fromDocument(Document document, Class<T> clazz) {
        if (document == null) return null;

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            List<FieldMetadata> fieldMetadataList = getFieldMetadata(clazz);

            for (FieldMetadata metadata : fieldMetadataList) {
                if (metadata.ignore) {
                    continue;
                }

                metadata.field.setAccessible(true);
                Object value = document.get(metadata.fieldName);

                if (value != null) {
                    value = convertValueFromStorage(metadata, value);
                    metadata.field.set(instance, value);
                }
            }

            return instance;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance from document", e);
        }
    }

    private static Object convertValueForStorage(FieldMetadata metadata, Object value) {
        // Handle embedded documents
        if (metadata.field.isAnnotationPresent(MongoEmbedded.class)) {
            return toDocument(value);
        }

        // Handle embedded document lists
        if (metadata.field.isAnnotationPresent(MongoEmbeddedList.class) && value instanceof List) {
            List<Document> embeddedList = new ArrayList<>();
            for (Object item : (List<?>) value) {
                embeddedList.add(toDocument(item));
            }
            return embeddedList;
        }

        // Handle Map types
        if (value instanceof Map) {
            return convertMapForStorage((Map<?, ?>) value);
        }

        // Handle Collection types (List, Set, etc.)
        if (value instanceof Collection) {
            return convertCollectionForStorage((Collection<?>) value, metadata);
        }

        // Handle array types
        if (value != null && value.getClass().isArray()) {
            return convertArrayForStorage(value, metadata);
        }

        // Handle enum types
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }

        // Handle date/time types
        if (value instanceof LocalDateTime) {
            return convertDateTimeForStorage(metadata.field, (LocalDateTime) value);
        }

        if (value instanceof Date) {
            return convertUtilDateForStorage((Date) value);
        }

        if (value instanceof java.sql.Date) {
            return convertSqlDateForStorage((java.sql.Date) value);
        }

        // Handle ID conversion
        if (metadata.field.isAnnotationPresent(MongoId.class) && value instanceof String) {
            return convertIdForStorage((String) value);
        }

        // Handle custom ID converters
        if (metadata.field.isAnnotationPresent(MongoIdConverter.class)) {
            return convertWithCustomConverter(metadata.field, value, true);
        }

        return value;
    }

    private static Object convertValueFromStorage(FieldMetadata metadata, Object value) {
        // Handle embedded documents
        if (metadata.field.isAnnotationPresent(MongoEmbedded.class) && value instanceof Document) {
            return fromDocument((Document) value, metadata.fieldType);
        }

        // Handle embedded document lists
        if (metadata.field.isAnnotationPresent(MongoEmbeddedList.class) && value instanceof List) {
            return convertEmbeddedListFromStorage((List<?>) value, metadata);
        }

        // Handle Map types from Document
        if (Map.class.isAssignableFrom(metadata.fieldType) && value instanceof Document) {
            return convertMapFromStorage((Document) value, metadata);
        }

        // Handle Collection types from List
        if (Collection.class.isAssignableFrom(metadata.fieldType) && value instanceof List) {
            return convertCollectionFromStorage((List<?>) value, metadata);
        }

        // Handle array types from List
        if (metadata.fieldType.isArray() && value instanceof List) {
            return convertArrayFromStorage((List<?>) value, metadata);
        }

        // Handle enum types from String
        if (metadata.fieldType.isEnum() && value instanceof String) {
            return convertEnumFromStorage((String) value, metadata.fieldType);
        }

        // Handle date/time types from String
        if (metadata.fieldType == LocalDateTime.class && value instanceof String) {
            return convertDateTimeFromStorage((String) value, metadata.field);
        }

        if (metadata.fieldType == Date.class && value instanceof String) {
            return convertUtilDateFromStorage((String) value);
        }

        if (metadata.fieldType == java.sql.Date.class && value instanceof String) {
            return convertSqlDateFromStorage((String) value);
        }

        // Handle ID conversion from ObjectId to String
        if (metadata.field.isAnnotationPresent(MongoId.class) && value instanceof ObjectId) {
            // return ((ObjectId) value).toHexString();
            return value;
        }

        // Handle custom ID converters
        if (metadata.field.isAnnotationPresent(MongoIdConverter.class)) {
            return convertWithCustomConverter(metadata.field, value, false);
        }

        // Handle ObjectId to String for fields named "id" but not annotated
        if (("id".equals(metadata.fieldName) || "id".equals(metadata.field.getName()))
                && value instanceof ObjectId && metadata.fieldType == String.class) {
            return ((ObjectId) value).toHexString();
        }

        return value;
    }

    private static Object convertMapForStorage(Map<?, ?> map) {
        Document document = new Document();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey() != null ? entry.getKey().toString() : "null";
            Object mapValue = entry.getValue();

            // Recursively handle nested structures
            if (mapValue instanceof Map) {
                mapValue = convertMapForStorage((Map<?, ?>) mapValue);
            } else if (mapValue instanceof Collection) {
                mapValue = convertCollectionForStorage((Collection<?>) mapValue, null);
            } else if (mapValue != null && mapValue.getClass().isArray()) {
                mapValue = convertArrayForStorage(mapValue, null);
            }

            document.put(key, mapValue);
        }
        return document;
    }

    private static Object convertMapFromStorage(Document document, FieldMetadata metadata) {
        try {
            Map<Object, Object> map;
            if (metadata.fieldType.isInterface()) {
                map = new HashMap<>();
            } else {
                map = (Map<Object, Object>) metadata.fieldType.getDeclaredConstructor().newInstance();
            }

            for (String key : document.keySet()) {
                Object value = document.get(key);

                // Handle nested documents recursively
                if (value instanceof Document) {
                    value = convertNestedDocument((Document) value, metadata);
                } else if (value instanceof List) {
                    value = convertNestedList((List<?>) value, metadata);
                }

                // Convert key to appropriate type if needed
                Object mapKey = key;
                if (metadata.genericType != null && metadata.genericType != Object.class) {
                    // Handle key conversion for typed maps
                    if (metadata.genericType == Integer.class) {
                        mapKey = Integer.parseInt(key);
                    } else if (metadata.genericType == Long.class) {
                        mapKey = Long.parseLong(key);
                    } else if (metadata.genericType == Double.class) {
                        mapKey = Double.parseDouble(key);
                    } else if (metadata.genericType == Float.class) {
                        mapKey = Float.parseFloat(key);
                    } else if (metadata.genericType == Boolean.class) {
                        mapKey = Boolean.parseBoolean(key);
                    }
                }

                map.put(mapKey, value);
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create map instance", e);
        }
    }

    private static Object convertCollectionForStorage(Collection<?> collection, FieldMetadata metadata) {
        List<Object> result = new ArrayList<>();
        for (Object item : collection) {
            Object convertedItem = convertItemForStorage(item, metadata);
            result.add(convertedItem);
        }
        return result;
    }

    private static Object convertCollectionFromStorage(List<?> list, FieldMetadata metadata) {
        try {
            Collection<Object> collection;
            if (metadata.fieldType.isInterface()) {
                collection = new ArrayList<>();
            } else {
                collection = (Collection<Object>) metadata.fieldType.getDeclaredConstructor().newInstance();
            }

            for (Object item : list) {
                Object convertedItem = convertItemFromStorage(item, metadata);
                collection.add(convertedItem);
            }
            return collection;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create collection instance", e);
        }
    }

    private static Object convertArrayForStorage(Object array, FieldMetadata metadata) {
        int length = java.lang.reflect.Array.getLength(array);
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Object item = java.lang.reflect.Array.get(array, i);
            Object convertedItem = convertItemForStorage(item, metadata);
            result.add(convertedItem);
        }
        return result;
    }

    private static Object convertArrayFromStorage(List<?> list, FieldMetadata metadata) {
        Class<?> componentType = metadata.fieldType.getComponentType();
        Object array = java.lang.reflect.Array.newInstance(componentType, list.size());

        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            Object convertedItem = convertItemFromStorage(item,
                    new FieldMetadata(null, null, false, componentType, null));
            java.lang.reflect.Array.set(array, i, convertedItem);
        }
        return array;
    }

    private static Object convertEmbeddedListFromStorage(List<?> list, FieldMetadata metadata) {
        try {
            List<Object> result = new ArrayList<>();
            Class<?> itemClass = metadata.genericType;

            for (Object item : list) {
                if (item instanceof Document) {
                    result.add(fromDocument((Document) item, itemClass));
                } else {
                    result.add(item);
                }
            }

            // Convert to the target collection type
            if (metadata.fieldType.isInterface()) {
                return result;
            } else {
                Collection<Object> collection = (Collection<Object>) metadata.fieldType.getDeclaredConstructor().newInstance();
                collection.addAll(result);
                return collection;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create embedded list", e);
        }
    }

    private static Object convertItemForStorage(Object item, FieldMetadata metadata) {
        if (item == null) return null;

        if (item instanceof Map) {
            return convertMapForStorage((Map<?, ?>) item);
        } else if (item instanceof Collection) {
            return convertCollectionForStorage((Collection<?>) item, metadata);
        } else if (item.getClass().isArray()) {
            return convertArrayForStorage(item, metadata);
        } else if (item instanceof LocalDateTime) {
            return convertDateTimeForStorage(null, (LocalDateTime) item);
        } else if (item instanceof Enum) {
            return ((Enum<?>) item).name();
        }

        return item;
    }

    private static Object convertItemFromStorage(Object item, FieldMetadata metadata) {
        if (item == null) return null;

        if (item instanceof Document) {
            return convertNestedDocument((Document) item, metadata);
        } else if (item instanceof List) {
            return convertNestedList((List<?>) item, metadata);
        } else if (metadata != null && metadata.fieldType.isEnum() && item instanceof String) {
            return convertEnumFromStorage((String) item, metadata.fieldType);
        }

        return item;
    }

    private static Object convertNestedDocument(Document document, FieldMetadata metadata) {
        // For nested documents, we try to determine the target type
        if (metadata != null && metadata.genericType != null) {
            return fromDocument(document, metadata.genericType);
        }

        // Fallback: convert to Map
        Map<String, Object> map = new HashMap<>();
        for (String key : document.keySet()) {
            Object value = document.get(key);
            if (value instanceof Document) {
                value = convertNestedDocument((Document) value, null);
            } else if (value instanceof List) {
                value = convertNestedList((List<?>) value, null);
            }
            map.put(key, value);
        }
        return map;
    }

    private static Object convertNestedList(List<?> list, FieldMetadata metadata) {
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Document) {
                result.add(convertNestedDocument((Document) item, metadata));
            } else if (item instanceof List) {
                result.add(convertNestedList((List<?>) item, metadata));
            } else {
                result.add(item);
            }
        }
        return result;
    }

    private static String convertDateTimeForStorage(Field field, LocalDateTime dateTime) {
        boolean useUTC = true;
        if (field != null && field.isAnnotationPresent(MongoDateTime.class)) {
            MongoDateTime annotation = field.getAnnotation(MongoDateTime.class);
            useUTC = annotation.utc();
        }

        if (useUTC) {
            return dateTime.atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(UTC_ZONE)
                    .format(DateTimeFormatter.ISO_INSTANT);
        } else {
            return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    private static LocalDateTime convertDateTimeFromStorage(String dateString, Field field) {
        try {
            boolean useUTC = true;
            if (field != null && field.isAnnotationPresent(MongoDateTime.class)) {
                MongoDateTime annotation = field.getAnnotation(MongoDateTime.class);
                useUTC = annotation.utc();
            }

            if (useUTC) {
                Instant instant = Instant.parse(dateString);
                return LocalDateTime.ofInstant(instant, UTC_ZONE);
            } else {
                return LocalDateTime.parse(dateString);
            }
        } catch (Exception e) {
            // Try different formats
            try {
                return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to parse date: " + dateString, ex);
            }
        }
    }

    private static String convertUtilDateForStorage(Date date) {
        return date.toInstant().atZone(UTC_ZONE).format(DateTimeFormatter.ISO_INSTANT);
    }

    private static Date convertUtilDateFromStorage(String dateString) {

        try {
            // Case 1: ISO-8601 with zone (e.g., ...Z or ...+02:00)
            return Date.from(Instant.parse(dateString));
        } catch (DateTimeParseException ignored) {
            // Case 2: No zone -> parse as local date-time and assume UTC

            try {
                DateTimeFormatter f = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                        .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
                        .toFormatter();

                LocalDateTime ldt = LocalDateTime.parse(dateString, f);
                return Date.from(ldt.toInstant(ZoneOffset.UTC));
            } catch (Exception ignored2) {
                // Define formatter to allow microseconds without offset
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

                // Parse as LocalDateTime
                LocalDateTime ldt = LocalDateTime.parse(dateString, formatter);

                // Convert to Instant assuming UTC
                Instant instant = ldt.toInstant(ZoneOffset.UTC);

                // Convert to legacy java.util.Date
                return Date.from(instant);
            }
        }
    }

    private static String convertSqlDateForStorage(java.sql.Date date) {
        return date.toLocalDate().atStartOfDay(UTC_ZONE).format(DateTimeFormatter.ISO_INSTANT);
    }

    private static java.sql.Date convertSqlDateFromStorage(String dateString) {
        Instant instant = Instant.parse(dateString);
        return new java.sql.Date(instant.toEpochMilli());
    }

    private static Object convertEnumFromStorage(String enumValue, Class<?> enumClass) {
        try {
            @SuppressWarnings("unchecked")
            Enum<?> result = Enum.valueOf((Class<Enum>) enumClass, enumValue);
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static ObjectId convertIdForStorage(String id) {
        if (id != null && ObjectId.isValid(id)) {
            return new ObjectId(id);
        }
        throw new IllegalArgumentException("Invalid ObjectId format: " + id);
    }

    private static Object convertWithCustomConverter(Field field, Object value, boolean toStorage) {
        try {
            MongoIdConverter annotation = field.getAnnotation(MongoIdConverter.class);
            Class<? extends IdConverter<?>> converterClass = annotation.converter();

            // Use getDeclaredConstructor().newInstance() instead of deprecated newInstance()
            Constructor<?> constructor = converterClass.getDeclaredConstructor();
            IdConverter<Object> converter = (IdConverter<Object>) constructor.newInstance();

            if (toStorage) {
                // Cast the value to the appropriate type expected by the converter
                Object typedValue = convertValueToExpectedType(value, converter.getType());
                return converter.toObjectId(typedValue);
            } else {
                return converter.fromObjectId((ObjectId) value);
            }
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to use custom ID converter", e);
        }
    }

    private static Object convertValueToExpectedType(Object value, Class<?> expectedType) {
        if (value == null) {
            return null;
        }

        if (expectedType.isInstance(value)) {
            return value;
        }

        // Handle common type conversions
        if (expectedType == String.class) {
            return value.toString();
        }

        if (expectedType == Integer.class && value instanceof Number) {
            return ((Number) value).intValue();
        }

        if (expectedType == Long.class && value instanceof Number) {
            return ((Number) value).longValue();
        }

        throw new IllegalArgumentException(
                "Cannot convert value of type " + value.getClass().getName() +
                        " to expected type " + expectedType.getName()
        );
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass(); // go up the inheritance chain
        }

        return fields;
    }

    private static List<FieldMetadata> getFieldMetadata(Class<?> clazz) {
        if (fieldMetadataCache.containsKey(clazz)) {
            return fieldMetadataCache.get(clazz);
        }

        List<FieldMetadata> metadataList = new ArrayList<>();
        for (Field field : getAllFields(clazz)) {
            String fieldName = getFieldName(field);
            boolean ignore = isFieldIgnored(field);
            Class<?> fieldType = field.getType();
            Class<?> genericType = getGenericType(field);

            metadataList.add(new FieldMetadata(field, fieldName, ignore, fieldType, genericType));
        }

        fieldMetadataCache.put(clazz, metadataList);
        return metadataList;
    }

    public static String getFieldName(Field field) {
        // Check for embedded annotations first
        MongoEmbedded embedded = field.getAnnotation(MongoEmbedded.class);
        if (embedded != null && !embedded.value().isEmpty()) {
            return embedded.value();
        }

        MongoEmbeddedList embeddedList = field.getAnnotation(MongoEmbeddedList.class);
        if (embeddedList != null && !embeddedList.value().isEmpty()) {
            return embeddedList.value();
        }

        // Then check for regular field annotations
        MongoId mongoId = field.getAnnotation(MongoId.class);
        if (mongoId != null) {
            return mongoId.value();
        }

        MongoField mongoField = field.getAnnotation(MongoField.class);
        if (mongoField != null) {
            return mongoField.value();
        }

        return field.getName();
    }

    private static boolean isFieldIgnored(Field field) {
        MongoField mongoField = field.getAnnotation(MongoField.class);
        return mongoField != null && mongoField.ignore();
    }

    private static Class<?> getGenericType(Field field) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) field.getGenericType();
            java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();

            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        return Object.class;
    }

    public static Date nowMinusDays(int days) {
        // LocalDateTime in UTC minus X days
        LocalDateTime ldt = LocalDateTime.now(ZoneOffset.UTC).minusDays(days);

        // Convert LocalDateTime -> Instant (with UTC)
        Instant instant = ldt.toInstant(ZoneOffset.UTC);

        // Convert Instant -> java.util.Date
        return Date.from(instant);
    }

    public static Date getCurrentUTCDateTime() {
        // return LocalDateTime.now(ZoneOffset.UTC);
        return new Date();
    }

    public static String formatAsUTC(Date date) {
        if (date == null) return null;

        // ISO-8601 style UTC formatter
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone(UTC_ZONE));

        return sdf.format(date);
    }

    public static ObjectId generateObjectId() {
        return new ObjectId();
    }

    public static boolean isValidObjectId(String id) {
        return ObjectId.isValid(id);
    }

    public static class IndexDefinition {
        private final String[] fields;
        private final boolean unique;
        private final boolean sparse;

        public IndexDefinition(String[] fields, boolean unique, boolean sparse) {
            this.fields = fields;
            this.unique = unique;
            this.sparse = sparse;
        }

        public String[] getFields() { return fields; }
        public boolean isUnique() { return unique; }
        public boolean isSparse() { return sparse; }
    }

    public static List<IndexDefinition> getIndexDefinitions(Class<?> clazz) {
        List<IndexDefinition> indexes = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            MongoIndex indexAnnotation = field.getAnnotation(MongoIndex.class);
            if (indexAnnotation != null) {
                String fieldName = getFieldName(field);
                indexes.add(new IndexDefinition(
                        new String[]{fieldName},
                        indexAnnotation.unique(),
                        indexAnnotation.sparse()
                ));
            }
        }

        // Check for class-level index annotations
        MongoIndex classIndex = clazz.getAnnotation(MongoIndex.class);
        if (classIndex != null && classIndex.fields().length > 0) {
            indexes.add(new IndexDefinition(
                    classIndex.fields(),
                    classIndex.unique(),
                    classIndex.sparse()
            ));
        }

        return indexes;
    }
}