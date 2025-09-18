package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.dao.MetaDataDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.model.MetaData;
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

import static com.mongodb.client.model.Filters.eq;

public class MetaDataRepository extends BaseRepository implements MetaDataDao {


    public static MetaDataRepository getInstance() {
        return new MetaDataRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private final MongoCollection<Document> metaDataCollection;
    private final Class<MetaData> metaDataClass = MetaData.class;

    public MetaDataRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(metaDataClass);
        this.metaDataCollection = database.getCollection(collectionName);
    }

    private Field getField(String fieldName) {
        try {
            return metaDataClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }


    private Bson getFilterByMetaType(String metaType) {
        return eq(
                MongoAnnotationProcessor.getFieldName(getField("metaType")),
                metaType
        );
    }

    private Bson getFilterByName(String metaType, String name) {
        return Filters.and(
                eq(MongoAnnotationProcessor.getFieldName(getField("metaType")), metaType),
                eq(MongoAnnotationProcessor.getFieldName(getField("name")), name)
        );
    }

    private Optional<MetaData> getMetaData(FindIterable<Document> cursor) {
        List<MetaData> metaDataList = new ArrayList<>();
        for (Document document : cursor) {
            metaDataList.add(MongoAnnotationProcessor.fromDocument(document, metaDataClass));
        }

        if(metaDataList.isEmpty()) return Optional.empty();
        return Optional.ofNullable(metaDataList.getFirst());
    }

    private List<MetaData> getMetaDatas(FindIterable<Document> cursor) {
        List<MetaData> metaDataList = new ArrayList<>();
        for (Document document : cursor) {
            metaDataList.add(MongoAnnotationProcessor.fromDocument(document, metaDataClass));
        }

        return metaDataList;
    }


    /**
     * @param id metadata id
     * @return metadata with this identification
     */
    @Override
    public Optional<MetaData> findById(ObjectId id) {
        Document document = metaDataCollection.find(eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, metaDataClass));
    }

    /**
     * @return all documents in metadata collection
     */
    @Override
    public List<MetaData> findAll() {
        FindIterable<Document> cursor = metaDataCollection.find();
        return getMetaDatas(cursor);
    }

    /**
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return max pageSize metadata
     */
    @Override
    public List<MetaData> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = metaDataCollection
                .find()
                .skip(skip)
                .limit(pageSize);

        return getMetaDatas(cursor);
    }

    /**
     * @return number of documents in metadata collection
     */
    @Override
    public long count() {
        return metaDataCollection.estimatedDocumentCount();
    }

    /**
     * @param metaType meta type
     * @return list of metadata associated at this meta type
     */
    @Override
    public List<MetaData> findAll(String metaType) {
        Bson filter = getFilterByMetaType(metaType);

        FindIterable<Document> cursor = metaDataCollection.find(filter);
        return getMetaDatas(cursor);
    }

    /**
     * @param metaType meta type
     * @param page number of page to return
     * @param pageSize number of element to return
     * @return list of metadata associated at this meta type
     */
    @Override
    public List<MetaData> findAll(String metaType, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        Bson filter = getFilterByMetaType(metaType);

        FindIterable<Document> cursor = metaDataCollection
                .find(filter)
                .skip(skip)
                .limit(pageSize);

        return getMetaDatas(cursor);
    }

    /**
     * @param metaType meta type
     * @return number of document in collection who have reference to this meta type
     */
    @Override
    public long count(String metaType) {
        Bson filter = getFilterByMetaType(metaType);

        return metaDataCollection.countDocuments(filter);
    }

    /**
     * @param metaType meta type
     * @param name name of metadata
     * @return a metadata with this specific type and name
     */
    @Override
    public Optional<MetaData> findByName(String metaType, String name) {
        Bson filter = getFilterByName(metaType, name);

        FindIterable<Document> cursor = metaDataCollection.find(filter);
        return getMetaData(cursor);
    }

    /**
     * @param metaData new metadata
     * @return id of new document
     */
    @Override
    public ObjectId save(MetaData metaData) {
        DateTimeInitializer.initializeTimestamps(metaData);

        Document document = MongoAnnotationProcessor.toDocument(metaData);
        InsertOneResult result = metaDataCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    /**
     * @param metaData meta data to update
     * @return true if updated else false
     */
    @Override
    public boolean update(MetaData metaData) {
        DateTimeInitializer.updateTimestamps(metaData);

        Document document = MongoAnnotationProcessor.toDocument(metaData);
        document.remove("_id"); // Remove ID for update

        UpdateResult result = metaDataCollection.updateOne(
                eq("_id", metaData.getMetadataId()),
                new Document("$set", document)
        );

        return result.getModifiedCount() > 0;
    }

    /**
     * @param id identification of meta data to delete
     * @return true if deleted else false
     */
    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = metaDataCollection.deleteOne(eq("_id", id));
        return result.getDeletedCount() > 0;
    }
}
