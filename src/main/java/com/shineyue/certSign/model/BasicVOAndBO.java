package com.shineyue.certSign.model;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/1
 */
public class BasicVOAndBO {

    /**
     * 证书类别代号:
     * 1: 个人证书
     * 2: 机构证书
     */
    private int userCertTypeCode;

    /**
     * 证书模板名称:
     * 比如
     * userCert1: 个人单项证书
     * entCert2:  机构双向证书
     */
    private String userCertType;

    /** 证书请求的验证信息 */
    private String req;

    public int getUserCertTypeCode() {
        return userCertTypeCode;
    }

    public void setUserCertTypeCode(int userCertTypeCode) {
        this.userCertTypeCode = userCertTypeCode;
    }

    public String getUserCertType() {
        return userCertType;
    }

    public void setUserCertType(String userCertType) {
        this.userCertType = userCertType;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }
}
