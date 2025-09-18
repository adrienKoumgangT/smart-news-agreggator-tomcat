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

public class DeepComment extends BaseModel {

    @MongoId(generateOnCreate = true)
    private ObjectId deepCommentId;

    @ModelField("title")
    private String title;

    @ModelField("content")
    private String content;

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
    private List<DeepComment> replies;

    public DeepComment() {}

    public DeepComment(CommentRequest commentRequest) {
        this.deepCommentId = null;

        this.content    = commentRequest.getContent();
        this.authorId   = commentRequest.getAuthorId();
        this.authorName = commentRequest.getAuthorName();
        this.depth      = 0;

        this.reactionCount  = new ReactionCounts();
        this.replyCount     = 0;
        this.hasMoreReplies = false;
        this.replies = new ArrayList<>();
    }

    public DeepComment(CommentRequest commentRequest, Integer depth) {
        this(commentRequest);

        this.depth      = depth;
    }

    public ObjectId getDeepCommentId() {
        return deepCommentId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public ReactionCounts getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(ReactionCounts reactionCount) {
        this.reactionCount = reactionCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }

    public Boolean getHasMoreReplies() {
        return hasMoreReplies;
    }

    public void setHasMoreReplies(Boolean hasMoreReplies) {
        this.hasMoreReplies = hasMoreReplies;
    }

    public List<DeepComment> getReplies() {
        return replies;
    }

    public void setReplies(List<DeepComment> replies) {
        this.replies = replies;
    }
}
