package com.hh.excel.engine.exception;

/**
 * 超出 row 阈值的异常
 * Created by huanghao on 2017/7/11.
 */
public class OutOfRowThresholdException extends OutOfThresholdException {

    private static final long serialVersionUID = 643468055850514174L;

    public OutOfRowThresholdException() {
        super();
    }

    public OutOfRowThresholdException(String s) {
        super(s);
    }

    public OutOfRowThresholdException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfRowThresholdException(Throwable cause) {
        super(cause);
    }
}
