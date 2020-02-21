package com.mg.community.advice;

import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CommonErrorCode;
import com.mg.community.exception.CommunityErrorCode;
import com.mg.community.exception.CustomizeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@ResponseBody
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    Object handler(Throwable e, HttpServletRequest request,
                   HttpServletResponse response) {
//        String contentType = request.getContentType();
        ResultDTO resultDTO = new ResultDTO();

        if (e instanceof CustomizeException) {
            CustomizeException curException = (CustomizeException) e;
            String errorType = ResultDTO.returnErrorType(curException);
            switch (errorType) {
                case "CommonErrorCode":
                    // 登录过期
                    if (curException.getCode() == CommonErrorCode.TOKEN_HAS_EXPIRED.getCode()
                            || curException.getCode() == CommonErrorCode.TOKEN_SESSION_HAS_EXPIRED.getCode())
                        response.setStatus(403);
                    else if (curException.getCode() == CommonErrorCode.TOKEN_NULL.getCode() ||
                            curException.getCode() == CommonErrorCode.TOKEN_INVALID.getCode()||
                            curException.getCode() == CommonErrorCode.TOKEN_USER_NOT_FOUND.getCode()){
                        // 未登录
                        response.setStatus(401);
                    }else{
                        response.setStatus(500);
                    }
                    break;
                case "CommunityErrorCode":
                    response.setStatus(500);
                    break;
                default:
                    response.setStatus(500);
                    break;
            }
            return ResultDTO.errorOf(curException);
        } else {
            response.setStatus(500);
            return ResultDTO.errorOf(CommonErrorCode.GENERAL_ERROR);
        }
    }
}
