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

package com.github.fenixsoft.bookstore.account.resource;

import com.github.fenixsoft.bookstore.account.applicaiton.AccountApplicationService;
import com.github.fenixsoft.bookstore.account.domain.validation.AuthenticatedAccount;
import com.github.fenixsoft.bookstore.account.domain.validation.NotConflictAccount;
import com.github.fenixsoft.bookstore.account.domain.validation.UniqueAccount;
import com.github.fenixsoft.bookstore.domain.account.Account;
import com.github.fenixsoft.bookstore.infrastructure.jaxrs.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 用户资源
 * <p>
 * 对客户端以Restful形式暴露资源，提供对用户资源{@link Account}的管理入口
 *
 * @author icyfenix@gmail.com
 * @date 2020/3/6 20:52
 **/
@RequestMapping("/restful/accounts")
@RestController
@CacheConfig(cacheNames = "resource.account")
public class AccountResource {

    @Autowired
    private AccountApplicationService service;

    /**
     * 根据用户名称获取用户详情
     */
    @GetMapping("/{username}")
    @Cacheable(key = "#username")
    public Account getUser(@PathVariable("username") String username, HttpServletResponse response) {
        Account account = service.findAccountByUsername(username);
        if(account==null){
            response.setStatus(HttpStatus.NO_CONTENT.value());
        }
        return account;
    }

    /**
     * 创建新的用户
     */
    @PostMapping
    @CacheEvict(key = "#user.username")
    public CommonResponse createUser(@Valid @UniqueAccount @RequestBody Account user) {
        return CommonResponse.op(() -> service.createAccount(user));
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    @CacheEvict(key = "#user.username")
    public CommonResponse updateUser(@Valid @AuthenticatedAccount @NotConflictAccount @RequestBody Account user) {
        return CommonResponse.op(() -> service.updateAccount(user));
    }
}
