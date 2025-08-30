package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.shared.model;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.Required;

public class DataLong extends BaseView {

    @Required
    private Long data;

    public DataLong() {}

    public DataLong(Long data) {
        this.data = data;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }
}
