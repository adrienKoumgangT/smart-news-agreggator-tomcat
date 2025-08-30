package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model.ServerEventLog;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface ServerEventLogDao {

    Optional<ServerEventLog> findById(ObjectId id);

    long count();
    List<ServerEventLog> findAll();

    long countByEvent(String event);
    List<ServerEventLog> findByEvent(String event);
    List<ServerEventLog> findByEvent(String event, int page, int pageSize);

    long countByName(String event);
    List<ServerEventLog> findByName(String name);
    List<ServerEventLog> findByName(String name, int page, int pageSize);

    long countByEventAndName(String event, String name);
    List<ServerEventLog> findByEventAndName(String event, String name);
    List<ServerEventLog> findByEventAndName(String event, String name, int page, int pageSize);

    ObjectId save(ServerEventLog serverEventLog);

    boolean delete(ObjectId id);

}
