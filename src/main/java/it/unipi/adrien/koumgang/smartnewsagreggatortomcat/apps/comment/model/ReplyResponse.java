package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model;


import java.util.List;

public class ReplyResponse {
    private List<Comment> replies;
    private int totalReplies;
    private int page;
    private int size;
    private boolean hasMore;

    // Constructors
    public ReplyResponse() {}

    public ReplyResponse(List<Comment> replies, int totalReplies, int page, int size, boolean hasMore) {
        this.replies = replies;
        this.totalReplies = totalReplies;
        this.page = page;
        this.size = size;
        this.hasMore = hasMore;
    }

    // Getters and setters
    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }

    public int getTotalReplies() { return totalReplies; }
    public void setTotalReplies(int totalReplies) { this.totalReplies = totalReplies; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public boolean isHasMore() { return hasMore; }
    public void setHasMore(boolean hasMore) { this.hasMore = hasMore; }
}
