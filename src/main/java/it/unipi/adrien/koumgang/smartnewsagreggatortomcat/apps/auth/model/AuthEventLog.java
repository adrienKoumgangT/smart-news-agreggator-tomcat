package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.RequestData;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

@MongoCollectionName("auth-event-log")
@MongoIndex(fields = {"event:1", "created_at:-1"})
@MongoIndex(fields = {"is_success:1", "created_at:-1"})
@MongoIndex(fields = {"event:1", "is_success:1", "created_at:-1"})
public class AuthEventLog extends BaseModel {

    @MongoId
    private ObjectId authEventLogId;

    @MongoEmbedded("request_data")
    private RequestData requestData;

    @ModelField("event")
    private String event;

    @ModelField("is_success")
    private Boolean isSuccess;

    @ModelField("message")
    private String message;

    @ModelField("token")
    private String token;

    @ModelField("log_ip")
    private String logIp;

    @ModelField("server_data")
    private Map<String, List<String>> serverData;

    public AuthEventLog() {}

    public AuthEventLog(String event, Boolean isSuccess, String message) {
        this.event = event;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public ObjectId getAuthEventLogId() {
        return authEventLogId;
    }

    public void setAuthEventLogId(ObjectId authEventLogId) {
        this.authEventLogId = authEventLogId;
    }

    public RequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogIp() {
        return logIp;
    }

    public void setLogIp(String logIp) {
        this.logIp = logIp;
    }

    public Map<String, List<String>> getServerData() {
        return serverData;
    }

    public void setServerData(Map<String, List<String>> serverData) {
        this.serverData = serverData;
    }
}
