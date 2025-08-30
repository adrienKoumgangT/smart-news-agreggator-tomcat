package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.dao.TestDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model.Test;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils.DateTimeInitializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.repository.BaseRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@MongoRepository
public class TestRepository extends BaseRepository implements TestDao {

    private final MongoCollection<Document> testCollection;
    private final Class<Test> testClass = Test.class;

    public TestRepository(MongoDatabase db) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(testClass);
        this.testCollection = db.getCollection(collectionName);
    }


    public static TestRepository getInstance() {
        return new TestRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private Field getField(String fieldName) {
        try {
            return testClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    @Override
    public Optional<Test> findById(ObjectId id) {
        Document document = testCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, testClass));
    }

    @Override
    public List<Test> findAll() {
        List<Test> tests = new ArrayList<>();
        for (Document document : testCollection.find()) {
            tests.add(MongoAnnotationProcessor.fromDocument(document, testClass));
        }
        return tests;
    }

    @Override
    public List<Test> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        List<Test> tests = new ArrayList<>();
        for (Document document : testCollection.find().skip(skip).limit(pageSize)) {
            tests.add(MongoAnnotationProcessor.fromDocument(document, testClass));
        }
        return tests;
    }

    @Override
    public ObjectId save(Test test) {
        // Initialize timestamps before saving
        DateTimeInitializer.initializeTimestamps(test);

        Document document = MongoAnnotationProcessor.toDocument(test);
        InsertOneResult result = testCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    @Override
    public boolean update(Test test) {
        // Update timestamps before updating
        DateTimeInitializer.updateTimestamps(test);

        Document document = MongoAnnotationProcessor.toDocument(test);
        document.remove("_id"); // Remove ID for update

        UpdateResult result = testCollection.updateOne(
                Filters.eq("_id", test.getTestId()),
                new Document("$set", document)
        );

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = testCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    @Override
    public long count() {
        return testCollection.countDocuments();
    }
}
