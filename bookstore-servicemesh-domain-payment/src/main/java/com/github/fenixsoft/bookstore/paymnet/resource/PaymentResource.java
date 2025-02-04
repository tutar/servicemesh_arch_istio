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

import com.github.fenixsoft.bookstore.domain.account.Account;
import com.github.fenixsoft.bookstore.infrastructure.jaxrs.CommonResponse;
import com.github.fenixsoft.bookstore.paymnet.application.PaymentApplicationService;
import com.github.fenixsoft.bookstore.paymnet.domain.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 支付单相关的资源
 *
 * @author icyfenix@gmail.com
 * @date 2020/3/13 12:52
 **/
@RequestMapping("/restful/pay")
@RestController
public class PaymentResource {

    @Autowired
    private PaymentApplicationService service;

    /**
     * 修改支付单据的状态
     */
    @PatchMapping("/{payId}")
    public CommonResponse updatePaymentState(@PathVariable("payId") String payId, @RequestParam("state") Payment.State state) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updatePaymentStateAlias(payId, account.getId(), state);
    }

    /**
     * 修改支付单状态的GET方法别名
     * 考虑到该动作要由二维码扫描来触发，只能进行GET请求，所以增加一个别名以便通过二维码调用
     * 这个方法原本应该作为银行支付接口的回调，不控制调用权限（谁付款都行），但都认为是购买用户付的款
     */
    @GetMapping("/modify/{payId}")
    public CommonResponse updatePaymentStateAlias(@PathVariable("payId") String payId, @RequestParam("accountId") Integer accountId, @RequestParam("state") Payment.State state) {
        if (state == Payment.State.PAYED) {
            return CommonResponse.op(() -> service.accomplishPayment(accountId, payId));
        } else {
            CommonResponse response = CommonResponse.op(() -> service.cancelPayment(payId));
            return response;
        }
    }

}
