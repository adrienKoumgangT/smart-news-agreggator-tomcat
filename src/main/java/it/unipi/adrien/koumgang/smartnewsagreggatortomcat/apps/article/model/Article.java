package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.manager.nyt.model.NYTArticle;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.comment.model.EmbeddedComment;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionCounts;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbedded;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbeddedList;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MongoCollectionName("articles")
public class Article extends BaseModel {

    @MongoId
    private ObjectId articleId;

    @ModelField("article_source")
    private String articleSource;

    @ModelField("extern_article_id")
    private String externArticleId;



    @ModelField("title")
    private String title;

    @ModelField("sub_title")
    private String subTitle;

    @ModelField("description")
    private String description;

    @ModelField("content")
    private String content;

    @ModelField("source")
    private String source;

    @ModelField("author")
    private String author;

    @ModelField("web_url")
    private String webUrl;

    @ModelField("image_url")
    private String imageUrl;



    @ModelField("tags")
    private List<String> tags;

    @ModelField("publication_date")
    private Date publicationDate;



    @ModelField("reactions_count")
    private Integer reactionsCount;

    @MongoEmbedded("reaction_counts")
    private ReactionCounts reactionCounts;

    @MongoEmbeddedList("reactions")
    private List<EmbeddedReaction> reactions;



    @ModelField("comments_count")
    private Integer commentsCount;

    @ModelField("top_level_comments_count")
    private Integer topLevelCommentsCount;

    @MongoEmbeddedList("comments")
    private List<EmbeddedComment> comments;


    public Article() {
        this.reactionsCount = 0;
        this.reactionCounts = new ReactionCounts();
        this.reactions = new ArrayList<>();

        this.commentsCount = 0;
        this.topLevelCommentsCount = 0;
        this.comments = new ArrayList<>();
    }

    public Article(ArticleView article) {

    }

    public Article(NYTArticle article) {
        this();

        this.articleSource = "NYT";
        this.externArticleId = article.getArticleId();

        this.title = article.getHeadline();

        this.content = article.getSnippet();

        // this.tags = article.getKeywords().stream().map(KeywordsConverter::normalizeQuotes).toList();
        this.tags = article.getKeywords();

        this.author = article.getByline();
        this.source = article.getSource();
        this.webUrl = article.getWebUrl();

        this.publicationDate = article.getPublicationDate();

    }

    public void update(ArticleView article) {

    }

    public ObjectId getArticleId() {
        return articleId;
    }

    public void setArticleId(ObjectId articleId) {
        this.articleId = articleId;
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

    public String getAuthor() {
        return author;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public Integer getReactionsCount() {
        return reactionsCount != null ? reactionsCount : 0;
    }

    public ReactionCounts getReactionCounts() {
        return reactionCounts;
    }

    public List<EmbeddedReaction> getReactions() {
        return reactions;
    }

    public Integer getCommentsCount() {
        return commentsCount != null ? commentsCount : 0;
    }

    public Integer getTopLevelCommentsCount() {
        return topLevelCommentsCount != null ? topLevelCommentsCount : 0;
    }

    public List<EmbeddedComment> getComments() {
        return comments;
    }
}
