package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.dao.UserDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserPassword;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.LoginHistoryView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.helpers.utils.DateTimeInitializer;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.MongoInstance;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.repository.BaseRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.Address;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.LoginHistory;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserRepository extends BaseRepository implements UserDao {

    public static UserRepository getInstance() {
        return new UserRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private final MongoCollection<Document> userCollection;
    private final Class<User> userClass = User.class;

    public UserRepository(MongoDatabase database) {
        String collectionName = MongoAnnotationProcessor.getCollectionName(userClass);
        this.userCollection = database.getCollection(collectionName);
    }


    private Field getField(String fieldName) {
        try {
            return userClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    @Override
    public Optional<User> findById(ObjectId id) {
        Document document = userCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, userClass));
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (Document document : userCollection.find()) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public List<User> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        List<User> users = new ArrayList<>();
        for (Document document : userCollection.find().skip(skip).limit(pageSize)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public long count() {
        return userCollection.countDocuments();
    }

    @Override
    public List<User> findByUsername(String username) {
        List<User> users = new ArrayList<>();
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("username")),
                username
        );

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public List<User> findByEmail(String email) {
        List<User> users = new ArrayList<>();
        Bson filter = Filters.eq(
                "email.email",
                email
        );

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public List<User> findActiveUsers() {
        List<User> users = new ArrayList<>();
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("active")),
                true
        );

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public ObjectId save(User user) {
        // Initialize timestamps before saving
        DateTimeInitializer.initializeTimestamps(user);

        Document document = MongoAnnotationProcessor.toDocument(user);
        InsertOneResult result = userCollection.insertOne(document);

        if(result.getInsertedId() == null) return null;

        return result.getInsertedId().asObjectId().getValue();
    }

    @Override
    public boolean update(User user) {
        // Update timestamps before updating
        DateTimeInitializer.updateTimestamps(user);

        Document document = MongoAnnotationProcessor.toDocument(user);
        document.remove("_id"); // Remove ID for update

        UpdateResult result = userCollection.updateOne(
                Filters.eq("_id", user.getUserId()),
                new Document("$set", document)
        );

        return result.getModifiedCount() > 0;
    }

    // Additional methods for timestamp-based queries
    public List<User> findUsersCreatedAfter(Date dateTime) {
        List<User> users = new ArrayList<>();
        Bson filter = Filters.gt(
                "created_at",
                MongoAnnotationProcessor.formatAsUTC(dateTime)
        );

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    public List<User> findUsersUpdatedBefore(Date dateTime) {
        List<User> users = new ArrayList<>();
        Bson filter = Filters.lt(
                "updated_at",
                MongoAnnotationProcessor.formatAsUTC(dateTime)
        );

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    public List<User> findRecentlyActiveUsers(int days) {
        // LocalDateTime cutoff = MongoAnnotationProcessor.getCurrentUTCDateTime().minusDays(days);
        Date cutoff = MongoAnnotationProcessor.nowMinusDays(days);
        List<User> users = new ArrayList<>();
        Bson filter = Filters.gte(
                "last_login_at",
                MongoAnnotationProcessor.formatAsUTC(cutoff)
        );

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public boolean delete(ObjectId id) {
        DeleteResult result = userCollection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userCollection.countDocuments(Filters.eq("username", username)) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userCollection.countDocuments(Filters.eq("email", email)) > 0;
    }


    // Methods for embedded document operations
    @Override
    public boolean updatePassword(String userId, UserPassword newPassword) {
        try {
            ObjectId objectId = new ObjectId(userId);
            Document passwordDoc = MongoAnnotationProcessor.toDocument(newPassword);

            UpdateResult result = userCollection.updateOne(
                    Filters.eq("_id", objectId),
                    Updates.combine(
                            Updates.set("password_info", passwordDoc),
                            Updates.set("updated_at", MongoAnnotationProcessor.getCurrentUTCDateTime().toString())
                    )
            );

            return result.getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean addLoginHistory(String userId, LoginHistoryView loginHistoryView) {
        try {
            ObjectId objectId = new ObjectId(userId);

            LoginHistory loginHistory = new LoginHistory(loginHistoryView);

            Document historyDoc = MongoAnnotationProcessor.toDocument(loginHistory);

            UpdateResult result = userCollection.updateOne(
                    Filters.eq("_id", objectId),
                    Updates.combine(
                            Updates.push("login_history", historyDoc),
                            Updates.set("last_login_at", loginHistory.getLoginTime().toString()),
                            Updates.set("updated_at", MongoAnnotationProcessor.getCurrentUTCDateTime().toString())
                    )
            );

            return result.getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean addAddress(String userId, Address address) {
        try {
            ObjectId objectId = new ObjectId(userId);
            Document addressDoc = MongoAnnotationProcessor.toDocument(address);

            // If adding a primary address, remove primary from others
            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.push("addresses", addressDoc));
            updates.add(Updates.set("updated_at", MongoAnnotationProcessor.getCurrentUTCDateTime().toString()));

            if (address.isPrimary()) {
                updates.add(Updates.set("addresses.$[].is_primary", false));
            }

            UpdateResult result = userCollection.updateOne(
                    Filters.eq("_id", objectId),
                    Updates.combine(updates)
            );

            return result.getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<User> findUsersWithExpiredPasswords() {
        String currentTime = MongoAnnotationProcessor.formatAsUTC(MongoAnnotationProcessor.getCurrentUTCDateTime());

        Bson filter = Filters.lt("password.expires_at", currentTime);
        List<User> users = new ArrayList<>();

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    public List<User> findUsersWithFailedLoginAttempts(int minAttempts) {
        Bson filter = Filters.gte("password.failed_attempts", minAttempts);
        List<User> users = new ArrayList<>();

        for (Document document : userCollection.find(filter)) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

}
