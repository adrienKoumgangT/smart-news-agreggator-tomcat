package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCollectionName;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoEmbedded;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoId;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoBaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.RequestData;
import org.bson.types.ObjectId;

@MongoCollectionName("auth-event-log")
public class AuthEventLog extends MongoBaseModel {

    @MongoId
    private ObjectId authEventLogId;

    @MongoEmbedded("request_data")
    private RequestData requestData;

    @MongoField("event")
    private String event;

    @MongoField("is_success")
    private Boolean isSuccess;

    @MongoField("message")
    private String message;

    public AuthEventLog() {}

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
}
