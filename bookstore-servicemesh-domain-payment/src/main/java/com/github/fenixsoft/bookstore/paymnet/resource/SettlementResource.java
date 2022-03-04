/*
 * Copyright 2012-2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. More information from:
 *
 *        https://github.com/fenixsoft
 */

package com.github.fenixsoft.bookstore.paymnet.resource;

import com.github.fenixsoft.bookstore.dto.Item;
import com.github.fenixsoft.bookstore.dto.Settlement;
import com.github.fenixsoft.bookstore.paymnet.application.PaymentApplicationService;
import com.github.fenixsoft.bookstore.paymnet.domain.Payment;
import com.github.fenixsoft.bookstore.paymnet.domain.client.ProductServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 结算清单相关的资源
 *
 * @author icyfenix@gmail.com
 * @date 2020/3/12 11:23
 **/
@RequestMapping("/restful/settlements")
@RestController
@Validated
public class SettlementResource {

    @Autowired
    private PaymentApplicationService service;

    @Autowired
    private ProductServiceClient productServiceClient;
    /**
     * 提交一张交易结算单，根据结算单中的物品，生成支付单
     */
    @PostMapping
    public Payment executeSettlement(@Valid @RequestBody Settlement settlement) {
        for(Item item:settlement.getItems()){
            if(productServiceClient.queryStockpile(item.getProductId()).getAmount() < item.getAmount()){
                throw new UnsupportedOperationException("商品库存不足");
            }
        }

        return service.executeBySettlement(settlement);
    }

}
