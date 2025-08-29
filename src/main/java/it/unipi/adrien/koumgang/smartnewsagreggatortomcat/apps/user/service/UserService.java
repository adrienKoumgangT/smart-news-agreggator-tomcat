package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.dao.UserDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserPassword;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.repository.UserRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordHasher;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.LoginHistory;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> getUserById(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return Optional.empty();
        }
        try {
            ObjectId objectId = new ObjectId(id);
            return userDao.findById(objectId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean updateUser(String id, User userDetails) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }
        try {
            ObjectId objectId = new ObjectId(id);
            Optional<User> existingUser = userDao.findById(objectId);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                // TODO: Update fields...
                return userDao.update(user);
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean deleteUser(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }
        try {
            ObjectId objectId = new ObjectId(id);
            return userDao.delete(objectId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        try {
            ObjectId objectId = new ObjectId(userId);
            Optional<User> userOpt = userDao.findById(objectId);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (user.verifyPassword(currentPassword)) {
                    UserPassword newUserPassword = new UserPassword(PasswordHasher.hashPassword(newPassword));
                    return ((UserRepository) userDao).updatePassword(userId, newUserPassword);
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean recordLogin(String userId, String ipAddress, String userAgent) {
        try {
            ObjectId objectId = new ObjectId(userId);
            Optional<User> userOpt = userDao.findById(objectId);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.recordLogin();

                LoginHistory loginHistory = new LoginHistory(
                        new Date(),
                        "success",
                        ipAddress,
                        userAgent
                );

                return ((UserRepository) userDao).addLoginHistory(userId, loginHistory);
            }
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<User> getUsersWithExpiredPasswords() {
        return ((UserRepository) userDao).findUsersWithExpiredPasswords();
    }

    public List<User> getUsersWithSuspiciousActivity() {
        return ((UserRepository) userDao).findUsersWithFailedLoginAttempts(3);
    }
}
