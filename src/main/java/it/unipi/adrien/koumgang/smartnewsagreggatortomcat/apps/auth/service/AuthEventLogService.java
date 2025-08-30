package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.dao.AuthEventLogDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model.AuthEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.repository.AuthEventLogRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view.AuthEventLogView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.log.MineLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.RequestData;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthEventLogService {

    public static AuthEventLogService getInstance() {
        return new AuthEventLogService(AuthEventLogRepository.getInstance());
    }


    private static final Gson gson = new GsonBuilder().serializeNulls().create();


    private final AuthEventLogDao authEventLogDao;

    public AuthEventLogService(AuthEventLogDao authEventLogDao) {
        this.authEventLogDao = authEventLogDao;
    }


    public Optional<AuthEventLogView> getAuthEvent(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return Optional.empty();
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [GET] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            Optional<AuthEventLog> authEventLog = authEventLogDao.findById(objectId);
            if (authEventLog.isPresent()) {
                timePrinter.log();
                return Optional.of(new AuthEventLogView(authEventLog.get()));
            }
        } catch (IllegalArgumentException ignored) { }

        timePrinter.missing("Server event not found");

        return Optional.empty();
    }

    public AuthEventLogView saveAuthEventLog(
            String event,
            String message,
            Boolean isSuccess,
            RequestDataView requestDataView,
            String token,
            String ip,
            Map<String, List<String>> dataServer,
            String metaUser
    ) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter(
                "[SERVICE] [AUTH EVENT LOG] [SAVE] test: event:" + event
                        + ", message:" + message
                        + ", isSuccess:" + isSuccess
                        + ", message" + (requestDataView != null ? gson.toJson(requestDataView) : "null")
        );

        RequestData requestData = requestDataView != null ? new RequestData(requestDataView) : new RequestData();

        AuthEventLog authEventLog = new AuthEventLog(event, isSuccess, message);
        authEventLog.setRequestData(requestData);
        authEventLog.setToken(token);
        authEventLog.setLogIp(ip);
        authEventLog.setServerData(dataServer);
        authEventLog.setCreatedBy(metaUser);
        authEventLog.setUpdatedBy(metaUser);

        try {
            ObjectId authEventLogId =  authEventLogDao.save(authEventLog);

            if(authEventLogId == null) {
                timePrinter.error("Error saving auth event log");
                return null;
            }

            Optional<AuthEventLog> optAuthEventLog = authEventLogDao.findById(authEventLogId);

            if(optAuthEventLog.isEmpty()) {
                timePrinter.error("Error saving auth event log");
                return null;
            }

            AuthEventLogView authEventLogView = new AuthEventLogView(optAuthEventLog.get());

            timePrinter.log();

            return authEventLogView;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return null;
    }

    public boolean deleteAuthEventLog(String id) {
        if (!MongoAnnotationProcessor.isValidObjectId(id)) {
            return false;
        }

        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [DELETE] id: " + id);

        try {
            ObjectId objectId = new ObjectId(id);
            boolean deleted = authEventLogDao.delete(objectId);

            timePrinter.log();

            return deleted;
        } catch (IllegalArgumentException e) {
            timePrinter.error(e.getMessage());
        }

        return false;
    }

    public List<AuthEventLogView> listAuthEventLogs() {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [LIST] ");

        List<AuthEventLog> authEventLogs = authEventLogDao.findAll();

        List<AuthEventLogView> authEventLogViews = authEventLogs.stream().map(AuthEventLogView::new).toList();

        timePrinter.log();

        return authEventLogViews;
    }

    public long numberOfAuthEventLogs() {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [COUNT] ");

        long count = authEventLogDao.count();

        timePrinter.log();

        return count;
    }

    public List<AuthEventLogView> listAuthEventLogsByEvent(String event) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [LIST] event: " + event);

        List<AuthEventLog> authEventLogs = authEventLogDao.findAllByEvent(event);

        List<AuthEventLogView> authEventLogViews = authEventLogs.stream().map(AuthEventLogView::new).toList();

        timePrinter.log();

        return authEventLogViews;
    }

    public long numberOfAuthEventLogsByEvent(String event) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [COUNT] event: " + event);

        long count = authEventLogDao.countByEvent(event);

        timePrinter.log();

        return count;
    }

    public List<AuthEventLogView> listAuthEventLogsByEventAndSuccess(String event, Boolean isSuccess) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [LIST] event: " + event + ", isSuccess: " + isSuccess);

        List<AuthEventLog> authEventLogs = authEventLogDao.findAllByEventAndSuccess(event, isSuccess);

        List<AuthEventLogView> authEventLogViews = authEventLogs.stream().map(AuthEventLogView::new).toList();

        timePrinter.log();

        return authEventLogViews;
    }

    public long numberOfAuthEventLogsByEvent(String event, Boolean isSuccess) {
        MineLog.TimePrinter timePrinter = new MineLog.TimePrinter("[SERVICE] [AUTH EVENT LOG] [COUNT] event: " + event + ", isSuccess: " + isSuccess);

        long count = authEventLogDao.countByEventAndSuccess(event, isSuccess);

        timePrinter.log();

        return count;
    }

}
