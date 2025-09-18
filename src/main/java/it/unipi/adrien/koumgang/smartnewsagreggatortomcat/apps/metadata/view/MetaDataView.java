package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleSourceView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.model.MetaData;

public class MetaDataView extends MetaDataSimpleView {

    private String metaType;

    public MetaDataView() {}


    public MetaDataView(MetaDataSimpleView  metaData) {
        super(metaData);
    }

    public MetaDataView(MetaDataSimpleView  metaData, String metaType) {
        super(metaData);

        this.metaType = metaType;
    }

    public MetaDataView(MetaData metaData) {
        super(metaData);

        this.metaType = metaData.getMetaType();
    }

    public MetaDataView(ArticleSourceView articleSource, String metaType) {
        super(articleSource);

        this.metaType = metaType;
    }

    public String getMetaType() {
        return metaType;
    }
}
