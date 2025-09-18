package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserReactionsResponse {

    private List<UserReaction> reactions;
    private int totalReactions;
    private int page;
    private int size;
    private boolean hasMore;
    private ReactionCount reactionCount;

    public UserReactionsResponse() {}

    public UserReactionsResponse(@NotNull Article article) {
        this.totalReactions = article.getReactionsCount();
    }

    public List<UserReaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<UserReaction> reactions) {
        this.reactions = reactions;
    }

    public int getTotalReactions() {
        return totalReactions;
    }

    public void setTotalReactions(int totalReactions) {
        this.totalReactions = totalReactions;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public ReactionCount getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(ReactionCount reactionCount) {
        this.reactionCount = reactionCount;
    }


    private ReactionCount calculateReactionCounts() {
        ReactionCount reactionCounts = new ReactionCount();
        if(reactions != null) {
            for(UserReaction reaction : reactions) {
                reactionCounts.increment(reaction.getReactionType());
            }
        }

        return reactionCounts;
    }

}
