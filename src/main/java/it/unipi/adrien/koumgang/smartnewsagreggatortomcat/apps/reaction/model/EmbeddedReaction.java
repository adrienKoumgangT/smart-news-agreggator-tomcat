package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

public class EmbeddedReaction extends BaseModel {

    @MongoId(generateOnCreate = true)
    private ObjectId reactionId;

    @ModelField("entity_id")
    private String entityId;

    @ModelField("author_id")
    private String authorId;

    @ModelField("author_name")
    private String authorName;

    @ModelField("reaction_type")
    private ReactionEnum reactionType;

    public EmbeddedReaction() {}

    public EmbeddedReaction(String entityId, ReactionRequest reactionRequest) {
        this.reactionId = null;

        this.entityId = entityId;

        this.authorId       = reactionRequest.getAuthorId();
        this.authorName     = reactionRequest.getAuthorName();
        this.reactionType   = reactionRequest.getReactionType();
    }

    public ObjectId getReactionId() {
        return reactionId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public ReactionEnum getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionEnum reactionType) {
        this.reactionType = reactionType;
    }
}
