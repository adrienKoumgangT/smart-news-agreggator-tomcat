package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils;

import org.bson.types.ObjectId;

// Integer to ObjectId converter (example for numeric IDs)
public class IntegerIdConverter implements IdConverter<Integer> {

    @Override
    public ObjectId toObjectId(Integer id) {
        // This is just an example - you might want a different mapping strategy
        return id != null ? new ObjectId(String.format("%024d", id)) : null;
    }

    @Override
    public Integer fromObjectId(ObjectId objectId) {
        if (objectId == null) return null;
        try {
            return Integer.parseInt(objectId.toHexString(), 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean isValid(Integer id) {
        return id != null && id > 0;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

}
