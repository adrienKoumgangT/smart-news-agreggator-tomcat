package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model.ServerEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class ServerEventLogSimpleView extends BaseView {

    private String serverEventLogId;

    @Required
    private String event;

    private String curl;

    @Required
    private String name;

    private String message;

    public ServerEventLogSimpleView() {}

    public ServerEventLogSimpleView(ServerEventLog serverEventLog) {
        this.serverEventLogId = StringIdConverter.getInstance().fromObjectId(serverEventLog.getServerEventLogId());

        this.event = serverEventLog.getEvent();
        this.curl = serverEventLog.getCurl();
        this.name = serverEventLog.getName();
        this.message = serverEventLog.getMessage();
    }

    public String getServerEventLogId() {
        return serverEventLogId;
    }

    public String getEvent() {
        return event;
    }

    public String getCurl() {
        return curl;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
