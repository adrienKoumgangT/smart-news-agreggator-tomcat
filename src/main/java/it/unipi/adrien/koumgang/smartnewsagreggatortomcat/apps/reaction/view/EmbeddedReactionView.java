package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionEnum;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;

public class EmbeddedReactionView extends BaseView {

    private String idReaction;

    private String authorId;

    private String authorName;

    private ReactionEnum reactionType;

    public EmbeddedReactionView() {}

    public EmbeddedReactionView(EmbeddedReaction embeddedReaction) {
        this.idReaction = StringIdConverter.getInstance().fromObjectId(embeddedReaction.getReactionId());

        this.authorId       = embeddedReaction.getAuthorId();
        this.authorName     = embeddedReaction.getAuthorName();
        this.reactionType   = embeddedReaction.getReactionType();
    }

    public String getIdReaction() {
        return idReaction;
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
}
