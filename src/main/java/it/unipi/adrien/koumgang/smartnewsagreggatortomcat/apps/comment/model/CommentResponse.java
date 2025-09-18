package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentResponse {

    private int totalComments;
    private int topLevelComments;
    private List<Comment> comments;
    private int page;
    private int size;
    private boolean hasMore;

    // Constructors
    public CommentResponse() {}

    public CommentResponse(@NotNull Article article) {
        this.totalComments = article.getCommentsCount();
        this.topLevelComments = article.getTopLevelCommentsCount();
    }

    public CommentResponse(
            int totalComments,
            int topLevelComments,
            List<Comment> comments,
            int page,
            int size,
            boolean hasMore
    ) {
        this.totalComments = totalComments;
        this.topLevelComments = topLevelComments;
        this.comments = comments;
        this.page = page;
        this.size = size;
        this.hasMore = hasMore;
    }


    public int getTotalComments() { return totalComments; }
    public void setTotalComments(int totalComments) { this.totalComments = totalComments; }

    public int getTopLevelComments() { return topLevelComments; }
    public void setTopLevelComments(int topLevelComments) { this.topLevelComments = topLevelComments; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }
}
