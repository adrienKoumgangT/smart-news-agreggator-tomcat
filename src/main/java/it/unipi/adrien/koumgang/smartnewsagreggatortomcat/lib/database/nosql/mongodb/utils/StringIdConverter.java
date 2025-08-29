package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils;

import org.bson.types.ObjectId;

// String to ObjectId converter
public class StringIdConverter implements IdConverter<String> {

    private static final StringIdConverter INSTANCE = new StringIdConverter();

    public static StringIdConverter getInstance() {
        return INSTANCE;
    }

    /**
     * Return null if id is null, empty and invalid ObjectId, else objectId from id
     * */
    @Override
    public ObjectId toObjectId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        if(!isValid(id)) return null;
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid ObjectId format: " + id, e);
        }
    }

    @Override
    public String fromObjectId(ObjectId objectId) {
        return objectId != null ? objectId.toHexString() : null;
    }

    @Override
    public boolean isValid(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            new ObjectId(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

}
