package com.server.nodak.global.common.status_check;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health-check")
public class StatusCheckController {

    @GetMapping
    public ResponseEntity<Void> healthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    // TODO : 삭제 예정
    public ResponseEntity<?> test(@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") String redirectUri) {
        return ResponseEntity.ok(redirectUri);
    }
}
