package com.shineyue.certSign.model.vo;

import com.shineyue.certSign.model.BasicVOAndBO;

/**
 * @PackageName: com.shineyue.certSign.model.vo
 * @Description: TODO 用户VO,用于证书注册
 * @author: 罗绂威
 * @date: wrote on 2019/8/30
 */
public class RaUserVO extends BasicVOAndBO {
    /** 忽略组织机构代码证唯一检查 */
    private String ignoreUserIdNo;

    /** 用户名称/机构名称 */
    private String userName;

    /** 用户证件号码 */
    private String userIdno;

    /** 证书CN项 */
    private String certCn;

    /** 区/县 */
    private String userOrg;

    /** 市 */
    private String userCity;

    /** 省 */
    private String userState;

    /** 国家 */
    private String userCountry;

    /** 电子邮件 */
    private String userEmail;

    /** 企业/机构编号 */
    private String userPhone;

    /** 所属行业 */
    private String userSaname;

    /** 所属单位 */
    private String userDomain;

    /** 证书类型，固定为00 */
    private String userCrey;

    public String getIgnoreUserIdNo() {
        return ignoreUserIdNo;
    }

    public void setIgnoreUserIdNo(String ignoreUserIdNo) {
        this.ignoreUserIdNo = ignoreUserIdNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIdno() {
        return userIdno;
    }

    public void setUserIdno(String userIdno) {
        this.userIdno = userIdno;
    }

    public String getCertCn() {
        return certCn;
    }

    public void setCertCn(String certCn) {
        this.certCn = certCn;
    }

    public String getUserOrg() {
        return userOrg;
    }

    public void setUserOrg(String userOrg) {
        this.userOrg = userOrg;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }


    public String getUserSaname() {
        return userSaname;
    }

    public void setUserSaname(String userSaname) {
        this.userSaname = userSaname;
    }

    public String getUserDomain() {
        return userDomain;
    }

    public void setUserDomain(String userDomain) {
        this.userDomain = userDomain;
    }

    public String getUserCrey() {
        return userCrey;
    }

    public void setUserCrey(String userCrey) {
        this.userCrey = userCrey;
    }
}
