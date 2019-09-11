package com.shineyue.certSign.model.vo;

/**
 * @Description: TODO 电子章绑定
 * @author: luofuwei
 * @date: wrote on 2019/9/9
 */
public class PicBindCertCNVO {

    /** 对象名称 */
    private String subject;
    /** 序列号/唯一号 */
    private String serial;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public String toString() {
        return "PicBindCertCNVO{" +
                "subject='" + subject + '\'' +
                ", serial='" + serial + '\'' +
                '}';
    }
}
