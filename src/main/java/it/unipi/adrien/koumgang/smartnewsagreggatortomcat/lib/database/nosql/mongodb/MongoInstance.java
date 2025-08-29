package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb;

public class MongoInstance {

    private static volatile MongoProvider instance;

    /** Thread-safe lazy singleton */
    public static MongoProvider getInstance() {
        MongoProvider local = instance;
        if (local == null) {
            synchronized (MongoProvider.class) {
                local = instance;
                if (local == null) {
                    instance = local = new MongoProvider();
                }
            }
        }
        return local;
    }


}