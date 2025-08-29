package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.core;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoCreatedAt;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoField;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoUpdatedAt;

import java.util.Date;

public class MongoBaseModel {


    @MongoField("created_by")
    private String createdBy;

    @MongoField("created_at")
    @MongoCreatedAt
    private Date createdAt;

    @MongoField("updated_by")
    private String updatedBy;

    @MongoField("updated_at")
    @MongoUpdatedAt
    private Date updatedAt;


    public MongoBaseModel() {}


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
