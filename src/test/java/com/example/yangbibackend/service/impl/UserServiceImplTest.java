package com.example.yangbibackend.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    UserServiceImpl userService;

    @Test
    void testRegister() {
        userService.register("admin","12345678","12345678");
    }

}