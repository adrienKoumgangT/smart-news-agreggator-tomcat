package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils;

import org.bson.types.ObjectId;

public interface IdConverter<T> {

    ObjectId toObjectId(T id);
    T fromObjectId(ObjectId objectId);
    boolean isValid(T id);

    // Add a method to get the converter class for type safety
    Class<T> getType();

}

