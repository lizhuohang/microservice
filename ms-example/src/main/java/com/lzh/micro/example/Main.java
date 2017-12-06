package com.lzh.micro.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: lizhuohang
 * @Date: Created in 16:04 17/12/6
 */
@SpringBootApplication(scanBasePackages = "com.lzh.micro")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
