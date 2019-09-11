package com.shineyue.certSign.model.po;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/6
 */
public class ManagerPO {

    private String uid;
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "{" +
                "username='" + username + '\'' +
                '}';
    }
}
