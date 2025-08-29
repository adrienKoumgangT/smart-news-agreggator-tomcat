package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model.LoginHistory;

import java.util.Date;

public class LoginHistorySimpleView {

    private Date loginTime;

    private String status;

    public LoginHistorySimpleView() {}

    public LoginHistorySimpleView(LoginHistory loginHistory) {
        this.loginTime = loginHistory.getLoginTime();
        this.status = loginHistory.getStatus();
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public String getStatus() {
        return status;
    }
}
