package com.mg.community.dto;

import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CommunityErrorCode;
import com.mg.community.exception.CustomizeException;
import lombok.Data;

import java.util.Map;

@Data
public class ResultDTO<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ResultDTO okOf(T t){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(CommonErrorCode.SUCCESS.getCode());
        resultDTO.setMessage(CommonErrorCode.SUCCESS.getMessage());
        resultDTO.setData(t);
        return resultDTO;
    }

    public static ResultDTO errorOf(String code, String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }
    public static ResultDTO errorOf(CommunityErrorCode errorCode) {
        return errorOf(errorCode.getCode(), errorCode.getMessage());
    }
    public static ResultDTO errorOf(CommonErrorCode errorCode) {
        return errorOf(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 附带自定义错误内容
     * @param e
     * @return
     */
    public static ResultDTO errorOf(CustomizeException e) {

        Map<String, String> addition = e.getAddition();

        if(addition != null){
            //将自定义信息填入错误信息里
            for (Map.Entry<String, String> entry : addition.entrySet()) {
                e.getMessage().replace(entry.getKey(),entry.getValue());
            }
        }
        return errorOf(e.getCode(),e.getMessage());
    }

    public static ResultDTO okOf() {
        return errorOf(CommonErrorCode.SUCCESS);
    }

    /**
     * 返回捕获到的异常类别；
     * 以定义好的异常枚举类作为返回类型；
     *  CommonErrorCode - 当前异常属于CommonErrorCode定义里的错误；
     *  CommunityErrorCode - 当前异常属于CommunityErrorCode定义里的错误；
     *  NONE - 当前异常没有定义异常枚举类；
     * @param e
     * @return
     */
    public static String returnErrorType(CustomizeException e){

        String code = e.getCode();
        for (CommonErrorCode err : CommonErrorCode.values()) {
            if (code.equals(err.getCode())) {
                return "CommonErrorCode";
            }
        }
        for (CommunityErrorCode err : CommunityErrorCode.values()) {
            if (code.equals(err.getCode())) {
                return "CommunityErrorCode";
            }
        }
        return "NONE";
    }

}
