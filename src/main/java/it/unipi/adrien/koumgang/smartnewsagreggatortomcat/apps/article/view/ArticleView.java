package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.view.EmbeddedCommentView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.view.EmbeddedReactionView;

import java.util.ArrayList;
import java.util.List;

public class ArticleView extends ArticleSimpleView {

    private List<EmbeddedCommentView> comments;

    private List<EmbeddedReactionView> reactions;


    public ArticleView() {
        super();
    }

    public ArticleView(Article article) {
        super(article);

        if(article.getComments() != null) {
            this.comments = article.getComments().stream().map(EmbeddedCommentView::new).toList();
        } else {
            this.comments = new ArrayList<>();
        }

        if(article.getReactions() != null) {
            this.reactions = article.getReactions().stream().map(EmbeddedReactionView::new).toList();
        }  else {
            this.reactions = new ArrayList<>();
        }
    }

    public List<EmbeddedCommentView> getComments() {
        return comments;
    }

    public List<EmbeddedReactionView> getReactions() {
        return reactions;
    }
}
