package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.configuration.service.MongoDBConfiguration;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoCollectionCreator;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoDatabaseCreator;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.reflections.Reflections;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

public class MongoProvider {

    private final MongoClient client;
    private final MongoDatabase database;
    private final String databaseName;

    public MongoProvider() {
        try {
            final MongoDBConfiguration mongoDBConfiguration = new MongoDBConfiguration();
            mongoDBConfiguration.printConfig();

            final String connectionString = mongoDBConfiguration.getMongodbUri();
            this.databaseName = Objects.requireNonNull(
                    mongoDBConfiguration.getMongodbDatabase(),
                    "MongoDB database name must not be null"
            );

            System.out.printf("[MongoProvider] Database name: '%s'.%n", databaseName);

            // POJO codec
            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            System.out.printf("[MongoProvider] POJO codec defined.%n");

            // Build settings with sane timeouts
            ConnectionString cs = new ConnectionString(connectionString);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(cs)
                    .codecRegistry(pojoCodecRegistry)
                    .readPreference(ReadPreference.secondaryPreferred())
                    .writeConcern(WriteConcern.MAJORITY)
                    .retryWrites(true)
                    .retryReads(true)
                    .applyToClusterSettings(builder -> {
                        // leave defaults; can tune here if needed
                    })
                    .applyToSocketSettings(builder ->
                            builder.connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(20, TimeUnit.SECONDS))
                    .applyToConnectionPoolSettings(builder ->
                            builder.maxSize(50)
                                    .maxConnectionIdleTime(60, TimeUnit.SECONDS))
                    .applyToServerSettings(builder ->
                            builder.heartbeatFrequency(10, TimeUnit.SECONDS))
                    .build();

            System.out.printf("[MongoProvider] MongoClientSettings defined.%n");

            client = MongoClients.create(settings);

            // --- Connection check (ping) ---
            System.out.printf("[MongoProvider] verify Connection begin.%n");
            verifyConnection(Duration.ofSeconds(5));
            System.out.printf("[MongoProvider] verify Connection end.%n");

            database = client.getDatabase(databaseName);

            // create database if don't exist
            MongoDatabaseCreator.ensureDatabase(client, databaseName);

            // --- Database presence check (non-creating) ---
            if (!databaseExists()) {
                // Not an error per se (MongoDB creates DBs lazily).
                System.out.printf(
                        "[MongoProvider] Database '%s' does not exist yet (no collections).%n",
                        databaseName
                );
            } else {
                System.out.printf(
                        "[MongoProvider] Database '%s' is present.%n",
                        databaseName
                );
            }

            createCollectionAndIndexes();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MongoDB connection", e);
        }
    }

    public MongoClient mongoClient() {
        return client;
    }

    public MongoDatabase mongoDatabase() {
        return database;
    }

    public void close() {
        client.close();
    }





    /** Simple ping to admin DB to validate connectivity & auth */
    private void verifyConnection(Duration maxTime) {
        try {
            // Add maxTimeMS to avoid hanging forever if hosts are unreachable
            Document res = mongoAdmin().runCommand(
                    new Document("ping", 1)
                            .append("maxTimeMS", (int) maxTime.toMillis())
            );

            // "ok" is returned as a Double (1.0), not an Integer
            Number ok = res.get("ok", Number.class);
            if (ok == null || ok.doubleValue() != 1.0d) {
                throw new RuntimeException("MongoDB ping returned non-OK: " + res.toJson());
            }
            System.out.println("[MongoProvider] Ping OK.");

        } catch (MongoTimeoutException te) {
            throw new RuntimeException("MongoDB ping timed out (cannot reach servers). Check URI/replica set.", te);
        } catch (Exception e) {
            throw new RuntimeException("MongoDB ping failed. Check credentials/network.", e);
        }
    }

    /** Returns true if a DB with target name is listed (i.e., has at least one collection). */
    public boolean databaseExists() {
        return StreamSupport.stream(client.listDatabaseNames().spliterator(), false)
                .anyMatch(name -> name.equals(databaseName));
    }

    /** Returns true if a Collection with target name exist */
    public boolean collectionExists(String name) {
        for (String collectionName : database.listCollectionNames()) {
            if (collectionName.equals(name)) {
                return true;
            }
        }
        return false;
    }


    private MongoDatabase mongoAdmin() {
        return client.getDatabase("admin");
    }


    public void createCollectionAndIndexes() {

        try {
            Reflections reflections = new Reflections("it/unipi/adrien/koumgang/smartnewsagreggatortomcat.apps");

            // find all classes annotated with @MongoCollectionName
            Set<Class<?>> collections = reflections.getTypesAnnotatedWith(MongoCollectionName.class);

            for (Class<?> collectionClass : collections) {
                try {
                    MongoCollectionCreator.ensureCollection(this.database, collectionClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
