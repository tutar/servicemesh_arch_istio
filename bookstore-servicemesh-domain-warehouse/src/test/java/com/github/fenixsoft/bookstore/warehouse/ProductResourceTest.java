package com.github.fenixsoft.bookstore.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fenixsoft.bookstore.domain.warehouse.Product;
import com.github.fenixsoft.bookstore.domain.warehouse.Stockpile;
import com.github.fenixsoft.bookstore.resource.HttpUtil;
import com.github.fenixsoft.bookstore.resource.JAXRSResourceBase;
import okhttp3.Response;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author icyfenix@gmail.com
 * @date 2020/4/21 17:40
 **/
class ProductResourceTest extends JAXRSResourceBase {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAllProducts() {
        assertOK(get("/products"));
    }

    @Test
    void getProduct() throws IOException {
        assertOK(get("/products/1"));
        assertNoContent(get("/products/10086"));
        Product book = objectMapper.readValue(get("/products/1").body().string(),Product.class);
        assertEquals("深入理解Java虚拟机（第3版）", book.getTitle());
    }

    @Test
    void updateProduct() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final Product book =  objectMapper.readValue(get("/products/1").body().string(),Product.class);
        book.setTitle("深入理解Java虚拟机（第4版）");
        assertForbidden(put("/products", book));
        authenticatedScope(() -> assertOK(put("/products", book)));
        Product modifiedBook = objectMapper.readValue(get("/products/1").body().string(),Product.class);
        assertEquals("深入理解Java虚拟机（第4版）", modifiedBook.getTitle());
    }

    @Test
    void createProduct() {
        final Product book = new Product();
        book.setTitle("new book");
        book.setPrice(50.0);
        book.setRate(8.0f);
        assertForbidden(post("/products", book));
        authenticatedScope(() -> {
            try {
                Response response = post("/products", book);
                assertOK(response);
                Product fetchBook = objectMapper.readValue(response.body().string(),Product.class);
                assertEquals(book.getTitle(), fetchBook.getTitle());
                assertNotNull(fetchBook.getId());
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Test
    void removeProduct() throws JSONException {
        int number = jsonArray(get("/products")).length();
        assertForbidden(delete("/products/1"));
        //authenticatedScope(() -> assertOK(delete("/products/1")));
        //assertEquals(number - 1, jsonArray(get("/products")).length());
    }

    @Test
    void updateAndQueryStockpile() {
        authenticatedScope(() -> {
            assertOK(patch("/products/stockpile/1?amount=20"));
            Stockpile stockpile = null;
            try {
                stockpile = objectMapper.readValue(get("/products/stockpile/1").body().string(), Stockpile.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assertEquals(20, stockpile.getAmount());
        });
    }
}
