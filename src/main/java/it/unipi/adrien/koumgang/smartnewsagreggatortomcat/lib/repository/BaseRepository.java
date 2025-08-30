package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.repository;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;

import java.lang.reflect.Field;

public class BaseRepository {

    private static final Class<BaseModel> mongoBaseModelClass = BaseModel.class;

    public static final String FIELD_NAME_CREATED_AT = "createdAt";
    public static final String MONGO_FIELD_NAME_CREATED_AT;
    public static final String FIELD_NAME_UPDATED_AT = "updatedAt";
    public static final String MONGO_FIELD_NAME_UPDATED_AT;

    static {
        MONGO_FIELD_NAME_CREATED_AT = MongoAnnotationProcessor.getFieldName(getFieldBase(FIELD_NAME_CREATED_AT));
        MONGO_FIELD_NAME_UPDATED_AT = MongoAnnotationProcessor.getFieldName(getFieldBase(FIELD_NAME_UPDATED_AT));
    }

    private static Field getFieldBase(String fieldName) {
        try {
            return mongoBaseModelClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

}
