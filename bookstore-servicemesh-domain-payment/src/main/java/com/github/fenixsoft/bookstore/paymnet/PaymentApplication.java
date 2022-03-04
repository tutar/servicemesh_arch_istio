package com.github.fenixsoft.bookstore.paymnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.github.fenixsoft.bookstore"})
@EnableFeignClients(basePackages = {"com.github.fenixsoft.bookstore.domain.security",
        "com.github.fenixsoft.bookstore.paymnet.domain.client"})
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

}
