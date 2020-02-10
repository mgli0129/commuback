package com.mg.community.advice;

import com.alibaba.fastjson.JSON;
import com.mg.community.dto.ResultDTO;
import com.mg.community.exception.CustomizeErrorCode;
import com.mg.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
@ResponseBody
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    Object handler(Throwable e,HttpServletRequest request,
                         HttpServletResponse response){
        String contentType = request.getContentType();
        ResultDTO resultDTO = new ResultDTO();
        if("application/json".equals(contentType)){
            if(e instanceof CustomizeException){
                return ResultDTO.errorOf((CustomizeException) e);
            }else{
                return ResultDTO.errorOf((CustomizeErrorCode.GENERAL_ERROR));
            }
        }else{
            if(e instanceof CustomizeException){
                resultDTO.setCode(((CustomizeException) e).getCode());
                resultDTO.setMessage(e.getMessage());
                return resultDTO;
            }else{
                resultDTO.setCode(CustomizeErrorCode.GENERAL_ERROR.getCode());
                resultDTO.setMessage(CustomizeErrorCode.GENERAL_ERROR.getMessage());
                return resultDTO;
            }
        }
    }

}
