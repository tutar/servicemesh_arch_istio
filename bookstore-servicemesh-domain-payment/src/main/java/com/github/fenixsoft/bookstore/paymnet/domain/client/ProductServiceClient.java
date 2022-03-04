package com.github.fenixsoft.bookstore.paymnet.domain.client;

import com.github.fenixsoft.bookstore.domain.warehouse.DeliveredStatus;
import com.github.fenixsoft.bookstore.domain.warehouse.Product;
import com.github.fenixsoft.bookstore.domain.warehouse.Stockpile;
import com.github.fenixsoft.bookstore.dto.Settlement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 仓库商品和库存相关远程服务
 *
 * @author icyfenix@gmail.com
 * @date 2020/4/19 22:22
 **/
@Profile(value = {"dev","default","kubernetes"})
@FeignClient(name = "warehouse",url = "warehouse")
public interface ProductServiceClient {

    default void replenishProductInformation(Settlement bill) {
        bill.productMap = Stream.of(getProducts()).collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    @GetMapping("/restful/products/{id}")
    Product getProduct(@PathVariable("id") Integer id);

    @GetMapping("/restful/products")
    Product[] getProducts();

    default void decrease(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.DECREASE, amount);
    }

    default void increase(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.INCREASE, amount);
    }

    default void frozen(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.FROZEN, amount);
    }

    default void thawed(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.THAWED, amount);
    }

    @PutMapping("/restful/products/stockpile/delivered/{productId}")
    void setDeliveredStatus(@PathVariable("productId") Integer productId, @RequestParam("status") DeliveredStatus status, @RequestParam("amount") Integer amount);

    @GetMapping("/restful/products/stockpile/{productId}")
    Stockpile queryStockpile(@PathVariable("productId") Integer productId);

}
