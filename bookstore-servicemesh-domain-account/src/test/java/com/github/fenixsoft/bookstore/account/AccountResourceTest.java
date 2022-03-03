package com.github.fenixsoft.bookstore.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fenixsoft.bookstore.domain.account.Account;
import com.github.fenixsoft.bookstore.resource.JAXRSResourceBase;
import okhttp3.Response;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author icyfenix@gmail.com
 * @date 2020/4/6 18:52
 **/
class AccountResourceTest extends JAXRSResourceBase {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getUserWithExistAccount() {
        Response resp = get("/accounts/icyfenix");
        assertForbidden(resp);
        authenticatedScope(() -> {
            try {
                Response resp2 = get("/accounts/icyfenix");
                Account icyfenix = null;
                icyfenix = objectMapper.readValue(resp2.body().string(), Account.class);
                assertEquals("icyfenix", icyfenix.getUsername(), "should return user: icyfenix");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void getUserWithNotExistAccount() {
        authenticatedScope(() -> assertNoContent(get("/accounts/nobody")));

    }

    @Test
    void createUser() {
        authenticatedScope(() -> {
            try {
                Account newbee = new Account();
                newbee.setUsername("newbee");
                newbee.setEmail("newbee@github.com");
                assertBadRequest(post("/accounts",  newbee));
                newbee.setTelephone("13888888888");
                newbee.setName("somebody");
                assertNoContent(get("/accounts/newbee"));
                assertOK(post("/accounts", newbee));
                assertOK(get("/accounts/newbee"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void updateUser() {
        authenticatedScope(() -> {
            try {
                Response resp = get("/accounts/icyfenix");
                Account icyfenix = objectMapper.readValue(resp.body().string(), Account.class);
                icyfenix.setName("zhouzhiming");
                assertOK(put("/accounts", icyfenix));
                assertEquals("zhouzhiming", objectMapper.readValue(get("/accounts/icyfenix").body().string(), (Account.class)).getName(), "should get the new name now");
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
