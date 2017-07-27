package com.hh.excel.engine.exception;

/**
 * 设置 cell 值时，配置为必需，但值为null时，抛出的异常 TODO 名字怎么取
 * Created by huanghao on 2017/7/11.
 */
public class RequireException extends RuntimeException {

    private static final long serialVersionUID = 1546399195227816737L;

    public RequireException() {
        super();
    }

    public RequireException(String s) {
        super(s);
    }

    public RequireException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequireException(Throwable cause) {
        super(cause);
    }
}
