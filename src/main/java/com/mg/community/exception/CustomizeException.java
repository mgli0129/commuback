package com.mg.community.exception;

import java.util.Map;

public class CustomizeException extends RuntimeException {

    //错误信息码
    private String code;
    //错误信息
    private String message;
    //错误信息附加内容
    private Map<String, String> addition;

    /**
     * 不带附加信息
     * @param errorCode
     */
    public CustomizeException(ICustomizeErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 附带附加信息
     * @param errorCode
     * @param map
     */
    public CustomizeException(ICustomizeErrorCode errorCode, Map<String, String> map) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.addition = map;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Map<String, String> getAddition() {
        return addition;
    }

}
