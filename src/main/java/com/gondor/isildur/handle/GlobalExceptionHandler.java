package com.gondor.isildur.handle;

import com.alibaba.fastjson.JSONObject;
import com.gondor.isildur.DTO.BaseDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JSONObject handleException(Exception e) {
        return new BaseDTO().build(500, "服务器错误: " + e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public JSONObject handleDataException(DataIntegrityViolationException e) {
        return new BaseDTO().build(400, "数据验证失败");
    }
}
