package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;

public class UserReaction {

    private String idReaction;

    private String idAuthor;

    private String authorName;

    private ReactionEnum reactionType;

    public UserReaction() {}

    public UserReaction(EmbeddedReaction reaction) {
        this.idReaction = StringIdConverter.getInstance().fromObjectId(reaction.getReactionId());

        this.idAuthor       = reaction.getAuthorId();
        this.authorName     = reaction.getAuthorName();
        this.reactionType   = reaction.getReactionType();
    }

    public UserReaction(
            String idReaction,
            String idAuthor,
            String authorName,
            ReactionEnum reactionType
    ) {
        this.idReaction     = idReaction;
        this.idAuthor       = idAuthor;
        this.authorName     = authorName;
        this.reactionType   = reactionType;
    }

    public String getIdReaction() {
        return idReaction;
    }

    public void setIdReaction(String idReaction) {
        this.idReaction = idReaction;
    }

    public String getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(String idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public ReactionEnum getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionEnum reactionType) {
        this.reactionType = reactionType;
    }
}
