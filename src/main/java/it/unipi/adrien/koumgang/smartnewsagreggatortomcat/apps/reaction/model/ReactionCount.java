package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

public class ReactionCount {

    private Integer like;

    private Integer dislike;

    private Integer love;

    private Integer laugh;

    private Integer wow;

    private Integer sad;

    private Integer angry;

    public ReactionCount() {
        this.like       = 0;
        this.dislike    = 0;
        this.love       = 0;
        this.laugh      = 0;
        this.wow        = 0;
        this.sad        = 0;
        this.angry      = 0;
    }

    public ReactionCount(ReactionCounts reaction) {
        this.like       = reaction.getLike();
        this.dislike    = reaction.getDislike();
        this.love       = reaction.getLove();
        this.laugh      = reaction.getLaugh();
        this.wow        = reaction.getWow();
        this.sad        = reaction.getSad();
        this.angry      = reaction.getAngry();
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Integer getDislike() {
        return dislike;
    }

    public void setDislike(Integer dislike) {
        this.dislike = dislike;
    }

    public Integer getLove() {
        return love;
    }

    public void setLove(Integer love) {
        this.love = love;
    }

    public Integer getLaugh() {
        return laugh;
    }

    public void setLaugh(Integer laugh) {
        this.laugh = laugh;
    }

    public Integer getWow() {
        return wow;
    }

    public void setWow(Integer wow) {
        this.wow = wow;
    }

    public Integer getSad() {
        return sad;
    }

    public void setSad(Integer sad) {
        this.sad = sad;
    }

    public Integer getAngry() {
        return angry;
    }

    public void setAngry(Integer angry) {
        this.angry = angry;
    }

    // Helper methods
    public void increment(ReactionEnum reactionType) {
        if (reactionType != null) {
            switch (reactionType) {
                case Like:      like++;     break;
                case Dislike:   dislike++;  break;
                case Love:      love++;     break;
                case Laugh:     laugh++;    break;
                case Wow:       wow++;      break;
                case Sad:       sad++;      break;
                case Angry:     angry++;    break;
            }
        }
    }

    public void decrement(ReactionEnum reactionType) {
        if (reactionType != null) {
            switch (reactionType) {
                case Like:      like    = Math.max(0, like      - 1); break;
                case Dislike:   dislike = Math.max(0, dislike   - 1); break;
                case Love:      love    = Math.max(0, love      - 1); break;
                case Laugh:     laugh   = Math.max(0, laugh     - 1); break;
                case Wow:       wow     = Math.max(0, wow       - 1); break;
                case Sad:       sad     = Math.max(0, sad       - 1); break;
                case Angry:     angry   = Math.max(0, angry     - 1); break;
            }
        }
    }
}
