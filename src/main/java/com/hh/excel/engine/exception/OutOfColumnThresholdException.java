package com.hh.excel.engine.exception;

/**
 * 超出 cell 阈值的异常
 * Created by huanghao on 2017/7/11.
 */
public class OutOfColumnThresholdException extends OutOfThresholdException {

    private static final long serialVersionUID = -4160093591265664602L;

    public OutOfColumnThresholdException() {
        super();
    }

    public OutOfColumnThresholdException(String s) {
        super(s);
    }

    public OutOfColumnThresholdException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfColumnThresholdException(Throwable cause) {
        super(cause);
    }
}
