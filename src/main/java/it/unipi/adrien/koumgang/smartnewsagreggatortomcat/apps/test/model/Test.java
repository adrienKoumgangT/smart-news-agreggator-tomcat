package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.model;


import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view.TestView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.annotation.*;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.BaseModel;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.database.nosql.mongodb.utils.StringIdConverter;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.annotation.ModelField;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@MongoCollectionName("tests")
@MongoIndex(fields = {"is_active:1", "created_at:-1"})
@MongoIndex(fields = {"name:1"}, unique = true)   // unique name
@MongoIndex(fields = {"description:1"})           // another single index
public class Test extends BaseModel {

    @MongoId
    private ObjectId testId;

    @ModelField("name")
    @MongoIndex(unique = true)
    private String name;

    @ModelField("description")
    private String description;

    @ModelField("is_active")
    private Boolean isActive;

    @MongoEmbedded("embed")
    private TestEmbed embed;

    @MongoEmbeddedList("list_embed")
    private List<TestEmbed> listEmbed;


    public Test() {}

    public Test(TestView testView) {
        this.testId         = StringIdConverter.getInstance().toObjectId(testView.getTestId());
        this.name           = testView.getName();
        this.description    = testView.getDescription();
        this.isActive       = testView.getActive();

        if(testView.getEmbed() != null) this.embed = new TestEmbed(testView.getEmbed());
        if(testView.getListEmbed() != null) this.listEmbed = testView.getListEmbed().stream().map(TestEmbed::new).toList();
    }


    public ObjectId getTestId() {
        return testId;
    }

    public void setTestId(ObjectId testId) {
        this.testId = testId;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public TestEmbed getEmbed() {
        return embed;
    }

    public void setEmbed(TestEmbed embed) {
        this.embed = embed;
    }

    public List<TestEmbed> getListEmbed() {
        return listEmbed;
    }

    public void setListEmbed(List<TestEmbed> listEmbed) {
        this.listEmbed = listEmbed;
    }

    public void addEmbed(TestEmbed testEmbed) {
        if(this.listEmbed == null) {
            this.listEmbed = new ArrayList<>();
        }
        this.listEmbed.add(testEmbed);
    }
}
