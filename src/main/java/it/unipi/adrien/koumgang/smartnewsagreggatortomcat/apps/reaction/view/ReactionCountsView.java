package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionCounts;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;

public class ReactionCountsView extends BaseView {

    private Integer like;

    private Integer dislike;

    private Integer love;

    private Integer laugh;

    private Integer wow;

    private Integer sad;

    private Integer angry;

    public ReactionCountsView() {
        this.like       = 0;
        this.dislike    = 0;
        this.love       = 0;
        this.laugh      = 0;
        this.wow        = 0;
        this.sad        = 0;
        this.angry      = 0;
    }

    public ReactionCountsView(ReactionCounts reactionCounts) {
        this.like       = reactionCounts.getLike();
        this.dislike    = reactionCounts.getDislike();
        this.love       = reactionCounts.getLove();
        this.laugh      = reactionCounts.getLaugh();
        this.wow        = reactionCounts.getWow();
        this.sad        = reactionCounts.getSad();
        this.angry      = reactionCounts.getAngry();
    }

    public Integer getLike() {
        return like;
    }

    public Integer getDislike() {
        return dislike;
    }

    public Integer getLove() {
        return love;
    }

    public Integer getLaugh() {
        return laugh;
    }

    public Integer getWow() {
        return wow;
    }

    public Integer getSad() {
        return sad;
    }

    public Integer getAngry() {
        return angry;
    }
}
