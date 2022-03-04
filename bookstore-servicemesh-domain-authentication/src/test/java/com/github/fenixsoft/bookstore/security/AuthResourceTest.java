package com.github.fenixsoft.bookstore.security;

import com.github.fenixsoft.bookstore.resource.HttpUtil;
import com.github.fenixsoft.bookstore.resource.JAXRSResourceBase;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author icyfenix@gmail.com
 * @date 2020/4/7 16:47
 **/
public class AuthResourceTest extends JAXRSResourceBase {

    @Test
    void refreshToken() throws JSONException {
        String prefix = "http://localhost:" + port + "/oauth/token?";
        String url = prefix + "username=icyfenix&password=MFfTW3uNI4eqhwDkG7HP9p2mzEUu%2Fr2&grant_type=password&client_id=bookstore_frontend&client_secret=bookstore_secret";
        Response resp = HttpUtil.doRequest(new Request.Builder().url(url).build());
        String refreshToken = json(resp).getString("refresh_token");
        url = prefix + "refresh_token=" + refreshToken + "&grant_type=refresh_token&client_id=bookstore_frontend&client_secret=bookstore_secret";
        resp = HttpUtil.doRequest(new Request.Builder().url(url).build());
        String accessToken = json(resp).getString("access_token");
        Assertions.assertNotNull(accessToken);
    }

}
