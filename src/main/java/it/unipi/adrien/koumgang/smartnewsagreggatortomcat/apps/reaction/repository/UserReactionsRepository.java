package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.dao.UserReactionsDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReactions;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils.DateTimeInitializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.EntityTypeEnum;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class UserReactionsRepository implements UserReactionsDao {

    public static UserReactionsRepository getInstance() {
        return new UserReactionsRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private final MongoCollection<Document> userReactionsCollection;
    private static final Class<UserReactions> userReactionsClass = UserReactions.class;
    private static final Class<EmbeddedReaction> embeddedReactionClass = EmbeddedReaction.class;


    public UserReactionsRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(userReactionsClass);
        this.userReactionsCollection = database.getCollection(collectionName);
    }

    private Field getField(String fieldName) {
        try {
            return userReactionsClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    private Field getEmbeddedField(String fieldName) {
        try {
            return embeddedReactionClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    private Bson getFilterByArticleId(String articleId) {
        return eq(
                MongoAnnotationProcessor.getFieldName(getField("articleId")),
                articleId
        );
    }

    private Bson getFilterByArticleIdAndEntityId(EntityTypeEnum entityType, String entityId) {
        return Filters.and(
                eq(MongoAnnotationProcessor.getFieldName(getField("entityType")), entityType),
                eq(MongoAnnotationProcessor.getFieldName(getField("entityId")), entityId)
        );
    }

    private Bson getFilterByArticleIdEntityIdAndAuthorId(EntityTypeEnum entityType,String entityId, String authorId) {
        return Filters.and(
                eq(MongoAnnotationProcessor.getFieldName(getField("entityType")), entityType),
                eq(MongoAnnotationProcessor.getFieldName(getField("entityId")), entityId),
                eq(MongoAnnotationProcessor.getFieldName(getField("authorId")), authorId)
        );
    }

    private List<UserReactions> getUserReactions(FindIterable<Document> cursor) {
        List<UserReactions> userReactions = new ArrayList<>();
        for (Document document : cursor) {
            userReactions.add(MongoAnnotationProcessor.fromDocument(document, userReactionsClass));
        }

        return userReactions;
    }

    private Optional<UserReactions> getUserReaction(FindIterable<Document> cursor) {
        List<UserReactions> userReactions = new ArrayList<>();
        for (Document document : cursor) {
            userReactions.add(MongoAnnotationProcessor.fromDocument(document, userReactionsClass));
        }

        if (userReactions.isEmpty()) return Optional.empty();
        return Optional.ofNullable(userReactions.getFirst());
    }

    private List<EmbeddedReaction> getEmbeddedReactions(FindIterable<Document> cursor) {
        List<EmbeddedReaction> embeddedReactions = new ArrayList<>();
        for (Document document : cursor) {
            embeddedReactions.add(MongoAnnotationProcessor.fromDocument(document, embeddedReactionClass));
        }

        return embeddedReactions;
    }

    private Optional<EmbeddedReaction> getEmbeddedReaction(FindIterable<Document> cursor) {
        List<EmbeddedReaction> embeddedReactions = new ArrayList<>();
        for (Document document : cursor) {
            embeddedReactions.add(MongoAnnotationProcessor.fromDocument(document, embeddedReactionClass));
        }

        if (embeddedReactions.isEmpty()) return Optional.empty();
        return Optional.ofNullable(embeddedReactions.getFirst());
    }

    /**
     * @param id user reaction id
     * @return user reaction with this identification
     */
    @Override
    public Optional<UserReactions> findById(ObjectId id) {
        Document document = userReactionsCollection.find(eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, userReactionsClass));
    }

    /**
     * @return number of documents in user reactions collection
     */
    @Override
    public long count() {
        return userReactionsCollection.estimatedDocumentCount();
    }

    /**
     * @return all documents in user reactions collection
     */
    @Override
    public List<UserReactions> findAll() {
        FindIterable<Document> cursor = userReactionsCollection.find();
        return getUserReactions(cursor);
    }

    /**
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return max pageSize user reactions
     */
    @Override
    public List<UserReactions> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = userReactionsCollection
                .find()
                .skip(skip)
                .limit(pageSize);

        return getUserReactions(cursor);
    }


    /**
     * @param entityId id of entity
     * @return number of document in collection who have reference to article and this entity
     */
    @Override
    public long count(EntityTypeEnum entityType, String entityId) {
        return userReactionsCollection.countDocuments(getFilterByArticleIdAndEntityId(entityType, entityId));
    }

    /**
     * @param entityId id of entity
     * @return list of document in collection who have reference to article and this entity
     */
    @Override
    public List<UserReactions> findByArticleId(EntityTypeEnum entityType, String entityId) {
        Bson filter = getFilterByArticleIdAndEntityId(entityType, entityId);

        FindIterable<Document> cursor = userReactionsCollection.find(filter);

        return getUserReactions(cursor);
    }

    /**
     * @param entityId id of entity
     * @param page number of page to return
     * @param pageSize number of element to return
     * @return list of pageSize document in collection who have reference to article and this entity
     */
    @Override
    public List<UserReactions> findByArticleId(EntityTypeEnum entityType, String entityId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        Bson filter = getFilterByArticleIdAndEntityId(entityType, entityId);

        FindIterable<Document> cursor = userReactionsCollection
                .find(filter)
                .skip(skip)
                .limit(pageSize);;

        return getUserReactions(cursor);
    }

    /**
     * @param entityId id of entity
     * @param AuthorId id of author
     * @return list of document in collection who have reference to article, this entity and this author
     */
    @Override
    public Optional<EmbeddedReaction> findByArticleId(EntityTypeEnum entityType, String entityId, String AuthorId) {
        Bson filter = getFilterByArticleIdEntityIdAndAuthorId(entityType, entityId, AuthorId);

        // TODO: is incorrect
        FindIterable<Document> cursor = userReactionsCollection.find(filter);

        return getEmbeddedReaction(cursor);
    }

    /**
     * @param userReactions new user reactions container
     * @return id of new document
     */
    @Override
    public ObjectId save(UserReactions userReactions) {
        DateTimeInitializer.initializeTimestamps(userReactions);

        Document document = MongoAnnotationProcessor.toDocument(userReactions);
        InsertOneResult result = userReactionsCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    /**
     * @param userReactions user reaction container to update
     * @return true if updated else false
     */
    @Override
    public boolean update(UserReactions userReactions) {
        DateTimeInitializer.updateTimestamps(userReactions);

        Document document = MongoAnnotationProcessor.toDocument(userReactions);
        document.remove("_id"); // Remove ID for update

        UpdateResult result = userReactionsCollection.updateOne(
                eq("_id", userReactions.getUserReactionsId()),
                new Document("$set", document)
        );

        return result.getModifiedCount() > 0;
    }

    /**
     * @param id user reactions container to delete
     * @return true if deleted else false
     */
    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = userReactionsCollection.deleteOne(eq("_id", id));
        return result.getDeletedCount() > 0;
    }
}
