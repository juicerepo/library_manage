package com.gondor.isildur.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

public class ReturnObject extends JSONObject {

    public ReturnObject(int resultCode, String resultMsg) {
        this.put("code", resultCode);
        this.put("message", resultMsg);
    }

    public <E> ReturnObject(E data) {
        this.put("code", 0);
        this.put("message", "Success");
        this.put("data", data);
    }

    public <E> ReturnObject(int resultCode, String resultMsg, E data) {
        this.put("code", resultCode);
        this.put("message", resultMsg);
        this.put("data", data);
    }

    // 添加新方法：创建带错误详情的返回对象
    public static <E> ReturnObject validationError(int resultCode, String resultMsg, E errors) {
        ReturnObject obj = new ReturnObject(resultCode, resultMsg);
        obj.put("errors", errors);
        return obj;
    }


    public void send(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.println(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void successRespond(HttpServletResponse response) {
        respond(response, 0, "Success");
    }

    public static <E> void successRespond(HttpServletResponse response, E data) {
        respond(response, 0, "Success", data);
    }

    public static void respond(HttpServletResponse response, int resultCode, String resultMsg) {
        ReturnObject res = new ReturnObject(resultCode, resultMsg);
        res.send(response);
    }

    public static <E> void respond(HttpServletResponse response, int resultCode, String resultMsg, E data) {
        ReturnObject res = new ReturnObject(resultCode, resultMsg, data);
        res.send(response);
    }
}
