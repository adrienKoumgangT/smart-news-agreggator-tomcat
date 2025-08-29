package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.server.model.ServerEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;

public class ServerEventLogView extends ServerEventLogSimpleView {

    private RequestDataView requestData;

    public ServerEventLogView() {
        super();
    }

    public ServerEventLogView(ServerEventLog serverEventLog) {
        super(serverEventLog);

        if(serverEventLog.getRequestData() != null) {
            this.requestData = new RequestDataView(serverEventLog.getRequestData());
        }
    }

    public RequestDataView getRequestData() {
        return requestData;
    }
}
