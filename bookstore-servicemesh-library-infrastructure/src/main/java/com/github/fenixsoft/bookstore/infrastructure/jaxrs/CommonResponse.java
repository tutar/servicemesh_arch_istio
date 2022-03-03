package com.github.fenixsoft.bookstore.infrastructure.jaxrs;

import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.function.Consumer;

public class CommonResponse<T> implements Serializable {
    private static final long serialVersionUID = -750644833749014619L;
    private static final Logger log = LoggerFactory.getLogger(CommonResponse.class);
    /**
     * 状态码
     * 若该值返回给前端，会转为http code
     * 默认成功 200，失败400，
     */
    private int status;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 数据
     */
    private T data;
    private String message;

    public CommonResponse() {
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return this.status;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return this.message;
    }

    public void setError(int code,String error) {
        this.status =code;
        this.success = false;
        this.message = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("success", this.success).add("data", this.data).add("error", this.message).omitNullValues().toString();
    }

    public static <T> CommonResponse<T> ok(T data) {
        CommonResponse resp = new CommonResponse();
        resp.setSuccess(true);
        resp.setStatus(200);
        resp.setData(data);
        return resp;
    }

    public static <T> CommonResponse<T> ok() {
        return ok(null);
    }

    public static <T> CommonResponse<T> failure(String error) {
        CommonResponse resp = new CommonResponse();
        resp.setError(400,error);
        return resp;
    }
    public static <T> CommonResponse<T> fail(String error, int code) {
        CommonResponse resp = new CommonResponse();
        resp.setError(code,error);
        return resp;
    }

    /**
     * 执行操作，并根据操作是否成功返回给客户端相应信息
     * 封装了在服务端接口中很常见的执行操作，成功返回成功标志、失败返回失败标志的通用操作，用于简化编码
     */
    public static CommonResponse op(Runnable executor) {
        return op(executor, e -> log.error(e.getMessage(), e));
    }

    /**
     * 执行操作（带自定义的失败处理），并根据操作是否成功返回给客户端相应信息
     * 封装了在服务端接口中很常见的执行操作，成功返回成功标志、失败返回失败标志的通用操作，用于简化编码
     */
    public static CommonResponse op(Runnable executor, Consumer<Exception> exceptionConsumer) {
        try {
            executor.run();
            return CommonResponse.ok();
        } catch (Exception e) {
            exceptionConsumer.accept(e);
            return CommonResponse.failure(e.getMessage());
        }
    }

}
