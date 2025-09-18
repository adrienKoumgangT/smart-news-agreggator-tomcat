package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view.MetaDataSimpleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class ArticleSourceView extends BaseView {

    private String idArticleSource;

    @Required
    private String name;

    private String description;

    public ArticleSourceView() {}


    public ArticleSourceView(String idArticleSource, String name, String description) {
        this.idArticleSource = idArticleSource;
        this.name = name;
        this.description = description;
    }

    public ArticleSourceView(MetaDataSimpleView metadata) {
        this.idArticleSource = metadata.getIdMetaData();

        this.name           = metadata.getName();
        this.description    = metadata.getDescription();
    }

    public String getIdArticleSource() {
        return idArticleSource;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
