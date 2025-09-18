package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbeddedList;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@MongoCollectionName("deep-replies")
public class DeepReplies extends BaseModel {

    @MongoId
    private ObjectId deepRepliesId;


    @ModelField("article_id")
    private String articleId;

    @ModelField("root_comment_id")
    private String rootCommentId;

    @ModelField("parent_reply_id")
    private String parentReplyId;


    @ModelField("depth")
    private Integer depth;

    @ModelField("reply_count")
    private Integer replyCount;

    @ModelField("has_more_replies")
    private Boolean hasMoreReplies;

    @MongoEmbeddedList("replies")
    private List<DeepComment> replies;

    public DeepReplies() {}

    public DeepReplies(String articleId, String parentReplyId, int depth) {
        this.articleId = articleId;
        this.parentReplyId = parentReplyId;
        this.depth = depth;
        this.replyCount = 0;
        this.hasMoreReplies = false;
        this.replies = new ArrayList<>();
    }

    public ObjectId getDeepRepliesId() {
        return deepRepliesId;
    }

    public void setDeepRepliesId(ObjectId deepRepliesId) {
        this.deepRepliesId = deepRepliesId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getRootCommentId() {
        return rootCommentId;
    }

    public void setRootCommentId(String rootCommentId) {
        this.rootCommentId = rootCommentId;
    }

    public String getParentReplyId() {
        return parentReplyId;
    }

    public void setParentReplyId(String parentReplyId) {
        this.parentReplyId = parentReplyId;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
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
