package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoDateTime;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core.MongoAnnotationProcessor;

import java.util.Date;

public class UserPassword {

    @MongoField("password")
    private String passwordHash;

    @MongoField("need_change")
    private Boolean needChange;

    @MongoField("changed_at")
    @MongoDateTime(utc = true)
    private Date changedAt;

    @MongoField("expires_at")
    @MongoDateTime(utc = true)
    private Date expiresAt;

    @MongoField("failed_attempts")
    private Integer failedAttempts;

    @MongoField("last_failed_attempt")
    @MongoDateTime(utc = true)
    private Date lastFailedAttempt;

    @MongoField("is_locked")
    private Boolean locked;


    // Constructors
    public UserPassword() {
        this.needChange = false;
        this.failedAttempts = 0;
        this.locked = false;
    }

    public UserPassword(String passwordHash) {
        this();
        this.passwordHash = passwordHash;
        this.changedAt = new Date();
        this.expiresAt = MongoAnnotationProcessor.nowMinusDays(3); // 3 months expiration
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.changedAt = new Date();
    }

    public Boolean isNeedChange() {
        return needChange;
    }

    public void setNeedChange(Boolean needChange) {
        this.needChange = needChange;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Integer getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Date getLastFailedAttempt() {
        return lastFailedAttempt;
    }

    public void setLastFailedAttempt(Date lastFailedAttempt) {
        this.lastFailedAttempt = lastFailedAttempt;
    }

    public Boolean isLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    // Business methods

    public void recordFailedAttempt() {
        this.failedAttempts++;
        this.lastFailedAttempt = new Date();

        if (this.failedAttempts >= 5) {
            this.locked = true;
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.lastFailedAttempt = null;
        this.locked = false;
    }

    public boolean isExpired() {
        return expiresAt != null && (new Date()).after(expiresAt);
    }

    public boolean needsChange() {
        return needChange || isExpired();
    }

}
