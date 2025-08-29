package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoDateTime;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoField;

import java.util.Date;

public class LoginHistory {

    @MongoField("login_time")
    @MongoDateTime(utc = true)
    private Date loginTime;

    @MongoField("status")
    private String status;

    @MongoField("ip_address")
    private String ipAddress;

    @MongoField("user_agent")
    private String userAgent;

    public LoginHistory() {}

    public LoginHistory(Date loginTime, String status) {
        this.loginTime = loginTime;
        this.status = status;
    }

    public LoginHistory(Date loginTime, String status, String ipAddress, String userAgent) {
        this(loginTime, status);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // Getters and Setters
    public Date getLoginTime() { return loginTime; }
    public void setLoginTime(Date loginTime) { this.loginTime = loginTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
