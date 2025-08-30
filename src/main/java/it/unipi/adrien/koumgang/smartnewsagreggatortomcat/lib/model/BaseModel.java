package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCreatedAt;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoUpdatedAt;

import java.util.Date;

public class BaseModel {


    @ModelField("created_by")
    private String createdBy;

    @ModelField("created_at")
    @MongoCreatedAt
    private Date createdAt;

    @ModelField("updated_by")
    private String updatedBy;

    @ModelField("updated_at")
    @MongoUpdatedAt
    private Date updatedAt;


    public BaseModel() {}


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    // Business methods

    public void updateTimestamps() {
        this.updatedAt = new Date();
    }
}
