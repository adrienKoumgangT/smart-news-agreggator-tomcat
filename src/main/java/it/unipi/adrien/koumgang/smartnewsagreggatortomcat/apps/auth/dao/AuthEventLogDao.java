package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model.AuthEventLog;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface AuthEventLogDao {

    Optional<AuthEventLog> findById(ObjectId id);

    long count();
    List<AuthEventLog> findAll();
    List<AuthEventLog> findAll(int page, int pageSize);

    long countByEvent(String event);
    List<AuthEventLog> findAllByEvent(String event);
    List<AuthEventLog> findAllByEvent(String event, int page, int pageSize);

    long countBySuccess(Boolean success);
    List<AuthEventLog> findAllBySuccess(Boolean success);
    List<AuthEventLog> findAllBySuccess(Boolean success, int page, int pageSize);

    long countByEventAndSuccess(String event, Boolean success);
    List<AuthEventLog> findAllByEventAndSuccess(String event, Boolean success);
    List<AuthEventLog> findAllByEventAndSuccess(String event, Boolean success, int page, int pageSize);

    ObjectId save(AuthEventLog authEventLog);

    boolean delete(ObjectId id);

}
