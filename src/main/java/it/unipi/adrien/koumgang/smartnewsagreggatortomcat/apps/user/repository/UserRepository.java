package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.dao.UserDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserEmail;
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
import java.util.*;

public class UserRepository extends BaseRepository implements UserDao {

    public static UserRepository getInstance() {
        return new UserRepository(MongoInstance.getInstance().mongoDatabase());
    }


    private final MongoCollection<Document> userCollection;
    private final Class<User> userClass = User.class;
    private final Class<UserEmail> emailClass = UserEmail.class;
    private final Class<UserPassword> userPasswordClass = UserPassword.class;
    private final Class<LoginHistory> loginHistoryClass = LoginHistory.class;
    private final Class<Address> addressClass = Address.class;

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

    private Field getFiledInEmail(String fieldName) {
        try {
            return emailClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

     private Field getFiledInUserPassword(String fieldName) {
        try {
            return userPasswordClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field not found: " + fieldName, e);
        }
    }

    private List<User> getUsers(FindIterable<Document> cursor) {
        List<User> users = new ArrayList<>();
        for (Document document : cursor) {
            users.add(MongoAnnotationProcessor.fromDocument(document, userClass));
        }
        return users;
    }

    @Override
    public Optional<User> findById(ObjectId id) {
        Document document = userCollection.find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(MongoAnnotationProcessor.fromDocument(document, userClass));
    }

    @Override
    public List<User> findAll() {
        FindIterable<Document> cursor = userCollection.find();
        return getUsers(cursor);
    }

    @Override
    public List<User> findAll(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 10;

        int skip = (page - 1) * pageSize;

        FindIterable<Document> cursor = userCollection.find().skip(skip).limit(pageSize);

        return getUsers(cursor);
    }

    @Override
    public long count() {
        return userCollection.estimatedDocumentCount();
    }

    @Override
    public List<User> findByUsername(String username) {
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("username")),
                username
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

    @Override
    public List<User> findByEmail(String email) {
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("email"))
                        + "."
                        + MongoAnnotationProcessor.getFieldName(getFiledInEmail("email")),
                email
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

    @Override
    public List<User> findByEmailNotConfirmed() {
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("email"))
                        + "."
                        + MongoAnnotationProcessor.getFieldName(getFiledInEmail("confirmed")),
                false
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

    @Override
    public List<User> findActiveUsers() {
        Bson filter = Filters.eq(
                MongoAnnotationProcessor.getFieldName(getField("active")),
                true
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
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
        Bson filter = Filters.gt(
                MONGO_FIELD_NAME_CREATED_AT,
                MongoAnnotationProcessor.formatAsUTC(dateTime)
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

    public List<User> findUsersUpdatedBefore(Date dateTime) {
        List<User> users = new ArrayList<>();
        Bson filter = Filters.lt(
                MONGO_FIELD_NAME_UPDATED_AT,
                MongoAnnotationProcessor.formatAsUTC(dateTime)
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

    public List<User> findRecentlyActiveUsers(int days) {
        Date cutoff = MongoAnnotationProcessor.nowMinusDays(days);
        Bson filter = Filters.gte(
                MongoAnnotationProcessor.getFieldName(getField("lastLoginAt")),
                MongoAnnotationProcessor.formatAsUTC(cutoff)
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
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
                            Updates.set("updated_at", MongoAnnotationProcessor.getCurrentUTCDateTime())
                    )
            );

            return result.getModifiedCount() > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean addLoginHistory(String userId, LoginHistoryView loginHistoryView, Boolean failedPassword) {
        try {
            ObjectId objectId = new ObjectId(userId);

            LoginHistory loginHistory = new LoginHistory(loginHistoryView);

            Document historyDoc = MongoAnnotationProcessor.toDocument(loginHistory);

            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.push(MongoAnnotationProcessor.getFieldName(getField("loginHistory")), historyDoc));
            updates.add(Updates.set(MongoAnnotationProcessor.getFieldName(getField("lastLoginAt")), loginHistory.getLoginTime()));
            updates.add(Updates.set(MONGO_FIELD_NAME_UPDATED_AT, MongoAnnotationProcessor.getCurrentUTCDateTime()));

            if(Objects.equals(failedPassword, true)) {
                updates.add(Updates.inc("password.failed_attempts",  1));
                updates.add(Updates.set("password.last_failed_attempt", loginHistory.getLoginTime()));
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

    public boolean addAddress(String userId, Address address) {
        try {
            ObjectId objectId = new ObjectId(userId);
            Document addressDoc = MongoAnnotationProcessor.toDocument(address);

            // If adding a primary address, remove primary from others
            List<Bson> updates = new ArrayList<>();
            updates.add(Updates.push("addresses", addressDoc));
            updates.add(Updates.set(MONGO_FIELD_NAME_UPDATED_AT, MongoAnnotationProcessor.getCurrentUTCDateTime()));

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

        Bson filter = Filters.lt(
                MongoAnnotationProcessor.getFieldName(getField("password"))
                        + "."
                        + MongoAnnotationProcessor.getFieldName(getFiledInUserPassword("expiresAt")),
                currentTime
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

    public List<User> findUsersWithFailedLoginAttempts(int minAttempts) {
        Bson filter = Filters.gte(
                MongoAnnotationProcessor.getFieldName(getField("password"))
                        + "."
                        + MongoAnnotationProcessor.getFieldName(getFiledInUserPassword("failedAttempts")),
                minAttempts
        );

        FindIterable<Document> cursor = userCollection.find(filter);

        return getUsers(cursor);
    }

}
