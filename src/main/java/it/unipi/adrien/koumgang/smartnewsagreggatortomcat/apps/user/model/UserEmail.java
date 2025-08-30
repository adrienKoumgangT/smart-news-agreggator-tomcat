package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoDateTime;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;

import java.util.Date;

public class UserEmail {

    @ModelField("email")
    private String email;

    @ModelField("confirmed")
    private Boolean confirmed;

    @ModelField("confirmed_at")
    @MongoDateTime(utc = true)
    private Date confirmedAt;

    public UserEmail() {}

    public UserEmail(String email) {
        this.email = email;
        this.confirmed = false;
    }

    public UserEmail(String email, Boolean confirmed) {
        this.email = email;
        this.confirmed = confirmed;
        this.confirmedAt = new Date();
    }

    public String getEmail() {
        return email;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
