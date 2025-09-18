package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionCounts;

import java.util.Date;
import java.util.List;

public class Comment {

    private String idComment;
    private String title;
    private String content;
    private String authorId;
    private String authorName;

    private Date createdAt;

    private Integer depth;
    private Integer replyCount;
    private ReactionCounts reactions;
    private List<Comment> replies;

    public Comment() {}

    public Comment(EmbeddedComment comment) {
        this.title      = comment.getTitle();
        this.content    = comment.getContent();
        this.authorId   = comment.getAuthorId();
        this.authorName = comment.getAuthorName();

        this.createdAt = comment.getCreatedAt();

        this.depth      = comment.getDepth();
        this.replyCount = comment.getReplyCount();
    }

    public Comment(DeepComment comment) {
        this.title      = comment.getTitle();
        this.content    = comment.getContent();
        this.authorId   = comment.getAuthorId();
        this.authorName = comment.getAuthorName();

        this.createdAt = comment.getCreatedAt();

        this.depth      = comment.getDepth();
        this.replyCount = comment.getReplyCount();
    }

    public String getIdComment() {
        return idComment;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Integer getDepth() {
        return depth;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public ReactionCounts getReactions() {
        return reactions;
    }

    public List<Comment> getReplies() {
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

}
