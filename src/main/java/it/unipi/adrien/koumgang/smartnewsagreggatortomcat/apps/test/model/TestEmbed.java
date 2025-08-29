package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.TestEmbedView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoDateTime;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.MongoField;

import java.util.Date;

public class TestEmbed {

    @MongoField("name")
    private String name;

    @MongoField("description")
    private String description;

    @MongoField("date")
    @MongoDateTime(utc = true)
    private Date date;

    public TestEmbed() {}

    public TestEmbed(TestEmbedView testEmbedView) {
        this.name           = testEmbedView.getName();
        this.description    = testEmbedView.getDescription();
        this.date           = testEmbedView.getDate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
