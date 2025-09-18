package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionCounts;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbedded;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbeddedList;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class EmbeddedComment extends BaseModel {

    @MongoId(generateOnCreate = true)
    private ObjectId commentId;

    @ModelField("title")
    private String title;

    @ModelField("content")
    private String content;

    @ModelField("entity_id")
    private String entityId;

    @ModelField("author_id")
    private String authorId;

    @ModelField("author_name")
    private String authorName;

    @ModelField("depth")
    private Integer depth;

    @MongoEmbedded("reactions")
    private ReactionCounts reactionCount;

    @ModelField("reply_count")
    private Integer replyCount;

    @ModelField("has_more_replies")
    private Boolean hasMoreReplies;

    @MongoEmbeddedList("replies")
    private List<EmbeddedComment> replies;

    public EmbeddedComment() {}
    
    public EmbeddedComment(String entityId, CommentRequest commentRequest) {
        this.commentId = null;

        this.entityId = entityId;

        this.content    = commentRequest.getContent();
        this.authorId   = commentRequest.getAuthorId();
        this.authorName = commentRequest.getAuthorName();

        this.depth          = 0;
        this.replyCount     = 0;
        this.hasMoreReplies = false;
        this.replies        = new ArrayList<>();

        this.reactionCount  = new ReactionCounts();
        this.replyCount     = 0;
    }
    
    public EmbeddedComment(String entityId, CommentRequest commentRequest, int depth) {
        this(entityId, commentRequest);

        this.depth = depth;
    }


    public ObjectId getCommentId() {
        return commentId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Integer getDepth() {
        return depth;
    }

    public ReactionCounts getReactionCount() {
        return reactionCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public Boolean getHasMoreReplies() {
        return hasMoreReplies;
    }

    public List<EmbeddedComment> getReplies() {
        return replies;
    }
}
