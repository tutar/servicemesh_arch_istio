package com.github.fenixsoft.bookstore.domain.security;

import com.github.fenixsoft.bookstore.domain.account.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户信息的远程服务客户端
 * 各个工程都可能涉及取当前用户之类的操作，将此客户端放到基础包以便通用
 *
 * @author icyfenix@gmail.com
 * @date 2020/4/18 12:33
 **/
@FeignClient(name = "account",url = "account")
public interface AccountServiceClient {

    @GetMapping("/restful/accounts/{username}")
    Account findByUsername(@PathVariable("username") String username);
}
