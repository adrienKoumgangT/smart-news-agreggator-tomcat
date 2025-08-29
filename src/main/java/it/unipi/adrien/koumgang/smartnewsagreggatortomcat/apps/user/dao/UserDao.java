package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.User;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(ObjectId id);
    List<User> findAll();
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);
    List<User> findActiveUsers();
    User save(User user);
    boolean update(User user);
    boolean delete(ObjectId id);
    long count();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}