package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.dao.UserDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserPassword;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.repository.UserRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.LoginHistoryView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserMeView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserStatusView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view.UserView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.password.PasswordHasher;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class UserService {

    public static UserService getInstance() {
        return new UserService(UserRepository.getInstance());
    }


    private static final Gson gson = new GsonBuilder().serializeNulls().create();


    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserView getUserById(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return null;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [GET] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<User> optUser = userDao.findById(objectId);

            if(optUser.isEmpty()) {
                timePrinter.missing("User not found");
                return null;
            }

            UserView userView = new UserView(optUser.get());

            timePrinter.log();

            return userView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return null;
        }
    }

    public List<UserView> listUsers(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [LIST] ");

        List<User> users = userDao.findAll();

        List<UserView> userViews = users.stream().map(UserView::new).toList();

        timePrinter.log();

        return userViews;
    }

    public List<UserView> listUsers(UserToken userToken, int page, int pageSize) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [LIST] page: " + page + ", size: " + pageSize);

        List<User> users = userDao.findAll(page, pageSize);

        List<UserView> userViews = users.stream().map(UserView::new).toList();

        timePrinter.log();

        return userViews;
    }

    public long count(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [COUNT] ");

        long count = userDao.count();

        timePrinter.log();

        return count;
    }

    public Optional<User> getUserByUsername(String username) {

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [GET] username: " + username);

        try {
            List<User> users = userDao.findByUsername(username);

            if(!users.isEmpty()) {
                timePrinter.log();
                return Optional.ofNullable(users.getFirst());
            }
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return Optional.empty();
        }

        timePrinter.missing("User not found");

        return Optional.empty();
    }

    public Optional<User> getUserByEmail(String email) {

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [GET] email: " + email);

        try {
            List<User> users = userDao.findByEmail(email);

            if(!users.isEmpty()) {
                timePrinter.log();
                return Optional.ofNullable(users.getFirst());
            }
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
            return Optional.empty();
        }

        timePrinter.missing("User not found");

        return Optional.empty();
    }

    public UserView saveUser(User user) {

        UserView userViewParam = new UserView(user);

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [SAVE] user: " + gson.toJson(userViewParam));

        try {
            // Optional<User> optUser = getUserByUsername(user.getUsername());
            Optional<User> optUser = getUserByEmail(user.getEmail().getEmail());
            if(optUser.isPresent()) {
                // timePrinter.error("User with username" + user.getUsername() + " already exists");
                timePrinter.error("User with email" + user.getEmail() + " already exists");
                return null;
            }

            ObjectId userId = userDao.save(user);

            if(userId == null) {
                timePrinter.error("Error saving user");
                return null;
            }

            Optional<User> optSavedUser = userDao.findById(userId);
            if(optSavedUser.isEmpty()) {
                timePrinter.error("Error saving user");
                return null;
            }

            UserView userView = new UserView(optSavedUser.get());

            timePrinter.log();

            return userView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public boolean updateUser(UserToken userToken, String id, UserMeView userDetails) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [UPDATE] user: " + gson.toJson(userDetails));

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<User> existingUser = userDao.findById(objectId);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.update(userDetails);
                user.setUpdatedBy(userToken.getIdUser());
                boolean updated =  userDao.update(user);

                timePrinter.log();

                return updated;
            }

            timePrinter.missing("User not found");
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public boolean updateUserAdmin(UserToken userToken, String id, Boolean admin) {
        UserView userView = getUserById(userToken, id);

        userView.setAdmin(admin != null ? admin : false);

        return updateUser(userToken, id, userView);
    }

    public boolean updateUser(UserToken userToken, String id, UserView userDetails) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [UPDATE] user: " + gson.toJson(userDetails));

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<User> existingUser = userDao.findById(objectId);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.update(userDetails);
                user.setUpdatedBy(userToken.getIdUser());
                boolean updated =  userDao.update(user);

                timePrinter.log();

                return updated;
            }

            timePrinter.missing("User not found");
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public boolean deleteUser(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [DELETE] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            boolean deleted = userDao.delete(objectId);

            timePrinter.log();

            return deleted;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public boolean changePassword(UserToken userToken, String userId, String currentPassword, String newPassword) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [CHANGE PASSWORD] id: " + userId);

        try {
            ObjectId objectId = new ObjectId(userId);
            Optional<User> userOpt = userDao.findById(objectId);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                if (user.verifyPassword(currentPassword)) {
                    UserPassword newUserPassword = new UserPassword(PasswordHasher.hashPassword(newPassword));
                    return userDao.updatePassword(userId, newUserPassword);
                }

                timePrinter.error("Invalid current password");
            }

            timePrinter.missing("User not found");
        } catch (IllegalArgumentException ignored) { }

        return false;
    }

    public boolean recordFailedLogin(String userId, String ipAddress, String userAgent, Boolean failedPassword) {
        return recordLogin(userId, "failed", ipAddress, userAgent, failedPassword);
    }

    public boolean recordSuccessLogin(String userId, String ipAddress, String userAgent, Boolean failedPassword) {
        return recordLogin(userId, "success", ipAddress, userAgent, failedPassword);
    }

    private boolean recordLogin(String userId, String status, String ipAddress, String userAgent, Boolean failedPassword) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [USER] [RECORD LOGIN] id: " + userId
                        + ", status:" + status
                        + ",  ipAddress:" + ipAddress
                        + ",  userAgent:" + userAgent
                        + ", failedPassword:" + failedPassword
        );

        try {
            ObjectId objectId = new ObjectId(userId);
            LoginHistoryView loginHistoryView = new LoginHistoryView(
                    new Date(),
                    status,
                    ipAddress,
                    userAgent
            );

            boolean updated =  userDao.addLoginHistory(userId, loginHistoryView, failedPassword);

            if(updated) {
                timePrinter.log();
                return true;
            }

            timePrinter.missing("Error during add login history");
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public List<User> getUsersWithExpiredPasswords(UserToken userToken) {
        return userDao.findUsersWithExpiredPasswords();
    }

    public List<User> getUsersWithSuspiciousActivity(UserToken userToken) {
        return userDao.findUsersWithFailedLoginAttempts(3);
    }





    // USER STATUS

    public UserStatusView randomUserStatus() {
        UserStatus[] values = UserStatus.values();
        int index = ThreadLocalRandom.current().nextInt(values.length);
        UserStatus userStatus =  values[index];

        return new UserStatusView(
                userStatus.getIdUserStatus(),
                userStatus.getCode(),
                userStatus.getLabel(),
                userStatus.getColor()
        );
    }

    public List<UserStatusView> listUserStatus(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [STATUS] [LIST] ");

        List<UserStatusView> userStatusViews = new ArrayList<>();

        for(UserStatus userStatus : UserStatus.values()) {
            userStatusViews.add(new UserStatusView(
                    userStatus.getIdUserStatus(),
                    userStatus.getCode(),
                    userStatus.getLabel(),
                    userStatus.getColor()
            ));
        }

        timePrinter.log();

        return userStatusViews;
    }

    public static Long toLongOrNull(String str) {
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return null; // or some default like 0L
        }
    }

    public UserStatusView getUserStatusById(UserToken userToken, String id) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [STATUS] [GET] id: " + id);

        Long idUserStatus = toLongOrNull(id);

        if(idUserStatus == null) {
            return null;
        }

        UserStatus userStatus = UserStatus.fromID(idUserStatus);
        if(userStatus == null) {
            timePrinter.missing("User status not found");
            return null;
        }

        timePrinter.log();

        return new UserStatusView(
                userStatus.getIdUserStatus(),
                userStatus.getCode(),
                userStatus.getLabel(),
                userStatus.getColor()
        );
    }

    public UserStatusView createUserStatus(UserToken userToken, UserStatusView userStatusView) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [STATUS] [CREATE] user status: " + gson.toJson(userStatusView));

        UserStatus userStatus = UserStatus.fromCode(userStatusView.getCode());

        if(userStatus != null) {
            timePrinter.error("User Status code already exists");
            return null;
        }

        timePrinter.log();

        return userStatusView;
    }

    public boolean updateUserStatus(UserToken userToken, String id, UserStatusView userStatusDetails) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [STATUS] [UPDATE] id: " + id + " , user status: " + gson.toJson(userStatusDetails));

        UserStatusView userStatus = getUserStatusById(userToken, id);
        if(userStatus == null) {
            timePrinter.missing("User status not found");
        }

        timePrinter.log();

        return true;
    }


    public boolean deleteUserStatus(UserToken userToken, String id) {

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [USER] [STATUS] [DELETE] id: " + id);

        timePrinter.log();

        return true;
    }

}
