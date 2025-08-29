package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;

import java.util.Objects;

/**
 * Creates MongoDB collections based on model class annotations.
 * <p>
 * Usage:
 *   MongoCollectionCreator.ensureCollection(db, Test.class); // will create if missing
 *   MongoCollectionCreator.ensureCollectionsAndIndexes(db, Test.class, OtherModel.class);
 */
public final class MongoCollectionCreator {

    private MongoCollectionCreator() {}

    /**
     * Ensure the collection (derived from @MongoCollection on the model) exists.
     * If missing, create it with default options.
     */
    public static void ensureCollection(MongoDatabase db, Class<?> modelClass) {
        ensureCollection(db, modelClass, null);
    }

    /**
     * Ensure the collection exists; if missing, create it using the provided options.
     */
    public static void ensureCollection(MongoDatabase db, Class<?> modelClass, CreateCollectionOptions options) {
        Objects.requireNonNull(db, "db must not be null");
        Objects.requireNonNull(modelClass, "modelClass must not be null");

        String collectionName = getCollectionNameOrThrow(modelClass);
        ensureCollection(db, collectionName, options);
    }

    /**
     * Ensure the collection with the given name exists; create it if missing.
     */
    public static void ensureCollection(MongoDatabase db, String collectionName, CreateCollectionOptions options) {
        Objects.requireNonNull(db, "db must not be null");
        Objects.requireNonNull(collectionName, "collectionName must not be null");

        boolean exists = collectionExists(db, collectionName);
        if (!exists) {
            if (options != null) {
                db.createCollection(collectionName, options);
            } else {
                db.createCollection(collectionName);
            }
            System.out.println("[MongoCollectionCreator] Created collection: " + collectionName);
        } else {
            System.out.println("[MongoCollectionCreator] Collection already exists: " + collectionName);
        }
    }

    /**
     * Ensure collections exist for all given model classes, then create their indexes.
     * This is a convenient "one-shot" bootstrap for startup.
     */
    public static void ensureCollectionsAndIndexes(MongoDatabase db, Class<?>... modelClasses) {
        Objects.requireNonNull(db, "db must not be null");
        if (modelClasses == null) return;

        for (Class<?> model : modelClasses) {
            String collectionName = getCollectionNameOrThrow(model);
            ensureCollection(db, collectionName, null);

            // After ensuring the collection, create indexes declared via annotations.
            MongoIndexCreator.createIndexes(db, model, collectionName);
        }
    }

    // -------- helpers --------

    private static boolean collectionExists(MongoDatabase db, String collectionName) {
        for (String name : db.listCollectionNames()) {
            if (collectionName.equals(name)) return true;
        }
        return false;
    }

    private static String getCollectionNameOrThrow(Class<?> modelClass) {
        // Fully-qualify your annotation type to avoid name clash with the driver class.
        MongoCollectionName ann = modelClass.getAnnotation(MongoCollectionName.class);

        if (ann == null || ann.value() == null || ann.value().isBlank()) {
            throw new IllegalStateException("Missing or empty @MongoCollection on " + modelClass.getName());
        }
        return ann.value();
    }
}
