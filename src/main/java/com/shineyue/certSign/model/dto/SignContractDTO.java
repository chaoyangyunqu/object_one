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

    /** 个人名称 */
    private String personName;

    /** 个人电话 */
    private String personPhone;

    /** 个人身份证 */
    private String personIdCard;

    /** 个人签署页码 */
    private String personPageNums;

    /** 个人签署坐标位置 */
    private String personPicPoints;

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

    public String getPersonPageNums() {
        return personPageNums;
    }

    public void setPersonPageNums(String personPageNums) {
        this.personPageNums = personPageNums;
    }

    public String getPersonPicPoints() {
        return personPicPoints;
    }

    public void setPersonPicPoints(String personPicPoints) {
        this.personPicPoints = personPicPoints;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
    }

    public String getPersonIdCard() {
        return personIdCard;
    }

    public void setPersonIdCard(String personIdCard) {
        this.personIdCard = personIdCard;
    }
}
