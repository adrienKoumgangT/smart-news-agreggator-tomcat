package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.dao.ArticleDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.EmbeddedComment;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionEnum;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils.DateTimeInitializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.repository.BaseRepository;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ArticleRepository extends BaseRepository implements ArticleDao {

    public static ArticleRepository getInstance() {
        return new ArticleRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private final MongoCollection<Document> articleCollection;
    private final Class<Article> articleClass = Article.class;
    private final Class<EmbeddedComment> embeddedCommentClass = EmbeddedComment.class;

    public ArticleRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(articleClass);
        this.articleCollection = database.getCollection(collectionName);
    }

    private Field getField(String fieldName) {
        try {
            return articleClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    private Field getFieldInComment(String fieldName) {
        try {
            return embeddedCommentClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    @Override
    public Optional<Article> findById(ObjectId id) {
        Document document = articleCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, articleClass));
    }

    /** Retrieve all distinct tags across all articles */
    @Override
    public List<String> findAllDistinctTags() {
        List<String> tags = new ArrayList<>();
        articleCollection.distinct("tags", String.class).into(tags);
        return tags;
    }

    /**
     * Retrieve all article IDs where the given tag is in the tags array.
     */
    @Override
    public List<String> findIdsByTag(String tag) {
        List<String> ids = new ArrayList<>();

        // Query: { tags: "tagValue" }
        for (Document doc : articleCollection.find(new Document("tags", tag))
                .projection(new Document("_id", 1))) {
            ids.add(doc.getObjectId("_id").toHexString());
        }

        return ids;
    }

    /**
     * Retrieve all article IDs where the tags array contains all given tags.
     */
    @Override
    public List<String> findIdsByTags(List<String> tags) {
        List<String> ids = new ArrayList<>();

        // Query: { tags: { $all: [tag1, tag2, ...] } }
        for (Document doc : articleCollection.find(new Document("tags", new Document("$all", tags)))
                .projection(new Document("_id", 1))) {
            ids.add(doc.getObjectId("_id").toHexString());
        }

        return ids;
    }

    /**
     * Retrieve all article IDs where the tags array contains all given tags.
     */
    @Override
    public List<String> findIdsByTags(List<String> tags, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = articleCollection
                .find(new Document("tags", new Document("$all", tags)))
                .projection(new Document("_id", 1)).skip(skip)
                .limit(pageSize);

        List<String> ids = new ArrayList<>();

        // Query: { tags: { $all: [tag1, tag2, ...] } }
        for (Document doc : cursor) {
            ids.add(doc.getObjectId("_id").toHexString());
        }

        return ids;
    }


    @Override
    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        for(Document document : articleCollection.find()) {
            articles.add(MongoAnnotationProcessor.fromDocument(document, articleClass));
        }
        return articles;
    }

    /**
     * @return list of all article IDs
     */
    @Override
    public List<String> findAllIds() {
        List<String> ids = new ArrayList<>();
        // Only project the _id field
        for (Document document : articleCollection.find().projection(new Document("_id", 1))) {
            ids.add(StringIdConverter.getInstance().fromObjectId((ObjectId) document.get("_id")));
        }
        return ids;
    }


    @Override
    public List<Article> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = articleCollection.find()
                .skip(skip)
                .limit(pageSize);

        List<Article> articles = new ArrayList<>();
        for (Document document : cursor) {
            articles.add(MongoAnnotationProcessor.fromDocument(document, articleClass));
        }

        return articles;
    }

    @Override
    public List<String> findAllIds(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        List<String> ids = new ArrayList<>();
        FindIterable<Document> cursor = articleCollection.find()
                .projection(new Document("_id", 1))
                .skip(skip)
                .limit(pageSize);

        for (Document document : cursor) {
            ids.add(StringIdConverter.getInstance().fromObjectId((ObjectId) document.get("_id")));
        }

        return ids;
    }


    @Override
    public long count() {
        return articleCollection.estimatedDocumentCount();
    }


    @Override
    public ObjectId save(Article article) {
        DateTimeInitializer.initializeTimestamps(article);

        Document document = MongoAnnotationProcessor.toDocument(article);
        InsertOneResult result = articleCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }


    @Override
    public boolean update(Article article) {
        DateTimeInitializer.updateTimestamps(article);

        Document document = MongoAnnotationProcessor.toDocument(article);
        document.remove("_id"); // Remove ID for update

        UpdateResult result = articleCollection.updateOne(
                Filters.eq("_id", article.getArticleId()),
                new Document("$set", document)
        );

        return result.getModifiedCount() > 0;
    }


    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = articleCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }


    @Override
    public boolean addTopLeveComment(final String articleId, EmbeddedComment newComment, final String metaUser) {
        Document newCommentDoc = MongoAnnotationProcessor.toDocument(newComment);

        // Query filter
        Document filter = new Document("_id", new ObjectId(articleId)); // filter by article id

        Document update = new Document(
                "$push", // append to the comments array
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("comments")),
                        newCommentDoc
                )
        ).append(
                "$inc",  // increment numeric fields
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("commentsCount")),
                        1
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("topLevelCommentsCount")),
                        1
                )
        ).append(
                "$set", // set the updated timestamp
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        // Apply update
        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }


    @Override
    public boolean addEmbeddedReply(final String articleId, EmbeddedComment newReply, final String arrayPath, final String metaUser) {
        Document filter = new Document("_id", new ObjectId(articleId));

        Document newReplyDoc = MongoAnnotationProcessor.toDocument(newReply);

        Document update = new Document(
                "$push",
                new Document(
                        arrayPath + "." + MongoAnnotationProcessor.getFieldName(getFieldInComment("replies")),
                        newReplyDoc
                )
        ).append(
                "$inc",
                new Document(
                        arrayPath + "." + MongoAnnotationProcessor.getFieldName(getFieldInComment("replyCount")),
                        1
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("commentsCount")),
                        1
                )
        ).append(
                "$set",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }

    public boolean decTopLevelCommentsCount(final String articleId, final String path, final String metaUser) {
        return incTopLevelCommentsCount(articleId, path, -1, metaUser);
    }

    public boolean incTopLevelCommentsCount(final String articleId, final String path, final String metaUser) {
        return incTopLevelCommentsCount(articleId, path, 1, metaUser);
    }

    @Override
    public boolean incTopLevelCommentsCount(final String articleId, final String path, final int inc, final String metaUser) {
        if (path == null) return false;

        Document filter = new Document("_id", new ObjectId(articleId));

        // $unset expects any value ("" is common)
        Document update = new Document(
                "$unset", new Document(path, "")
        ).append(
                "$inc",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("commentsCount")),
                        inc
                )
        ).append(
                "$set",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        // If it's a top-level comment, also inc/decr topLevelCommentsCount
        if (path.startsWith("comments.")) {
            ((Document) update.get("$inc"))
                    .append(
                            MongoAnnotationProcessor.getFieldName(getField("topLevelCommentsCount")),
                            inc
                    );
        }

        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }


    @Override
    public boolean updateHasMoreReplies(final String articleId, final String path, final boolean hasMoreReplies, final String metaUser) {
        if (path == null) return false;

        Document filter = new Document("_id", new ObjectId(articleId));

        Document update = new Document(
                "$set",
                new Document(
                        path + "." + MongoAnnotationProcessor.getFieldName(getFieldInComment("hasMoreReplies")),
                        hasMoreReplies
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean incCommentsCount(final String articleId, final int inc, final String metaUser) {
        Document filter = new Document("_id", new ObjectId(articleId));

        Document update = new Document(
                "$inc",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("commentsCount")),
                        inc
                )
        ).append(
                "$set",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }


    private String getReactionPath(ReactionEnum reactionEnum) {
        return switch (reactionEnum) {
            case Like -> "like";
            case Dislike -> "dislike";
            case Love -> "love";
            case Laugh -> "laugh";
            case Wow -> "wow";
            case Sad -> "sad";
            case Angry -> "angry";
            case null -> "unknow";
        };
    }



    @Override
    public boolean addEmbeddedReaction(String articleId, EmbeddedReaction newReaction, String metaUser) {
        Document filter = new Document("_id", new ObjectId(articleId));

        Document newReactionDoc = MongoAnnotationProcessor.toDocument(newReaction);

        Document update = new Document(
                "$push",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getFieldInComment("reactions")),
                        newReactionDoc
                )
        ).append(
                "$inc",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getFieldInComment("reactionsCount")),
                        1
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("reactionCounts")) + "." + getReactionPath(newReaction.getReactionType()),
                        1
                )
        ).append(
                "$set",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean incReactionsCount(String articleId, ReactionEnum reactionEnum, int inc, String metaUser) {
        Document filter = new Document("_id", new ObjectId(articleId));

        Document update = new Document(
                "$inc",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getFieldInComment("reactionsCount")),
                        inc
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("reactionCounts")) + "." + getReactionPath(reactionEnum),
                        inc
                )
        ).append(
                "$set",
                new Document(
                        MongoAnnotationProcessor.getFieldName(getField("updatedAt")),
                        new Date()
                ).append(
                        MongoAnnotationProcessor.getFieldName(getField("updatedBy")),
                        metaUser
                )
        );

        UpdateResult result = articleCollection.updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }



}
