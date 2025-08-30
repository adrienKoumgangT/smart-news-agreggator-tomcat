package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.view;

public class PaginationView {

    private Integer page;
    private Integer pageSize;
    private Long total;

    public PaginationView() {}

    public PaginationView(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public PaginationView(Integer page, Integer pageSize, Long total) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
