package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class DataString extends BaseView {

    @Required
    private String data;

    public DataString() {}

    public DataString(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
