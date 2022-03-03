package com.github.fenixsoft.bookstore.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 单元测试基类
 * <p>
 * 提供对JAX-RS资源的HTTP访问方法、登录授权、JSON字符串访问等支持
 *
 * @author icyfenix@gmail.com
 * @date 2020/4/6 19:32
 **/
@SpringBootTest(properties = "spring.cloud.config.enabled:false", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JAXRSResourceBase extends DBRollbackBase {


    private static JsonMapper JSON_MAPPER= new JsonMapper();

    @Value("${local.server.port}")
    protected int port;

    private String accessToken = null;

    protected String getUrl(String path){
        return  "http://localhost:" + port + "/restful"+path;
    }

    protected Map<String,String> getHeader(){
        // 创建一个 Headers.Builder
        Map<String,String> header=new HashMap<>(3);
        header.put("accept", org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        header.put("content-type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        if (accessToken != null) {
            header.put("Authorization", "bearer " + accessToken);
        }
        return header;
    }


    protected JSONObject json(Response response) throws JSONException {
        try {
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected JSONArray jsonArray(Response response) throws JSONException {
        try {
            return new JSONArray(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单元测试中登陆固定使用icyfenix这个用户
     */
    protected void login() {
        // 这是一个50年后才会过期的令牌，囧
        // HS256 JWT
        // accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJpY3lmZW5peCIsInNjb3BlIjpbIkJST1dTRVIiXSwiZXhwIjozMTY0MjYyNDMwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImp0aSI6IjZmMWJhOGUwLTVkMGMtNGIxMC1iM2I3LTI0MDhmZTFlNzBhZSIsImNsaWVudF9pZCI6ImJvb2tzdG9yZV9mcm9udGVuZCIsInVzZXJuYW1lIjoiaWN5ZmVuaXgifQ.TmbYv6_OQlJg8ViEW5406UzNzNKjPSronrksjZ_epKM";
        // RS256 JWT
        accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJpY3lmZW5peCIsInNjb3BlIjpbIkJST1dTRVIiXSwiZXhwIjozMTcxNzc5NTc1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiLCJST0xFX0FETUlOIl0sImp0aSI6IjZmMzM1ODRhLTE0OTgtNGJhYi05ZTcwLTBhN2Y5MDk5YzIwMyIsImNsaWVudF9pZCI6ImJvb2tzdG9yZV9mcm9udGVuZCIsInVzZXJuYW1lIjoiaWN5ZmVuaXgifQ.VeufGCgg_royfFeFVNJgV8zSQETt9PCGDQS_XCsv4yiWxKQvvWYVqx_714VoI4LBsdGSVDSdYfClvIOMHK7RS6qI1xiw7QHfhreFqYmLBYKvbhNJmmfZFw8lLeaQ68XDpFX1BjkIveyvURFCedCffqhU8DgGSxwHZVJ61mSnqt6OE8JDXKv18gP7rmsg4xDwkvyy-CL9kBKEsSx9iZ8Dm14O0EYPenyhXW7DESPOL63SEusjkjTdK5z_G-vBAeuMkAJZlh9a4DtWBnI5PMjfl_kBcgYePOrA1GypZqH7mgHXGybLEV5VpGaOZuJCNRWCjT5NgF4NhlgAEwMnctdMJg";
    }


    protected void logout() {
        accessToken = null;
    }

    protected void authenticatedScope(Runnable runnable) {
        try {
            login();
            runnable.run();
        } finally {
            logout();
        }
    }

    protected <T> T authenticatedGetter(Supplier<T> supplier) {
        try {
            login();
            return supplier.get();
        } finally {
            logout();
        }
    }

    protected Response get(String path) {

        Request request = new Request.Builder()
                .url(getUrl(path))
                .headers(Headers.of(getHeader()))
                .build();
        return HttpUtil.doRequest(request);
    }

    protected Response delete(String path) {
        FormBody body = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(getUrl(path))
                .delete(body)
                .headers(Headers.of(getHeader()))
                .build();
        return HttpUtil.doRequest(request);
    }

    protected Response post(String path,Object entity) {

        try {

            // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
            RequestBody requestBody = FormBody.create(okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE),
                    JSON_MAPPER.writeValueAsString(entity));

            Request request = new Request.Builder()
                    .url(getUrl(path))
                    .headers(Headers.of(getHeader()))
                    .post(requestBody)
                    .build();

            return HttpUtil.doRequest(request);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Response put(String path, Object entity) {
        try {
            // 或者 FormBody.create 方式，只适用于接口只接收文件流的情况
            RequestBody requestBody = FormBody.create(okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_VALUE),
                    JSON_MAPPER.writeValueAsString(entity));

            Request request = new Request.Builder()
                    .url(getUrl(path))
                    .headers(Headers.of(getHeader()))
                    .put(requestBody)
                    .build();

            return HttpUtil.doRequest(request);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Response patch(String path) {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "MUST_BE_PRESENT");
        Request request = new Request.Builder()
                .url(getUrl(path))
                .headers(Headers.of(getHeader()))
                .patch(body)
                .build();

        return HttpUtil.doRequest(request);
    }

    protected static void assertOK(Response response) {

        assertEquals(HttpStatus.OK.value(), response.code(), "期望HTTP Status Code应为：200/OK");
    }

    protected static void assertNoContent(Response response) {
        assertEquals(HttpStatus.NO_CONTENT.value(), response.code(), "期望HTTP Status Code应为：204/NO_CONTENT");
    }

    protected static void assertBadRequest(Response response) {
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.code(), "期望HTTP Status Code应为：400/BAD_REQUEST");
    }

    protected static void assertForbidden(Response response) {
        // istio环境中权限控制已经从代码实现挪到sidecar实现，单元测试上不会出现403了，这个要在集成测试环境中验证
        // assertEquals(HttpStatus.FORBIDDEN.value(), response.code(), "期望HTTP Status Code应为：403/FORBIDDEN");
    }

    protected static void assertServerError(Response response) {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.code(), "期望HTTP Status Code应为：500/INTERNAL_SERVER_ERROR");
    }

    protected static void assertNotFound(Response response) {
        assertEquals(HttpStatus.NOT_FOUND.value(), response.code(), "期望HTTP Status Code应为：404/NOT_FOUND");
    }


}
