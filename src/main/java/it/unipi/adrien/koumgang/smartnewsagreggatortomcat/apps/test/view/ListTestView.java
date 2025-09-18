package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.test.view;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.BaseView;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view.PaginationView;

import java.util.List;

public class ListTestView extends BaseView {

    private List<TestView> tests;

    private PaginationView pagination;

    public ListTestView() {}

    public ListTestView(List<TestView> tests, PaginationView pagination) {
        this.tests = tests;
        this.pagination = pagination;
    }

    public List<TestView> getTests() {
        return tests;
    }

    public void setTests(List<TestView> tests) {
        this.tests = tests;
    }

    public PaginationView getPagination() {
        return pagination;
    }

    public void setPagination(PaginationView pagination) {
        this.pagination = pagination;
    }
}
