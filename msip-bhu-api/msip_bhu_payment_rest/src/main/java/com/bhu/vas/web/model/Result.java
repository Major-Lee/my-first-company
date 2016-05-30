package com.bhu.vas.web.model;

/**
 * @author: Pengyu
 */
public class Result {
    public static final Result SUCCESS = new Result(true, "操作成功");
    public static final Result ERROR = new Result(false, "操作失败");

    boolean success = true;
    String message="";
    String error="";
    Object payload;

    public Result(boolean success) {
        this.success = success;
    }

    public Result(String message) {
        this(true,message);
    }
    public Result(String message, Object payload) {
        this(true,message,payload);
    }
    public Result(String message, String error, Object payload) {
        this(false,message,error,payload);
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Result(boolean success, String message, Object payload) {
        this.success = success;
        this.message = message;
        this.payload = payload;
    }

    public Result(boolean success, String message, String error, Object payload) {
        this.success = success;
        this.message = message;
        this.error = error;
        this.payload = payload;
    }

    public Result(boolean success, String message, String error) {
        this.success = success;
        this.message = message;
        this.error = error;
    }

    public Object getPayload() {

        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }


    public Result() {
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}

