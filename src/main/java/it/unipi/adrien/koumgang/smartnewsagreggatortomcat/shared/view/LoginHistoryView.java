package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.LoginHistory;

public class LoginHistoryView extends LoginHistorySimpleView {

    private String ipAddress;

    private String userAgent;

    public LoginHistoryView() {
        super();
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
