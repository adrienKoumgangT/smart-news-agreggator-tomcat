package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class DataInteger extends BaseView {

    @Required
    private Integer data;

    public DataInteger() {}

    public DataInteger(Integer data) {
        this.data = data;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }
}
