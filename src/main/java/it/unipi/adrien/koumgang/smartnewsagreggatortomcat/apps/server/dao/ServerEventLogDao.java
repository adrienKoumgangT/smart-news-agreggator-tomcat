package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.dao;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model.ServerEventLog;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface ServerEventLogDao {

    Optional<ServerEventLog> findById(ObjectId id);
    List<ServerEventLog> findAll();
    List<ServerEventLog> findByEvent(String event);
    List<ServerEventLog> findByName(String name);
    ServerEventLog save(ServerEventLog user);
    boolean delete(ObjectId id);
    long count();
    long countByEvent(String event);
    long countByName(String event);

}
