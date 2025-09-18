package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.PaginationView;

import java.util.List;

public class ListArticleView extends BaseView {

    private List<ArticleView> articles;

    private PaginationView pagination;

    public ListArticleView() {}

    public ListArticleView(List<ArticleView> articles, PaginationView pagination) {
        this.articles = articles;
        this.pagination = pagination;
    }

    public List<ArticleView> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleView> articles) {
        this.articles = articles;
    }

    public PaginationView getPagination() {
        return pagination;
    }

    public void setPagination(PaginationView pagination) {
        this.pagination = pagination;
    }
}
