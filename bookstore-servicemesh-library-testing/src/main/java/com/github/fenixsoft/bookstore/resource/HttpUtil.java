package com.github.fenixsoft.bookstore.resource;

import okhttp3.*;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * 封装Okhttp
 * @author c-nianyz
 */
public class HttpUtil {

    private static volatile OkHttpClient okHttpClient;

    public static OkHttpClient getOkHttpClient(boolean isHttps){
        if(okHttpClient==null){
            synchronized (HttpUtil.class){
                if(okHttpClient==null){
                    okHttpClient = new OkHttpClient.Builder().build();
                }
            }
        }
        return okHttpClient;
    }


    public static Response doRequest(Request request) {
        Call call =  getOkHttpClient( request.url().isHttps()).newCall(request);
        try {
            return call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get 请求
     * @param url 请求URL
     * @return
     * @throws Exception
     */
    public static Response doGet(String url) throws Exception {
        return doGet(url, new HashMap<>());
    }


    /**
     * get 请求
     * @param url 请求URL
     * @param query 携带参数参数
     * @return
     * @throws Exception
     */
    public static Response doGet(String url, Map<String, Object> query) {

        return doGet(url, new HashMap<>(), query);
    }
    public static Response doGet(String url, Map<String, Object> header, Map<String, Object> query){

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 HttpUrl.Builder
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder =getHeader(request,header);

        // 装载请求的参数
        Iterator<Map.Entry<String, Object>> queryIterator = query.entrySet().iterator();
        queryIterator.forEachRemaining(e -> {
            urlBuilder.addQueryParameter(e.getKey(), (String) e.getValue());
        });

        // 设置自定义的 builder
        // 因为 get 请求的参数，是在 URL 后面追加  http://xxxx:8080/user?name=xxxx?sex=1
        builder.url(urlBuilder.build()).headers(headerBuilder.build());
        return doRequest(builder.build());
    }


    /**
     * post 请求， 请求参数 并且 携带文件上传
     * @param url
     * @param header
     * @param parameter
     * @return
     * @throws Exception
     */
    public static Response doPost(String url, Map<String, Object> header, Map<String, Object> parameter) throws Exception {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder =getHeader(request,header);

        // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();

        // 状态请求参数
        Iterator<Map.Entry<String, Object>> queryIterator = parameter.entrySet().iterator();
        queryIterator.forEachRemaining(e -> {
            requestBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
        });

        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).post(requestBuilder.build());

        // 然后再 build 一下
        return doRequest(builder.build());
    }


    /**
     * post 请求， 请求参数 并且 携带文件上传
     * @param url
     * @param header
     * @param parameter
     * @return
     * @throws Exception
     */
    public static Response doPut(String url, Map<String, Object> header, Map<String, Object> parameter) {

        // 创建一个请求 Builder
        Request.Builder builder = new Request.Builder();
        // 创建一个 request
        Request request = builder.url(url).build();

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder =getHeader(request,header);

        // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
        // RequestBody requestBody = FormBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder();

        // 状态请求参数
        Iterator<Map.Entry<String, Object>> queryIterator = parameter.entrySet().iterator();
        queryIterator.forEachRemaining(e -> {
            requestBuilder.addFormDataPart(e.getKey(), (String) e.getValue());
        });


        // 设置自定义的 builder
        builder.headers(headerBuilder.build()).put(requestBuilder.build());

        // 然后再 build 一下
        return doRequest(builder.build());
    }


    private static Headers.Builder getHeader(Request request,Map<String, Object> header){

        // 创建一个 Headers.Builder
        Headers.Builder headerBuilder = request.headers().newBuilder();
        header.put("accept", MediaType.APPLICATION_JSON);
        header.put("content-type", MediaType.APPLICATION_JSON);
        // 装载请求头参数
        Iterator<Map.Entry<String, Object>> headerIterator = header.entrySet().iterator();
        headerIterator.forEachRemaining(e -> {
            headerBuilder.add(e.getKey(), (String) e.getValue());
        });
        return headerBuilder;
    }

    public static Map<String,String> getMap(Object object){
        try {
            return BeanUtils.describe(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
