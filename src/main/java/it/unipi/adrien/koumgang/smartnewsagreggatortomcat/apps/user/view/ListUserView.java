package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.PaginationView;

import java.util.List;

public class ListUserView {

    List<UserView> users;

    private PaginationView pagination;

    public ListUserView() {}

    public ListUserView(List<UserView> users, PaginationView pagination) {
        this.users = users;
        this.pagination = pagination;
    }

    public List<UserView> getUsers() {
        return users;
    }

    public void setUsers(List<UserView> users) {
        this.users = users;
    }

    public PaginationView getPagination() {
        return pagination;
    }

    public void setPagination(PaginationView pagination) {
        this.pagination = pagination;
    }
}
