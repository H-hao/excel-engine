package com.hh.excel.engine.exception;

/**
 * 超出 cell 阈值的异常
 * Created by huanghao on 2017/7/11.
 */
public class OutOfSheetThresholdException extends OutOfThresholdException {

    private static final long serialVersionUID = 4011962778334175069L;

    public OutOfSheetThresholdException() {
        super();
    }

    public OutOfSheetThresholdException(String s) {
        super(s);
    }

    public OutOfSheetThresholdException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfSheetThresholdException(Throwable cause) {
        super(cause);
    }
}
