package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model.Article;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.view.ReactionCountsView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleSimpleView extends BaseView {

    private String idArticle;

    @Required
    private String articleSource;

    private String externArticleId;


    @Required
    private String title;

    private String subTitle;

    private String description;

    @Required
    private String content;

    private String source;

    private String webUrl;

    private String imageUrl;

    private Date publicationDate;


    private List<String> tags;

    private ReactionCountsView reactionCount;


    private Date createdAt;

    private Date updatedAt;


    public ArticleSimpleView() {}

    public ArticleSimpleView(Article article) {
        this.idArticle = StringIdConverter.getInstance().fromObjectId(article.getArticleId());

        this.articleSource      = article.getArticleSource();
        this.externArticleId    = article.getExternArticleId();

        this.title          = article.getTitle();
        this.subTitle       = article.getSubTitle();
        this.description    = article.getDescription();
        this.content        = article.getContent();
        this.source         = article.getSource();
        this.webUrl         = article.getWebUrl();
        this.imageUrl       = article.getImageUrl();

        this.publicationDate = article.getPublicationDate();

        this.tags           = article.getTags();

        this.reactionCount = article.getReactionCounts() != null ? new ReactionCountsView(article.getReactionCounts()) : new ReactionCountsView();

        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }

    public String getIdArticle() {
        return idArticle;
    }

    public String getArticleSource() {
        return articleSource;
    }

    public String getExternArticleId() {
        return externArticleId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getSource() {
        return source;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public List<String> getTags() {
        return tags != null ? tags : new ArrayList<>();
    }

    public ReactionCountsView getReactionCount() {
        return reactionCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

}
