package com.shineyue.certSign.model.dto;

/**
 * @Description: TODO 个人签署回调对象
 * @author: luofuwei
 * @date: wrote on 2019/9/12
 */
public class SignCallbackDTO {
    /** 网签合同号 */
    private String wqhth;

    /** PDF文件 */
    private String inputPDF;

    /** 是否签署 */
    private String isSign;

    /** 拒绝/失败签署原因 */
    private String reason;

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

    public String getIsSign() {
        return isSign;
    }

    public void setIsSign(String isSign) {
        this.isSign = isSign;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "{" +
                "'wqhth':'" + wqhth + '\'' +
                ", 'inputPDF':'" + inputPDF + '\'' +
                ", 'isSign':'" + isSign + '\'' +
                ", 'reason':'" + reason + '\'' +
                '}';
    }
}
