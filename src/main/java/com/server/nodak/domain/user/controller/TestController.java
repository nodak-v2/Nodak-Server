package com.server.nodak.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/environment")
@RequiredArgsConstructor
public class TestController {
    private final Environment env;

    // 배포되면 삭제 예정
    @GetMapping
    public String getEnv(@RequestParam("q") String q) {
        String property = env.getProperty(q);
        return property == null ? "None" : property;
    }
}
