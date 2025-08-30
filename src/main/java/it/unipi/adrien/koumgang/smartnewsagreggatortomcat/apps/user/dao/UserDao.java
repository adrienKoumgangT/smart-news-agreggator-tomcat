package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.UserPassword;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(ObjectId id);
    List<User> findAll();
    List<User> findAll(int page, int pageSize);
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);
    List<User> findActiveUsers();
    ObjectId save(User user);
    boolean update(User user);
    boolean delete(ObjectId id);
    long count();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean updatePassword(String userId, UserPassword newPassword);

}