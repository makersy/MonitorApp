package com.sust.monitorapp.common;

/**
 * Created by yhl on 2020/3/4.
 */
public enum ResponseCode {

    SUCCESS("101", "提交成功"),

    /**
     * 登录
     * 100   登录成功
     * 101   密码不正确
     * 102   用户不存在
     * 103   登录失败，再次尝试
     */
    LOGIN_SUCCESS("101", "登录成功"),
    PASSWORD_WRONG("101", "密码不正确"),
    USER_NOT_EXIST("102", "用户不存在"),
    LOGIN_FAIL("103", "登录失败，请再次尝试"),

    /**
     * 设备管理
     */
    DEV_NOT_EXIST();

    private String code;

    private String msg;

    ResponseCode() {
        this("null", "null");
    }

    ResponseCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
