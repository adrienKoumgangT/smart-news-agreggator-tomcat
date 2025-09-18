package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.article.view.ArticleSourceView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.model.MetaData;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;

public class MetaDataSimpleView extends BaseView {

    private String idMetaData;

    private String name;

    private String description;

    private String colorLight;

    private String colorDark;

    private Boolean isDefault;

    public MetaDataSimpleView() {}

    public MetaDataSimpleView(MetaData metaData) {
        this.idMetaData = StringIdConverter.getInstance().fromObjectId(metaData.getMetadataId());

        this.name           = metaData.getName();
        this.description    = metaData.getDescription();
        this.colorLight     = metaData.getColorLight();
        this.colorDark      = metaData.getColorDark();
        this.isDefault      = metaData.getIsDefault();
    }

    public MetaDataSimpleView(MetaDataSimpleView metaData) {
        this.idMetaData = metaData.getIdMetaData();

        this.name           = metaData.getName();
        this.description    = metaData.getDescription();
        this.colorLight     = metaData.getColorLight();
        this.colorDark      = metaData.getColorDark();
        this.isDefault      = metaData.getIsDefault();
    }

    public MetaDataSimpleView(ArticleSourceView articleSource) {
        this.idMetaData = articleSource.getIdArticleSource();

        this.name           = articleSource.getName();
        this.description    = articleSource.getDescription();
    }

    public String getIdMetaData() {
        return idMetaData;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColorLight() {
        return colorLight;
    }

    public String getColorDark() {
        return colorDark;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }
}
