package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
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
import java.util.*;

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

    private Bson getFilterByEvent(String event) {
        return Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("event")),
                event
        );
    }

    private Bson getFilterByName(String name) {
        return Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("name")),
                name
        );
    }

    private Bson getFilterByEventAndName(String event, String name) {
        return Filters.and(
                Filters.eq(MongoAnnotationProcessor.getFieldName(getField("event")), event),
                Filters.eq(MongoAnnotationProcessor.getFieldName(getField("name")), name)
        );
    }

    /**
     * @param id the id server event log to return
     * @return server event log instance if found
     */
    @Override
    public Optional<ServerEventLog> findById(ObjectId id) {
        Document document = serverEventLogCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
    }

    /**
     * @return the number of event log
     */
    @Override
    public long count() {
        return serverEventLogCollection.countDocuments();
    }

    /**
     * @return all server event log
     */
    @Override
    public List<ServerEventLog> findAll() {
        FindIterable<Document> cursor = serverEventLogCollection
                .find()
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param event type of event log
     * @return the number of event log for this specify event
     */
    @Override
    public long countByEvent(String event) {
        return serverEventLogCollection.countDocuments(getFilterByEvent(event));
    }

    /**
     * @param event type of event log
     * @return server event log for this specify event
     */
    @Override
    public List<ServerEventLog> findByEvent(String event) {
        FindIterable<Document> cursor = serverEventLogCollection
                .find(getFilterByEvent(event))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param event event of server event log
     * @param page number of page to return
     * @param pageSize number of element to return
     * @return server event log for with specify event
     */
    @Override
    public List<ServerEventLog> findByEvent(String event, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        List<ServerEventLog> serverEventLogs = new ArrayList<>();

        FindIterable<Document> cursor = serverEventLogCollection
                .find(getFilterByEvent(event))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param name type of event log
     * @return the number of event log for this specify name
     */
    @Override
    public long countByName(String name) {
        return serverEventLogCollection.countDocuments(getFilterByName(name));
    }

    /**
     * @param name name of event log
     * @return server event log for with specify name
     */
    @Override
    public List<ServerEventLog> findByName(String name) {
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("name")),
                name
        );

        FindIterable<Document> cursor = serverEventLogCollection
                .find(getFilterByName(name))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param name name of event log
     * @param page number of page to return
     * @param pageSize number of element to return
     * @return server event log for with specify name
     */
    @Override
    public List<ServerEventLog> findByName(String name, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = serverEventLogCollection
                .find(getFilterByName(name))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param event type of event log
     * @param name type of event log
     * @return the number of event log for this specify name
     */
    @Override
    public long countByEventAndName(String event, String name) {
        return serverEventLogCollection.countDocuments(getFilterByEventAndName(event, name));
    }

    /**
     * @param event type of event log
     * @param name name of event log
     * @return server event log for this specify event and name
     */
    @Override
    public List<ServerEventLog> findByEventAndName(String event, String name) {
        FindIterable<Document> cursor = serverEventLogCollection
                .find(getFilterByEventAndName(event, name))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param event type of event log
     * @param name name of event log
     * @param page number of page to return
     * @param pageSize number of element to return
     * @return server event log for this specify event and name
     */
    @Override
    public List<ServerEventLog> findByEventAndName(String event, String name, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = serverEventLogCollection
                .find(getFilterByEventAndName(event, name))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        List<ServerEventLog> serverEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            serverEventLogs.add(MongoAnnotationProcessor.fromDocument(document, serverEventLogClass));
        }
        return serverEventLogs;
    }

    /**
     * @param serverEventLog the server event log to save
     * @return new server event log id saving
     */
    @Override
    public ObjectId save(ServerEventLog serverEventLog) {
        // Initialize timestamps before saving
        DateTimeInitializer.initializeTimestamps(serverEventLog);

        Document document = MongoAnnotationProcessor.toDocument(serverEventLog);
        InsertOneResult result = serverEventLogCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    /**
     * @param id the server event log to delete
     * @return true if success else false
     */
    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = serverEventLogCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }


    public List<String> listDistinctEvents() {
        List<String> events = new ArrayList<>();
        serverEventLogCollection.distinct("event", String.class).into(events);
        return events;
    }

    public Map<String, List<String>> mapDistinctEventsNames() {
        List<Document> pipeline = Arrays.asList(
                new Document("$group", new Document("_id", new Document("event", "$event").append("name", "$name"))),
                new Document("$project", new Document("_id", 0).append("event", "$_id.event").append("name", "$_id.name"))
        );

        AggregateIterable<Document> results = serverEventLogCollection.aggregate(pipeline);

        Map<String, List<String>> events = new HashMap<>();
        for (Document document : results) {
            String event  = document.getString("event");
            String name = document.getString("name");
            if(!events.containsKey(event)) {
                events.put(event, new ArrayList<>());
            }
            events.get(event).add(name);
        }

        return events;
    }

}