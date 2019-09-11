package com.shineyue.certSign.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Description: TODO 网签合同数据传输对象
 * @author: luofuwei
 * @date: wrote on 2019/9/7
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignContractDTO {
    /** 网签合同号 */
    private String wqhth;

    /** PDF文件 */
    private String inputPDF;

    /** key的序列号 */
    private String serial;

    /** 对象名称 */
    private String subject;

    /** 用户类型(1.个人/2.企业/3.房管局) */
    private int type;

    /** 页码,可多个 */
    private String pageNums;

    /** 签章位置 */
    private String picPoints;
    
    /** 统计位置 */
    private int pointCount;

    public String getWqhth() {
        return wqhth;
    }

    public void setWqhth(String wqhth) {
        this.wqhth = wqhth;
    }

    public String getInputPDF() {
        return inputPDF;
    }

    public void setInputPDF(String inputPDF) {
        this.inputPDF = inputPDF;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPageNums() {
        return pageNums;
    }

    public void setPageNums(String pageNums) {
        this.pageNums = pageNums;
    }

    public String getPicPoints() {
        return picPoints;
    }

    public void setPicPoints(String picPoints) {
        this.picPoints = picPoints;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
