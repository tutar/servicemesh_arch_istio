package com.github.fenixsoft.bookstore.warehouse.resource;

import com.github.fenixsoft.bookstore.domain.warehouse.DeliveredStatus;
import com.github.fenixsoft.bookstore.domain.warehouse.Stockpile;
import com.github.fenixsoft.bookstore.infrastructure.jaxrs.CommonResponse;
import com.github.fenixsoft.bookstore.warehouse.application.StockpileApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.*;

/**
 * 库存相关的资源
 *
 * @author icyfenix@gmail.com
 * @date 2020/4/19 21:40
 **/

@RequestMapping("/restful/products")
@RestController
@CacheConfig(cacheNames = "resource.product")
public class StockpileResource {

    @Autowired
    private StockpileApplicationService service;

    /**
     * 将指定的产品库存调整为指定数额
     */
    @PatchMapping("/stockpile/{productId}")
    public CommonResponse updateStockpile(@PathVariable("productId") Integer productId, @RequestParam("amount") Integer amount) {
        return CommonResponse.op(() -> service.setStockpileAmountByProductId(productId, amount));
    }

    /**
     * 根据产品查询库存
     */
    @GetMapping("/stockpile/{productId}")
    public Stockpile queryStockpile(@PathVariable("productId") Integer productId) {
        return service.getStockpile(productId);
    }

    // 以下是开放给内部微服务调用的方法

    /**
     * 将指定的产品库存调整为指定数额
     */
    @PatchMapping("/stockpile/delivered/{productId}")
    public CommonResponse setDeliveredStatus(@PathVariable("productId") Integer productId, @RequestParam("status") DeliveredStatus status, @RequestParam("amount") Integer amount) {
        return CommonResponse.op(() -> service.setDeliveredStatus(productId, status, amount));
    }
}
