package com.shineyue.certSign.enums;

/**
 * @PackageName: com.shineyue.certSign.enums
 * @Description: TODO
 * @author: 罗绂威
 * @date: wrote on 2019/8/21
 */
public enum StatusEnum {

    // ------------------成功状态码----------------------
    SUCCESS(100000,"请求成功"),

    // -------------------失败状态码----------------------
    // 参数错误
    PARAMS_IS_NULL(200001,"参数为空"),
    PARAMS_NOT_COMPLETE(200002,"参数不全"),
    PARAMS_TYPE_ERROR(200003,"参数类型错误"),
    PARAMS_IS_INVALID(200004,"参数无效"),

    // 用户错误
    USER_NOT_EXIST(300001,"用户不存在"),
    USER_NOT_LOGGED_IN(300002,"用户未登陆"),
    USER_ACCOUNT_ERROR(300003,"用户名或密码错误"),
    USER_ACCOUNT_FORBIDDEN(300004,"用户账户已被禁用"),
    USER_HAS_EXIST(300005,"用户已存在"),

    // 业务错误
    BUSINESS_ERROR(400001,"系统业务出现问题"),

    // 系统错误
    SYSTEM_INNER_ERROR(500001,"系统内部错误"),

    // 数据错误
    DATA_NOT_FOUND(600001,"数据未找到"),
    DATA_IS_WRONG(600002,"数据有误"),
    DATA_ALREADY_EXISTED(600003,"数据已存在"),
    FILE_NOT_FUOND(600005,"文件未找到"),

    // 接口错误
    INTERFACE_INNER_INVOKE_ERROR(700001,"系统内部接口调用异常"),
    INTERFACE_OUTER_INVOKE_ERROR(700002,"系统外部接口调用异常"),
    INTERFACE_FORBIDDEN(700003,"接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(700004,"接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(700005,"接口请求超时"),
    INTERFACE_EXCEED_LOAD(700006,"接口负载过高"),

    // 权限错误
    PERMISSION_NO_ACCESS(800001,"没有访问权限");


    private int statusCode;
    private String statusBody;

    StatusEnum(int statusCode, String statusBody) {
        this.statusCode = statusCode;
        this.statusBody = statusBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusBody() {
        return statusBody;
    }

    public void setStatusBody(String statusBody) {
        this.statusBody = statusBody;
    }
}
