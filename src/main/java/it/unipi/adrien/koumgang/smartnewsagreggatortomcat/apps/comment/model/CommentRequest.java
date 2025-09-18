package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model;

public class CommentRequest {

    private String content;

    private String authorId;

    private String authorName;

    private String parentCommentId; // null for top-level comments

    public CommentRequest() {}

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

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
