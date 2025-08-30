package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class DataBoolean extends BaseView {

    @Required
    private Boolean data;

    public DataBoolean() {}

    public DataBoolean(Boolean data) {
        this.data = data;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }
}
