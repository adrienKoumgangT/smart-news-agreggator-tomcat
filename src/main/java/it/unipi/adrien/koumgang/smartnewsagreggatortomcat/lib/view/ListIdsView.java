package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view;

import java.util.List;

public class ListIdsView {

    List<String> ids;

    private PaginationView pagination;

    public ListIdsView() {}

    public ListIdsView(List<String> ids, PaginationView pagination) {
        this.ids = ids;
        this.pagination = pagination;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public PaginationView getPagination() {
        return pagination;
    }

    public void setPagination(PaginationView pagination) {
        this.pagination = pagination;
    }
}
