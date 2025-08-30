package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class DataFloat extends BaseView {

    @Required
    private Float data;

    public DataFloat() {}

    public DataFloat(Float data) {
        this.data = data;
    }

    public Float getData() {
        return data;
    }

    public void setData(Float data) {
        this.data = data;
    }
}
