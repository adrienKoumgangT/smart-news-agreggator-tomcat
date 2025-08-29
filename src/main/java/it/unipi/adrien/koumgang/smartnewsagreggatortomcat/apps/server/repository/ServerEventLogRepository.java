package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.dao.ServerEventLogDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model.ServerEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils.DateTimeInitializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.repository.BaseRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerEventLogRepository extends BaseRepository implements ServerEventLogDao {

    public static ServerEventLogRepository getInstance() {
        return new ServerEventLogRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private final MongoCollection<Document> serverEventLogCollection;
    private static final Class<ServerEventLog> serverEventLogClass = ServerEventLog.class;

    public ServerEventLogRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(serverEventLogClass);
        this.serverEventLogCollection = database.getCollection(collectionName);
    }

    private Field getField(String fieldName) {
        try {
            return serverEventLogClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    /**
     * @param id id the server event log to return
     * @return server error log instance if found
     */
    @Override
    public Optional<ServerEventLog> findById(ObjectId id) {
        Document document = serverEventLogCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
    }

    /**
     * @return all server error log
     */
    @Override
    public List<ServerEventLog> findAll() {
        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : serverEventLogCollection.find()) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param event type of event log
     * @return event error log for this specify event
     */
    @Override
    public List<ServerEventLog> findByEvent(String event) {
        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("event")),
                event
        );

        for (Document document : serverEventLogCollection.find(filter)) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param name type of event log
     * @return event error log for this specify event
     */
    @Override
    public List<ServerEventLog> findByName(String name) {
        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("name")),
                name
        );

        for (Document document : serverEventLogCollection.find(filter)) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param serverEventLog the server event log
     * @return new server event log saving
     */
    @Override
    public ServerEventLog save(ServerEventLog serverEventLog) {
        // Initialize timestamps before saving
        DateTimeInitializer.initializeTimestamps(serverEventLog);

        Document document = MongoAnnotationProcessor.toDocument(serverEventLog);
        serverEventLogCollection.insertOne(document);

        // Set the generated ID back to the user object
        ObjectId generatedId = document.getObjectId("_id");
        serverEventLog.setServerEventLogId(generatedId);

        return serverEventLog;
    }

    /**
     * @param id the server event log to delete
     * @return true if success, false otherwise
     */
    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = serverEventLogCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    /**
     * @return the number of event log
     */
    @Override
    public long count() {
        return serverEventLogCollection.countDocuments();
    }

    /**
     * @param event type of event log
     * @return the number of event log for this specify event
     */
    @Override
    public long countByEvent(String event) {
        Document filter = new Document(
                MongoAnnotationProcessor.getFieldName(getField("event")),
                event
        );
        return serverEventLogCollection.countDocuments(filter);
    }

    /**
     * @param name type of event log
     * @return the number of event log for this specify name
     */
    @Override
    public long countByName(String name) {
        Document filter = new Document(
                MongoAnnotationProcessor.getFieldName(getField("name")),
                name
        );
        return serverEventLogCollection.countDocuments(filter);
    }

}