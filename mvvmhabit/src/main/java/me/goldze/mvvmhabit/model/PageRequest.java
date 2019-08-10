package me.goldze.mvvmhabit.model;

public class PageRequest {

    /**
     * 当前页码
     */
    private Integer page = 1;
    /**
     * 每页数量
     */
    private Integer rows = 15;
    /**
     * 排序规则：升序=asc，降序=desc
     */
    private String order;// asc desc
    /**
     * 排序字段名称
     */
    private String sort;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
