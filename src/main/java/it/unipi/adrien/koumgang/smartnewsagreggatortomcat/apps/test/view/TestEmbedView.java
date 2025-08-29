package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model.TestEmbed;

import java.util.Date;

public class TestEmbedView {

    private String name;

    private String description;

    private Date date;

    public TestEmbedView() {}

    public TestEmbedView(TestEmbed testEmbed) {
        this.name           = testEmbed.getName();
        this.description    = testEmbed.getDescription();
        this.date           = testEmbed.getDate();
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
