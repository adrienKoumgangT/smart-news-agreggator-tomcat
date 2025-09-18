package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.dao.AuthEventLogDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model.AuthEventLog;
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

public class AuthEventLogRepository extends BaseRepository implements AuthEventLogDao {

    public static AuthEventLogRepository getInstance() {
        return new AuthEventLogRepository(MongoInstance.getInstance().mongoDatabase());
    }

    private final MongoCollection<Document> authEventLogCollection;
    private static final Class<AuthEventLog> authEventLogClass = AuthEventLog.class;

    public AuthEventLogRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(authEventLogClass);
        this.authEventLogCollection = database.getCollection(collectionName);
    }

    private Field getField(String fieldName) {
        try {
            return authEventLogClass.getDeclaredField(fieldName);
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

    private Bson getFilterBySuccess(Boolean success) {
        return Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("success")),
                success
        );
    }

    private Bson getFilterByEventAndSuccess(String event, Boolean success) {
        return Filters.and(
                Filters.eq(MongoAnnotationProcessor.getFieldName(getField("event")), event),
                Filters.eq(MongoAnnotationProcessor.getFieldName(getField("success")), success)
        );
    }

    /**
     * @param id the id auth event log to return
     * @return auth event log instance
     */
    @Override
    public Optional<AuthEventLog> findById(ObjectId id) {
        Document document = authEventLogCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
    }

    /**
     * @return count all auth event log
     */
    @Override
    public long count() {
        return authEventLogCollection.countDocuments();
    }

    /**
     * @return all auth event log
     */
    @Override
    public List<AuthEventLog> findAll() {
        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : authEventLogCollection.find()) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return pageSize auth event log
     */
    @Override
    public List<AuthEventLog> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = authEventLogCollection
                .find()
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param event type of auth event log
     * @return count auth event log for this specify event
     */
    @Override
    public long countByEvent(String event) {
        return authEventLogCollection.countDocuments(getFilterByEvent(event));
    }

    /**
     * @param event type of auth event log
     * @return auth event log for this specify event
     */
    @Override
    public List<AuthEventLog> findAllByEvent(String event) {
        FindIterable<Document> cursor = authEventLogCollection
                .find(getFilterByEvent(event))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param event type of auth event log
     * @param page number of page to return
     * @param pageSize number max of element to return
     * @return pageSize auth event log for this specify event
     */
    @Override
    public List<AuthEventLog> findAllByEvent(String event, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = authEventLogCollection
                .find(getFilterByEvent(event))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param success success or failed
     * @return count auth event log with success or failed (depend on success param value)
     */
    @Override
    public long countBySuccess(Boolean success) {
        return authEventLogCollection.countDocuments(getFilterBySuccess(success));
    }

    /**
     * @param success success or failed
     * @return auth event log with success or failed (depend on success param value)
     */
    @Override
    public List<AuthEventLog> findAllBySuccess(Boolean success) {
        FindIterable<Document> cursor = authEventLogCollection
                .find(getFilterBySuccess(success))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param success success or failed
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return auth event log with success or failed (depend on success param value)
     */
    @Override
    public List<AuthEventLog> findAllBySuccess(Boolean success, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = authEventLogCollection
                .find(getFilterBySuccess(success))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param event type of event log
     * @param success if return success of failed event
     * @return count auth event log for this specify event
     */
    @Override
    public long countByEventAndSuccess(String event, Boolean success) {
        return authEventLogCollection.countDocuments(getFilterByEventAndSuccess(event, success));
    }

    /**
     * @param event type of event log
     * @param success if return success of failed event
     * @return auth event log for this specify event and success or not
     */
    @Override
    public List<AuthEventLog> findAllByEventAndSuccess(String event, Boolean success) {
        FindIterable<Document> cursor = authEventLogCollection
                .find(getFilterByEventAndSuccess(event, success))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT));

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param event type of event log
     * @param success  if return success of failed event
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return auth event log for this specify event and success or not
     */
    @Override
    public List<AuthEventLog> findAllByEventAndSuccess(String event, Boolean success, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = authEventLogCollection
                .find(getFilterByEventAndSuccess(event, success))
                .sort(Sorts.descending(MONGO_FIELD_NAME_CREATED_AT))
                .skip(skip)
                .limit(pageSize);

        List<AuthEventLog> authEventLogs = new ArrayList<>();
        for (Document document : cursor) {
            authEventLogs.add(MongoAnnotationProcessor.fromDocument(document, authEventLogClass));
        }
        return authEventLogs;
    }

    /**
     * @param authEventLog the auth event log to save
     * @return new auth event log id saving
     */
    @Override
    public ObjectId save(AuthEventLog authEventLog) {
        // Initialize timestamps before saving
        DateTimeInitializer.initializeTimestamps(authEventLog);

        Document document = MongoAnnotationProcessor.toDocument(authEventLog);
        InsertOneResult result = authEventLogCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    /**
     * @param id the auth event log to delete
     * @return true if success else false
     */
    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = authEventLogCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

}
