package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model.LoginHistory;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view.LoginHistorySimpleView;

import java.util.Date;

public class LoginHistoryView extends LoginHistorySimpleView {

    private String ipAddress;

    private String userAgent;

    public LoginHistoryView() {
        super();
    }

    public LoginHistoryView(Date loginTime, String status, String ipAddress, String userAgent) {
        super(loginTime, status);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public LoginHistoryView(LoginHistory loginHistory) {
        super(loginHistory);
        this.ipAddress = loginHistory.getIpAddress();
        this.userAgent = loginHistory.getUserAgent();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
