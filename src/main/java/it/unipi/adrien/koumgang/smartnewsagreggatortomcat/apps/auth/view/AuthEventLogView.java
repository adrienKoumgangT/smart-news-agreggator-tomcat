package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model.AuthEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.RequestDataView;

import java.util.List;
import java.util.Map;

public class AuthEventLogView extends AuthEventLogSimpleView {

    private RequestDataView requestData;

    private String logIp;

    private Map<String, List<String>> serverData;

    public AuthEventLogView() {
        super();
    }

    public AuthEventLogView(AuthEventLog authEventLog) {
        super(authEventLog);

        if(authEventLog.getRequestData() != null) {
            this.requestData = new RequestDataView(authEventLog.getRequestData());
        }

        this.logIp      = authEventLog.getLogIp();
        this.serverData = authEventLog.getServerData();
    }

    public RequestDataView getRequestData() {
        return requestData;
    }

    public String getLogIp() {
        return logIp;
    }

    public Map<String, List<String>> getServerData() {
        return serverData;
    }
}
