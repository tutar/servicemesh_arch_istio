package com.github.fenixsoft.bookstore.paymnet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fenixsoft.bookstore.dto.Item;
import com.github.fenixsoft.bookstore.dto.Purchase;
import com.github.fenixsoft.bookstore.dto.Settlement;
import com.github.fenixsoft.bookstore.paymnet.domain.Payment;
import com.github.fenixsoft.bookstore.resource.JAXRSResourceBase;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author icyfenix@gmail.com
 * @date 2020/4/21 17:04
 **/
class PaymentResourceTest extends JAXRSResourceBase {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static Settlement createSettlement() {
        Settlement settlement = new Settlement();
        Item item = new Item();
        Purchase purchase = new Purchase();
        settlement.setItems(Collections.singletonList(item));
        settlement.setPurchase(purchase);
        item.setAmount(2);
        item.setProductId(1);
        purchase.setLocation("xx rd. zhuhai. guangdong. china");
        purchase.setName("icyfenix");
        purchase.setPay("wechat");
        purchase.setTelephone("18888888888");
        return settlement;
    }

    @Test
    void executeSettlement() {
        final Settlement settlement = createSettlement();
        assertForbidden(post("/settlements", settlement));
        authenticatedScope(() -> {
            Response response = post("/settlements", settlement);
            assertOK(response);
            Payment payment = null;
            try {
                payment = objectMapper.readValue(response.body().string(), Payment.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertNotNull(payment.getPayId());
        });
    }

    @Test
    void updatePaymentState() {
        final Settlement settlement = createSettlement();
        authenticatedScope(() -> {
            try {
                Payment payment =objectMapper.readValue(post("/settlements", settlement).body().string(), Payment.class);
                assertOK(patch("/pay/" + payment.getPayId() + "?state=PAYED"));
                assertServerError(patch("/pay/" + payment.getPayId() + "?state=CANCEL"));
                payment = objectMapper.readValue(post("/settlements", settlement).body().string(), Payment.class);  // another
                assertOK(patch("/pay/" + payment.getPayId() + "?state=CANCEL"));
                assertServerError(patch("/pay/" + payment.getPayId() + "?state=NOT_SUPPORT"));
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Test
    void updatePaymentStateAlias() {
        Payment payment = authenticatedGetter(() -> {
            try {
                return objectMapper.readValue(post("/settlements", createSettlement()).body().string(),Payment.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
        assertOK(get(payment.getPaymentLink()));
    }

}
