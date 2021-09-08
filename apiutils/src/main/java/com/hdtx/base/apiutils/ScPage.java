package com.hdtx.base.apiutils;


import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象, 用于接口传输
 * @param <E>
 */
public class ScPage<E> {

    private int pageNum = 1;
    private int pageSize = 10;
    private int startRow;
    private int totalCount;
    private int totalPage;
    private List<E> dataList;

    public ScPage() {
        super();
    }

    public ScPage(int pageNum, int pageSize) {
        if (pageNum >= 1) {
            this.pageNum = pageNum;
        }
        if (pageNum >= 1) {
            this.pageSize = pageSize;
        }
        calculateStartAndEndRow();
    }

    /**
     * 计算起始行号
     */
    private void calculateStartAndEndRow() {
        this.startRow = (this.pageNum - 1) * this.pageSize;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总条数
     * @param totalCount
     * @return
     */
    public ScPage<E> setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        if (totalCount % pageSize == 0) {
            this.totalPage = totalCount / pageSize;
        } else {
            this.totalPage = totalCount / pageSize + 1;
        }

        return this;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public List<E> getDataList() {
        //make the data un-change
        return dataList == null ? new ArrayList<>() : new ArrayList<>(dataList);
    }

    public ScPage<E> setDataList(List<E> dataList) {
        this.dataList = dataList;
        return this;
    }

    public int getPageNum() {
        return pageNum;
    }
}
