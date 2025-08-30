package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.auth.model.AuthEventLog;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;

public class AuthEventLogSimpleView extends BaseView {

    private String authEventLogId;

    private String event;

    private Boolean isSuccess;

    private String message;

    public AuthEventLogSimpleView() {}

    public AuthEventLogSimpleView(AuthEventLog authEventLog) {
        this.authEventLogId = StringIdConverter.getInstance().fromObjectId(authEventLog.getAuthEventLogId());

        this.event      = authEventLog.getEvent();
        this.isSuccess  = authEventLog.getSuccess();
        this.message    = authEventLog.getMessage();
    }

    public String getAuthEventLogId() {
        return authEventLogId;
    }

    public String getEvent() {
        return event;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}
