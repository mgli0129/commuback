package com.mg.community.dto;

import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.exception.CustomizeException;
import lombok.Data;

@Data
public class ResultDTO<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(CustomizeErrorCode.SUCCESS.getCode());
        resultDTO.setMessage(CustomizeErrorCode.SUCCESS.getMessage());
        resultDTO.setData(t);
        return resultDTO;
    }

    public static ResultDTO errorOf(String code, String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }
    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        return errorOf(errorCode.getCode(), errorCode.getMessage());
    }
    public static ResultDTO errorOf(CustomizeException e) {
        return errorOf(e.getCode(),e.getMessage());
    }

    /**
     * 附带自定义错误内容
     * @param errorCode
     * @param additionalmsg
     * @return
     */
    public static ResultDTO errorOf(CustomizeErrorCode errorCode, String additionalmsg) {
        String errorMsg = errorCode.getMessage() + "---" +additionalmsg;
        return errorOf(errorCode.getCode(),errorMsg);
    }

    public static ResultDTO okOf() {
        return errorOf(CustomizeErrorCode.SUCCESS);
    }
}
