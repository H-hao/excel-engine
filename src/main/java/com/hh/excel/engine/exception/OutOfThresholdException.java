package com.hh.excel.engine.exception;

/**
 * 阈值异常的抽象类
 * Created by huanghao on 2017/7/11.
 */
public abstract class OutOfThresholdException extends RuntimeException {
    private static final long serialVersionUID = -5295140494645931189L;

    public OutOfThresholdException() {
        super();
    }

    public OutOfThresholdException(String s) {
        super(s);
    }

    public OutOfThresholdException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfThresholdException(Throwable cause) {
        super(cause);
    }

}
