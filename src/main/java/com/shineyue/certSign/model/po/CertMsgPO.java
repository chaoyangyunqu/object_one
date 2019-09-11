package com.shineyue.certSign.model.po;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/8
 */
public class CertMsgPO {
    private int id;
    private int userNumber;
    private String content;
    private int isInstall;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsInstall() {
        return isInstall;
    }

    public void setIsInstall(int isInstall) {
        this.isInstall = isInstall;
    }
}
