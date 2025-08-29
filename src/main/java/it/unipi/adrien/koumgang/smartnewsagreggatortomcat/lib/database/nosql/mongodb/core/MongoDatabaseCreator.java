package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.Objects;

/**
 * Utilities to create/ensure a MongoDB database exists.
 * <p>
 * Notes:
 *  - In MongoDB, a database is created lazily (on first collection insert/creation).
 *  - We "materialize" the DB by creating a small temporary collection and dropping it.
 */
public final class MongoDatabaseCreator {

    private static final String BOOTSTRAP_COLLECTION = "__init__";

    private MongoDatabaseCreator() { }

    /**
     * Check if a database exists by listing database names.
     */
    public static boolean databaseExists(MongoClient client, String dbName) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(dbName, "dbName must not be null");

        for (String name : client.listDatabaseNames()) {
            if (dbName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ensure the database exists (materialize if missing) and return its handle.
     * Idempotent: if it already exists, just returns it.
     */
    public static MongoDatabase ensureDatabase(MongoClient client, String dbName) {
        Objects.requireNonNull(client, "client must not be null");
        Objects.requireNonNull(dbName, "dbName must not be null");

        boolean exists = databaseExists(client, dbName);
        MongoDatabase db = client.getDatabase(dbName);

        if (!exists) {
            // Materialize DB by creating a tiny bootstrap collection, then drop it.
            try {
                db.createCollection(BOOTSTRAP_COLLECTION);
                System.out.println(
                        "[MongoDatabaseCreator] Created database '" + dbName
                                + "' via bootstrap collection '" + BOOTSTRAP_COLLECTION + "'."
                );
            } catch (Exception e) {
                // If another process created it concurrently, ignore collection already exists errors.
                System.out.println("[MongoDatabaseCreator] Bootstrap createCollection note: " + e.getMessage());
            } finally {
                try {
                    MongoCollection<Document> c = db.getCollection(BOOTSTRAP_COLLECTION);
                    c.drop();
                } catch (Exception ignore) {
                }
            }
        } else {
            System.out.println("[MongoDatabaseCreator] Database already exists: " + dbName);
        }

        return db;
    }

    /**
     * Ensure the database AND all collections & indexes for the given model classes.
     * Uses your MongoCollectionCreator and MongoIndexCreator under the hood.
     */
    public static MongoDatabase ensureDatabaseCollectionsAndIndexes(
            MongoClient client,
            String dbName,
            Class<?>... modelClasses
    ) {

        MongoDatabase db = ensureDatabase(client, dbName);

        if (modelClasses != null && modelClasses.length > 0) {
            // Ensure collections exist and build indexes declared via annotations.
            MongoCollectionCreator.ensureCollectionsAndIndexes(db, modelClasses);
        }

        return db;
    }

}