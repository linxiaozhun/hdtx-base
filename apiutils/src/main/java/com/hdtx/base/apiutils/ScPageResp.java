package com.hdtx.base.apiutils;

import com.hdtx.base.apiutils.api.CommonError;

import java.util.List;
import java.util.Objects;

import static com.hdtx.base.apiutils.Constants.HTTP_STATUS_OK;

/**
 * @author huangwenc
 */
public class ScPageResp<T> {

    protected List<T> result;
    protected Long totalCount;
    protected Long pageSize;
    protected Long currentPage;
    private String message;
    private int status;

    public static <T> ScPageResp<T> create(String message, int status, long totalCount,
                                           long pageSize, long currentPage, List<T> result) {
        ScPageResp<T> scPageResp = new ScPageResp<>();
        scPageResp.setResult(result);
        scPageResp.setMessage(message);
        scPageResp.setStatus(status);
        if (totalCount >= 0) {
            scPageResp.setCurrentPage(currentPage);
            scPageResp.setPageSize(pageSize);
            scPageResp.setTotalCount(totalCount);
        }
        return scPageResp;
    }

    public static <T> ScPageResp<T> create(String message, int status) {
        ScPageResp<T> scPageResp = new ScPageResp<>();
        scPageResp.setMessage(message);
        scPageResp.setStatus(status);
        return scPageResp;
    }

    public static <T> ScPageResp<T> ok(List<T> result) {
        return ok(-1, -1, -1, result);
    }

    public static <T> ScPageResp<T> ok(long totalCount, long pageSize, long currentPage, List<T> result) {
        return create("OK", HTTP_STATUS_OK, totalCount, pageSize, currentPage, result);
    }

    public static ScPageResp internalError() {
        return create(CommonError.INTERNAL_ERROR.getMessage(), CommonError.INTERNAL_ERROR.getStatus());
    }


    public boolean isOk() {
        return this.status == HTTP_STATUS_OK;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTotalPage() {
        if (Objects.isNull(this.totalCount)) {
            return null;
        }
        return this.totalCount <= 0 || this.pageSize <= 0? 1 : (this.totalCount - 1) / this.pageSize + 1;
    }



}
