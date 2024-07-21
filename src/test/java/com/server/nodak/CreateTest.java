package com.server.nodak;

import com.server.nodak.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CreateTest {


    @Autowired
    TokenProvider provider;

    @Test
    void test() {
        System.out.println(provider.createAccessToken("2"));
    }


}
