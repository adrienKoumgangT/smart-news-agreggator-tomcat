package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view.MetaDataSimpleView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.metadata.view.MetaDataView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

@MongoCollectionName("metadata")
public class MetaData {

    @MongoId
    private ObjectId metadataId;

    @ModelField("meta_type")
    private String metaType;

    @ModelField("name")
    private String name;

    @ModelField("description")
    private String description;

    @ModelField("color_light")
    private String colorLight;

    @ModelField("color_dark")
    private String colorDark;

    @ModelField("is_default")
    private Boolean isDefault;

    public MetaData() {}

    public MetaData(MetaDataSimpleView metaData) {
        this.metadataId = StringIdConverter.getInstance().toObjectId(metaData.getIdMetaData());

        this.name           = metaData.getName();
        this.description    = metaData.getDescription();
        this.colorLight     = metaData.getColorLight();
        this.colorDark      = metaData.getColorDark();
        this.isDefault      = metaData.getIsDefault();
    }

    public MetaData(MetaDataSimpleView metaData, String metaType) {
        this(metaData);

        this.metaType = metaType;
    }

    public MetaData(MetaDataView metaData) {
        this((MetaDataSimpleView) metaData);

        this.metaType    = metaData.getMetaType();
    }

    public void update(MetaDataView metaData) {
        this.name           = metaData.getName();
        this.description    = metaData.getDescription();
        this.colorLight     = metaData.getColorLight();
        this.colorDark      = metaData.getColorDark();
        this.isDefault      = metaData.getIsDefault();
    }

    public ObjectId getMetadataId() {
        return metadataId;
    }

    public String getMetaType() {
        return metaType;
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
