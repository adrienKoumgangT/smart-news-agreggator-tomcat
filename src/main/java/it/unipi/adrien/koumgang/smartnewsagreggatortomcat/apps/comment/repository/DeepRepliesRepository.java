package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.dao.DeepRepliesDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.DeepComment;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.DeepReplies;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils.DateTimeInitializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.repository.BaseRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class DeepRepliesRepository extends BaseRepository implements DeepRepliesDao {

    public static DeepRepliesRepository getInstance() {
        return new DeepRepliesRepository(MongoInstance.getInstance().mongoDatabase());
    }

    private final MongoCollection<Document> deepRepliesCollection;
    private static final Class<DeepReplies> deepRepliesClass = DeepReplies.class;
    private static final Class<DeepComment> deepCommentClass = DeepComment.class;

    public DeepRepliesRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(deepRepliesClass);
        this.deepRepliesCollection = database.getCollection(collectionName);
    }

    private Field getField(String fieldName) {
        try {
            return deepRepliesClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    private List<DeepReplies> getDeepReplies(FindIterable<Document> cursor) {
        List<DeepReplies> deepReplies = new ArrayList<>();
        for (Document document : cursor) {
            deepReplies.add(MongoAnnotationProcessor.fromDocument(document, deepRepliesClass));
        }

        return deepReplies;
    }

    private List<DeepComment> getDeepComments(List<Document> cursor) {
        List<DeepComment> deepComments = new ArrayList<>();
        for (Document document : cursor) {
            deepComments.add(MongoAnnotationProcessor.fromDocument(document, deepCommentClass));
        }
        return deepComments;
    }

    /**
     * @param id parent comment id
     * @return parent comment if present else empty optional
     */
    @Override
    public Optional<DeepReplies> findById(ObjectId id) {
        Document document = deepRepliesCollection.find(eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, deepRepliesClass));
    }

    /**
     * @return number of documents in deep replies collection
     */
    @Override
    public long count() {
        return deepRepliesCollection.estimatedDocumentCount();
    }

    /**
     * @return all documents in deep replies collection
     */
    @Override
    public List<DeepReplies> findAll() {
        FindIterable<Document> cursor = deepRepliesCollection.find();
        return getDeepReplies(cursor);
    }

    /**
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return max pageSize deep replies
     */
    @Override
    public List<DeepReplies> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = deepRepliesCollection
                .find()
                .skip(skip)
                .limit(pageSize);

        return getDeepReplies(cursor);
    }

    private Bson getFilterByArticleId(String articleId) {
        return eq(
                MongoAnnotationProcessor.getFieldName(getField("articleId")),
                articleId
        );
    }

     private Bson getFilterByParentCommentId(String parentCommentId) {
        return eq(
                "_id",
                parentCommentId
        );
    }

    private Bson getFilterByDepth(Integer depth) {
        return eq(
                MongoAnnotationProcessor.getFieldName(getField("depth")),
                depth
        );
    }

    /**
     * @param entityId id of article
     * @return number of document in collection who have reference to article
     */
    @Override
    public long count(String entityId) {
        return deepRepliesCollection.countDocuments(getFilterByArticleId(entityId));
    }

    /**
     * @param entityId id of article
     * @return documents in collection who have reference to article
     */
    @Override
    public List<DeepReplies> findByEntityId(String entityId) {
        FindIterable<Document> cursor = deepRepliesCollection.find(getFilterByArticleId(entityId));

        return getDeepReplies(cursor);
    }

    /**
     * @param entityId id of article
     * @param page number of page to return
     * @param pageSize max number of element to return
     * @return max pageSize documents in collection who have reference to article
     */
    @Override
    public List<DeepReplies> findByEntityId(String entityId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = deepRepliesCollection
                .find(getFilterByArticleId(entityId))
                .skip(skip)
                .limit(pageSize);

        return getDeepReplies(cursor);
    }

    /**
     * @param entityId id of article
     * @param parentId parent comment id
     * @return number of document in collection who have reference to article and parent comment
     */
    @Override
    public long count(String entityId, String parentId) {
        List<Bson> pipeline = List.of(
                Aggregates.match(Filters.and(
                        eq("_id", new ObjectId(parentId)),
                        eq(MongoAnnotationProcessor.getFieldName(getField("articleId")), entityId)
                )),
                Aggregates.project(new Document("replyCount", new Document("$size", "$replies")))
        );

        AggregateIterable<Document> result = deepRepliesCollection.aggregate(pipeline);

        Document doc = result.first();
        return doc != null ? doc.getInteger("replyCount", 0) : 0L;
    }

    /**
     * @param entityId id of article
     * @param parentId parent comment id
     * @param depth integer field that represents how far down in the reply hierarchy a comment is
     * @return number of deep comment associated at this article and parent comment
     */
    @Override
    public long count(String entityId, String parentId, Integer depth) {
        if (depth == null) {
            return count(entityId, parentId);
        }

        List<Bson> pipeline = List.of(
                Aggregates.match(Filters.and(
                        Filters.eq("_id", new ObjectId(parentId)),
                        Filters.eq(MongoAnnotationProcessor.getFieldName(getField("articleId")), entityId)
                )),
                Aggregates.project(new Document("replyCount",
                        new Document("$size",
                                new Document("$filter",
                                        new Document("input", "$replies")
                                                .append("as", "r")
                                                .append("cond", new Document("$eq", List.of("$$r.depth", depth)))
                                )
                        )
                ))
        );

        AggregateIterable<Document> result = deepRepliesCollection.aggregate(pipeline);

        Document doc = result.first();
        return doc != null ? doc.getInteger("replyCount", 0) : 0L;
    }

    /**
     * @param entityId id of article
     * @param parentId parent comment id
     * @return list of deep comment associated at this article and parent comment
     */
    @Override
    public List<DeepReplies> findByParentId(String entityId, String parentId) {
        Bson filter = Filters.and(
                getFilterByArticleId(entityId),
                getFilterByParentCommentId(parentId)
        );

        FindIterable<Document> cursor = deepRepliesCollection.find(filter);

        return getDeepReplies(cursor);
    }

    /**
     * @param entityId id of article
     * @param parentId parent comment id
     * @param depth integer field that represents how far down in the reply hierarchy a comment is
     * @return list of deep comment associated at this article and parent comment in this depth
     */
    @Override
    public List<DeepReplies> findByParentId(String entityId, String parentId, Integer depth) {
        List<Bson> filters = new ArrayList<>();
        filters.add(getFilterByArticleId(entityId));
        filters.add(getFilterByParentCommentId(parentId));

        if (depth != null) {
            filters.add(getFilterByDepth(depth));
        }

        Bson filter = Filters.and(filters);

        FindIterable<Document> cursor = deepRepliesCollection.find(filter);

        return getDeepReplies(cursor);
    }

    /**
     * @param entityId id of article
     * @param parentId parent comment id
     * @param depth integer field that represents how far down in the reply hierarchy a comment is
     * @param page number of page to return
     * @param pageSize number of element to return
     * @return list of deep reply associated at this article and parent comment
     */
    @Override
    public List<DeepReplies> findByParentId(String entityId, String parentId, Integer depth, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        List<Bson> filters = new ArrayList<>();
        filters.add(getFilterByArticleId(entityId));
        filters.add(getFilterByParentCommentId(parentId));

        if (depth != null) {
            filters.add(getFilterByDepth(depth));
        }

        Bson filter = Filters.and(filters);

        FindIterable<Document> cursor = deepRepliesCollection
                .find(filter)
                .skip(skip)
                .limit(pageSize);

        return getDeepReplies(cursor);
    }

    private List<DeepComment> getReplies(Document doc) {
        if (doc == null) return List.of();

        // @SuppressWarnings("unchecked")
        List<Document> replies = (List<Document>) doc.get("replies", List.class);
        return getDeepComments(replies);
    }

    public List<DeepComment> findDeepCommentByParentId(String entityId, String parentId) {
        List<Bson> pipeline = List.of(
                Aggregates.match(Filters.and(
                        Filters.eq("_id", new ObjectId(parentId)),
                        Filters.eq(MongoAnnotationProcessor.getFieldName(getField("articleId")), entityId)
                )),
                // project only the replies array
                Aggregates.project(new Document("replies", 1).append("_id", 0))
        );

        AggregateIterable<Document> res = deepRepliesCollection.aggregate(pipeline);
        Document doc = res.first();
        return getReplies(doc);
    }

    public List<DeepComment> findDeepCommentByParentId(String entityId, String parentId, Integer depth) {
        if (depth == null) {
            return findDeepCommentByParentId(entityId, parentId);
        }

        // repliesFiltered = $filter over replies by depth
        Document filterByDepth = new Document("$filter",
                new Document("input", "$replies")
                        .append("as", "r")
                        .append("cond", new Document("$eq", List.of("$$r.depth", depth)))
        );

        List<Bson> pipeline = List.of(
                Aggregates.match(Filters.and(
                        Filters.eq("_id", new ObjectId(parentId)),
                        Filters.eq(MongoAnnotationProcessor.getFieldName(getField("articleId")), entityId)
                )),
                Aggregates.project(new Document("replies", filterByDepth).append("_id", 0))
        );

        AggregateIterable<Document> res = deepRepliesCollection.aggregate(pipeline);
        Document doc = res.first();
        return getReplies(doc);
    }

    /**
     * @param deepReplies new deep reply to save
     * @return id of new document
     */
    @Override
    public ObjectId save(DeepReplies deepReplies) {
        DateTimeInitializer.initializeTimestamps(deepReplies);

        Document document = MongoAnnotationProcessor.toDocument(deepReplies);
        InsertOneResult result = deepRepliesCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    /**
     * @param deepReplies deep reply to update
     * @return true if updated else false
     */
    @Override
    public boolean update(DeepReplies deepReplies) {
        DateTimeInitializer.updateTimestamps(deepReplies);

        Document document = MongoAnnotationProcessor.toDocument(deepReplies);
        document.remove("_id"); // Remove ID for update

        UpdateResult result = deepRepliesCollection.updateOne(
                eq("_id", deepReplies.getDeepRepliesId()),
                new Document("$set", document)
        );

        return result.getModifiedCount() > 0;
    }

    /**
     * @param id parent comment id
     * @return true if deleted else false
     */
    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = deepRepliesCollection.deleteOne(eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    public boolean updateHasMoreReplies(String entityId, String parentId, boolean hasMore, String metaUser) {
        Document filter = new Document(
                "_id",
                parentId
        ).append(
                MongoAnnotationProcessor.getFieldName(getField("articleId")),
                entityId
        );

        Document update = new Document(
                "$set",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("hasMoreReplies")),
                        hasMore
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        UpdateResult result = deepRepliesCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }
}
