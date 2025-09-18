package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.dao.ServerEventLogDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model.ServerEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.repository.ServerEventLogRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view.ServerEventLogView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.RequestData;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerEventLogService {

    public static ServerEventLogService getInstance() {
        return new ServerEventLogService(ServerEventLogRepository.getInstance());
    }


    private static final Gson gson = new GsonBuilder().serializeNulls().create();


    private final ServerEventLogDao serverEventLogDao;

    public ServerEventLogService(ServerEventLogDao serverEventLogDao) {
        this.serverEventLogDao = serverEventLogDao;
    }


    public Optional<ServerEventLogView> getServerEventLog(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return Optional.empty();
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [GET] id: " + id
        );

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<ServerEventLog> serverEventLog = serverEventLogDao.findById(objectId);
            if (serverEventLog.isPresent()) {
                timePrinter.log();
                return Optional.of(new ServerEventLogView(serverEventLog.get()));
            }
        } catch (IllegalArgumentException ignored) { }

        timePrinter.missing("Server event not found");

        return Optional.empty();
    }

    public ServerEventLogView saveServerEventLog(
            String event,
            String name,
            String message,
            String file,
            String curl,
            RequestDataView requestDataView,
            String metaUser
    ) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [SAVE] test: event:" + event
                        + ", name:" + name
                        + ", message:" + message
                        + ", curl:" + curl
                        + ", message" + (requestDataView != null ? gson.toJson(requestDataView) : "null")
        );

        RequestData requestData = requestDataView != null ? new RequestData(requestDataView) : new RequestData();

        ServerEventLog serverEventLog = new ServerEventLog(event, name, message);
        serverEventLog.setCurl(curl);
        serverEventLog.setRequestData(requestData);
        serverEventLog.setCreatedBy(metaUser);
        serverEventLog.setUpdatedBy(metaUser);

        try {
            ObjectId serverEventLogId =  serverEventLogDao.save(serverEventLog);

            if(serverEventLogId == null) {
                timePrinter.error("Error saving server event log");
                return null;
            }

            Optional<ServerEventLog> optServerEventLog = serverEventLogDao.findById(serverEventLogId);

            if(optServerEventLog.isEmpty()) {
                timePrinter.error("Error saving server event log");
                return null;
            }

            ServerEventLogView serverEventLogView = new ServerEventLogView(optServerEventLog.get());

            timePrinter.log();

            return serverEventLogView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public boolean deleteServerEventLog(UserToken userToken, String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [DELETE] id: " + id
        );

        try {
            ObjectId objectId = new ObjectId(id);
            boolean deleted = serverEventLogDao.delete(objectId);

            timePrinter.log();

            return deleted;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public List<ServerEventLogView> listServerEventLogs(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [SERVER EVENT LOG] [LIST] ");

        List<ServerEventLog> serverEventLogs = serverEventLogDao.findAll();

        List<ServerEventLogView> serverEventLogViews = serverEventLogs.stream().map(ServerEventLogView::new).toList();

        timePrinter.log();

        return serverEventLogViews;
    }

    public long getNumberOfServerEventLogs(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [SERVER EVENT LOG] [COUNT] ");

        long count = serverEventLogDao.count();

        timePrinter.log();

        return count;
    }

    public List<ServerEventLogView> listServerEventLogsByEvent(UserToken userToken, String event) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [LIST] event: " + event
        );

        List<ServerEventLog> serverEventLogs = serverEventLogDao.findByEvent(event);

        List<ServerEventLogView> serverEventLogViews = serverEventLogs.stream().map(ServerEventLogView::new).toList();

        timePrinter.log();

        return serverEventLogViews;
    }

    public long getNumberOfServerEventLogsByEvent(UserToken userToken, String event) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [COUNT] event: " + event
        );

        long count = serverEventLogDao.countByEvent(event);

        timePrinter.log();

        return count;
    }

    public List<ServerEventLogView> listServerEventLogsByName(UserToken userToken, String name) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [LIST] name: " + name
        );

        List<ServerEventLog> serverEventLogs = serverEventLogDao.findByName(name);

        List<ServerEventLogView> serverEventLogViews = serverEventLogs.stream().map(ServerEventLogView::new).toList();

        timePrinter.log();

        return serverEventLogViews;
    }

    public long getNumberOfServerEventLogsByName(UserToken userToken, String name) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [SERVER EVENT LOG] [COUNT] name: " + name
        );

        long count = serverEventLogDao.countByName(name);

        timePrinter.log();

        return count;
    }

    public List<String> listDistinctEvents(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [SERVER EVENT LOG] [DISTINCT EVENTS] ");

        List<String> events = ((ServerEventLogRepository) serverEventLogDao).listDistinctEvents();

        timePrinter.log();
        return events;
    }

    public Map<String, List<String>> getDistinctEventsNames(UserToken userToken) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [SERVER EVENT LOG] [DISTINCT EVENTS NAMES] ");

        Map<String, List<String>> eventsNames = ((ServerEventLogRepository) serverEventLogDao).mapDistinctEventsNames();

        timePrinter.log();
        return eventsNames;
    }

}
