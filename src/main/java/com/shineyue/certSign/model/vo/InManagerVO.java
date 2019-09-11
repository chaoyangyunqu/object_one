package com.shineyue.certSign.model.vo;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/10
 */
public class InManagerVO {
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

    @Override
    public String toString() {
        return "InManagerVO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
