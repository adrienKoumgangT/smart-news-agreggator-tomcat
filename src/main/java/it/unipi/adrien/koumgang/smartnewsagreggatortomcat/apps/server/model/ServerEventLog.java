package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.RequestData;
import org.bson.types.ObjectId;

@MongoIndex(fields = {"event:1", "created_at:-1"})
@MongoIndex(fields = {"event:1", "name:1", "created_at:-1"})
@MongoCollectionName("server-event-log")
public class ServerEventLog extends BaseModel {

    @MongoId
    private ObjectId serverEventLogId;

    @MongoIndex
    @ModelField("event")
    private String event;

    @MongoEmbedded("request_data")
    private RequestData requestData;

    @ModelField("curl")
    private String curl;

    @MongoIndex
    @ModelField("name")
    private String name;

    @ModelField("message")
    private String message;

    public ServerEventLog() {}

    public ServerEventLog(String event, String name, String message) {
        this.event = event;
        this.name = name;
        this.message = message;
    }

    public ObjectId getServerEventLogId() {
        return serverEventLogId;
    }

    public void setServerEventLogId(ObjectId serverEventLogId) {
        this.serverEventLogId = serverEventLogId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public RequestData getRequestData() {
        return requestData;
    }

    public void setRequestData(RequestData requestData) {
        this.requestData = requestData;
    }

    public String getCurl() {
        return curl;
    }

    public void setCurl(String curl) {
        this.curl = curl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
