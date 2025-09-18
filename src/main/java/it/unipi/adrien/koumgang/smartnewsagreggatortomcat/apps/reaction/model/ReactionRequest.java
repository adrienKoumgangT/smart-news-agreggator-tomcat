package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

public class ReactionRequest {

    private ReactionEnum reactionType;

    private String authorId;

    private String authorName;

    private String entityId;

    public ReactionRequest() {}

    public ReactionEnum getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionEnum reactionType) {
        this.reactionType = reactionType;
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


    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
