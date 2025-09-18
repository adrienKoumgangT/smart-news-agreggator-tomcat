package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.EntityTypeEnum;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbeddedList;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@MongoCollectionName("user-reactions")
public class UserReactions extends BaseModel {

    @MongoId
    private ObjectId userReactionsId;

    @ModelField("entity_type")
    private EntityTypeEnum entityType;

    @ModelField("article_id")
    private String articleId;

    @ModelField("entity_id")
    private String entityId;

    @MongoEmbeddedList("reactions")
    private List<EmbeddedReaction> reactions;

    @ModelField("reaction_count")
    private Integer reactionCount;

    public UserReactions() {
        this.reactions = new ArrayList<>();
        this.reactionCount = 0;
    }

    public UserReactions(String entityType, String articleId, String entityId) {
        this();

        this.entityType = EntityTypeEnum.fromName(entityType);
        this.articleId  = articleId;
        this.entityId   = entityId;
    }

    public ObjectId getUserReactionsId() {
        return userReactionsId;
    }

    public EntityTypeEnum getEntityType() {
        return entityType;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getEntityId() {
        return entityId;
    }

    public List<EmbeddedReaction> getReactions() {
        return reactions;
    }

    public Integer getReactionCount() {
        return reactionCount;
    }



    public void addReaction(EmbeddedReaction reaction) {
        this.reactions.add(reaction);
        this.reactionCount++;
    }

    public boolean removeReaction(String authorId) {
        boolean removed = this.reactions.removeIf(reaction -> Objects.equals(authorId, reaction.getAuthorId()));
        if (removed) {
            this.reactionCount--;
        }
        return removed;
    }

    public boolean updateReaction(String authorId, String reactionType) {
        for (EmbeddedReaction reaction : this.reactions) {
            if (reaction.getAuthorId().equals(authorId)) {
                reaction.setReactionType(ReactionEnum.fromName(reactionType));
                reaction.setUpdatedAt(new Date());
            }
            return  true;
        }
        return false;
    }

}
