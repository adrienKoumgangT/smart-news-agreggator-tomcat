package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.view.ReactionCountsView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.EmbeddedComment;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;

import java.util.List;

public class EmbeddedCommentView extends BaseView {

    private String idComment;

    private String title;

    private String content;

    private String authorId;

    private String authorName;

    private Integer depth;

    private ReactionCountsView reactionCount;

    private Integer replyCount;

    private Boolean hasMoreReplies;

    private List<EmbeddedCommentView> replies;


    public EmbeddedCommentView() {}

    public EmbeddedCommentView(EmbeddedComment comment) {
        this.idComment = StringIdConverter.getInstance().fromObjectId(comment.getCommentId());

        this.title      = comment.getTitle();
        this.content    = comment.getContent();
        this.authorId   = comment.getAuthorId();
        this.authorName = comment.getAuthorName();

        this.depth          = comment.getDepth();
        this.reactionCount  = comment.getReactionCount() != null ? new ReactionCountsView(comment.getReactionCount()) : new ReactionCountsView();
        this.replyCount     = comment.getReplyCount();
        this.hasMoreReplies = comment.getHasMoreReplies() != null && comment.getHasMoreReplies();
    }

    public String getIdComment() {
        return idComment;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Integer getDepth() {
        return depth;
    }

    public ReactionCountsView getReactionCount() {
        return reactionCount;
    }

    public Integer getReplyCount() {
        return replyCount;
    }

    public Boolean getHasMoreReplies() {
        return hasMoreReplies;
    }
}
