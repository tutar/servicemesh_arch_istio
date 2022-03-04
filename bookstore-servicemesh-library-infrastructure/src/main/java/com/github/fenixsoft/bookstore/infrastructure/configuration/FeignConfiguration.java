package com.github.fenixsoft.bookstore.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 设置交互为JAX-RS2方式，实际Feign中的JAX-RS2指的是1.1
 *
 * @author icyfenix@gmail.com
 * @date 2020/4/18 22:38
 **/
@Configuration
public class FeignConfiguration {


    /**
     * 配置认证使用的密码加密算法：BCrypt
     * 由于在Spring Security很多验证器中都要用到{@link PasswordEncoder}的加密，所以这里要添加@Bean注解发布出去
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
