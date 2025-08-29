package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoIndex;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoIndexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Creates MongoDB indexes declared with @MongoIndex / @MongoIndexes on models.
 * <p>
 * Supported:
 *  - Class-level @MongoIndex (repeatable via @MongoIndexes) for compound/single indexes:
 *      @MongoIndex(fields = {"is_active:1", "created_at:-1"}, unique = false, sparse = false)
 *  - Field-level @MongoIndex for single-field indexes:
 *      @MongoField("name")
 *      @MongoIndex(unique = true)
 *      private String name;
 * <p>
 * Notes:
 *  - For class-level fields, prefer storage names (the values you put in @MongoField),
 *    e.g., "is_active", "created_at". You may also pass "javaFieldName" and the
 *    resolver will try to map it to @MongoField if present.
 *  - This utility is idempotent: creating an identical index again returns the existing name.
 */
public final class MongoIndexCreator {

    private MongoIndexCreator() {}

    /**
     * Entry point — detect collection from @MongoCollectionName and create indexes.
     */
    public static void createIndexes(MongoDatabase db, Class<?> modelClass) {
        String collectionName = getCollectionName(modelClass)
                .orElseThrow(() -> new IllegalStateException(
                        "Missing @MongoCollectionName on " + modelClass.getName() +
                                " (or provide an overload that accepts collectionName)."));

        createIndexes(db, modelClass, collectionName);
    }

    /**
     * Entry point — explicit collection name override.
     */
    public static void createIndexes(MongoDatabase db, Class<?> modelClass, String collectionName) {
        Objects.requireNonNull(db, "db must not be null");
        Objects.requireNonNull(modelClass, "modelClass must not be null");
        Objects.requireNonNull(collectionName, "collectionName must not be null");

        MongoCollection<Document> col = db.getCollection(collectionName);

        // 1) Class-level (repeatable) @MongoIndex / @MongoIndexes
        List<MongoIndex> classLevel = readClassLevelIndexes(modelClass);
        for (MongoIndex idx : classLevel) {
            createClassLevelIndex(col, modelClass, idx);
        }

        // 2) Field-level @MongoIndex (single-field)
        for (Field f : getAllFields(modelClass)) {
            MongoIndex idx = f.getAnnotation(MongoIndex.class);
            if (idx == null) continue;

            // Field-level should not use fields[]; we treat empty fields[] as single field index.
            if (idx.fields().length > 0) {
                // If someone uses fields[] at field-level, interpret it as a class-level style declaration.
                // But warn and continue anyway.
                System.out.println("[MongoIndexCreator] Warning: @MongoIndex on field " +
                        f.getDeclaringClass().getSimpleName() + "." + f.getName() +
                        " declared 'fields'. Interpreting as class-level style for this collection.");
                createClassLevelIndex(col, modelClass, idx);
                continue;
            }

            String storageName = storageNameOf(f);
            IndexOptions options = new IndexOptions()
                    .unique(idx.unique())
                    .sparse(idx.sparse());

            String createdName = col.createIndex(Indexes.ascending(storageName), options);
            System.out.println("[MongoIndexCreator] Created single-field index on '" +
                    storageName + "' -> name=" + createdName);
        }
    }

    // ---------- internals ----------

    private static Optional<String> getCollectionName(Class<?> modelClass) {
        MongoCollectionName coll = modelClass.getAnnotation(MongoCollectionName.class);
        if (coll == null || coll.value() == null || coll.value().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(coll.value());
    }

    private static List<MongoIndex> readClassLevelIndexes(Class<?> modelClass) {
        List<MongoIndex> result = new ArrayList<>();

        MongoIndex single = modelClass.getAnnotation(MongoIndex.class);
        if (single != null) {
            result.add(single);
        }

        MongoIndexes container = modelClass.getAnnotation(MongoIndexes.class);
        if (container != null && container.value() != null) {
            result.addAll(Arrays.asList(container.value()));
        }

        return result;
    }

    private static void createClassLevelIndex(MongoCollection<Document> col,
                                              Class<?> modelClass,
                                              MongoIndex idx) {
        String[] specs = idx.fields();
        if (specs == null || specs.length == 0) {
            System.out.println("[MongoIndexCreator] Skipping class-level @MongoIndex on " +
                    modelClass.getSimpleName() + " because fields[] is empty.");
            return;
        }

        List<Bson> parts = new ArrayList<>(specs.length);
        for (String spec : specs) {
            parts.add(parseIndexSpec(spec, modelClass));
        }

        Bson compound = parts.size() == 1 ? parts.get(0) : Indexes.compoundIndex(parts);
        IndexOptions options = new IndexOptions()
                .unique(idx.unique())
                .sparse(idx.sparse());

        String createdName = col.createIndex(compound, options);
        System.out.println("[MongoIndexCreator] Created class-level index on " +
                modelClass.getSimpleName() + " -> name=" + createdName +
                ", fields=" + Arrays.toString(specs) +
                ", unique=" + idx.unique() + ", sparse=" + idx.sparse());
    }

    /**
     * Accepts:
     *   - "field"          -> ascending
     *   - "field:1"        -> ascending
     *   - "field:-1"       -> descending
     *
     * field can be either:
     *   - storage name (preferred, matches @MongoField value)
     *   - java field name (we attempt to resolve to storage name via @MongoField on the class hierarchy)
     */
    private static Bson parseIndexSpec(String spec, Class<?> modelClass) {
        if (spec == null || spec.isBlank()) {
            throw new IllegalArgumentException("Empty index field spec in @MongoIndex on " + modelClass.getName());
        }

        String[] parts = spec.split(":");
        String rawName = parts[0].trim();
        int dir = 1;
        if (parts.length > 1) {
            try {
                dir = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid direction in index spec '" + spec +
                        "' on " + modelClass.getName() + ". Use 1 or -1.");
            }
        }

        // Try to resolve java field name -> storage name via @MongoField; fallback to raw
        String storageName = resolveStorageName(modelClass, rawName).orElse(rawName);

        return dir >= 0 ? Indexes.ascending(storageName) : Indexes.descending(storageName);
    }

    /**
     * Try to find a field named 'javaFieldName' in the class hierarchy, and if it has @MongoField,
     * return its value. Otherwise empty.
     */
    private static Optional<String> resolveStorageName(Class<?> modelClass, String javaFieldName) {
        for (Field f : getAllFields(modelClass)) {
            if (!f.getName().equals(javaFieldName)) continue;
            MongoField mf = f.getAnnotation(MongoField.class);
            if (mf != null && mf.value() != null && !mf.value().isBlank()) {
                return Optional.of(mf.value());
            }
            return Optional.of(javaFieldName); // present, but no @MongoField -> use same name
        }
        return Optional.empty();
    }

    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    private static String storageNameOf(Field f) {
        MongoField mf = f.getAnnotation(MongoField.class);
        if (mf != null && mf.value() != null && !mf.value().isBlank()) {
            return mf.value();
        }
        return f.getName();
    }
}