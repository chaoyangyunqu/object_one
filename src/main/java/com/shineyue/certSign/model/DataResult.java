package com.shineyue.certSign.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/8/30
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResult {
    private int status;
    private boolean success;
    private String msg;
    private String error;
    private int totalcount;
    private Object results;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }
}
