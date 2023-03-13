package com.aimin.boot.utils;

/**
 * @author Zhang Liqiang
 * @email 18945085165@163.com
 * @date 2021/11/30
 * @description:
 **/
public class AiminException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public AiminException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public AiminException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public AiminException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public AiminException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
