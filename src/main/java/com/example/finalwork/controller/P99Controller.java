package com.example.finalwork.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class P99Controller {

    @GetMapping("/api/test")
    public String testEndpoint() {
        // 模拟业务逻辑处理
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, World!";
    }
}