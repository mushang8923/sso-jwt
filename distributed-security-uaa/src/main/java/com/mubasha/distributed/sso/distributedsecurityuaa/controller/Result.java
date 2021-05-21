package com.mubasha.distributed.sso.distributedsecurityuaa.controller;

/**
 * @author: fengchangxin
 * @date: 2020/11/17:21:33
 * @description:
 **/
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Result error(String msg){
        Result result=new Result();
        result.setCode(-1);
        result.setMsg(msg);
        return result;
    }

    public static Result success(Object data){
        Result result=new Result();
        result.setCode(-1);
        result.setData(data);
        return result;
    }


}
