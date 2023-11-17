package com.example.yangbibackend.manager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    @Autowired
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        String userId="1";
        for(int i=0;i<2;i++){
            redisLimiterManager.doRateLimit(userId);
            System.out.println(i);
        }
        Thread.sleep(2000);
        for(int i=0;i<5;i++){
            redisLimiterManager.doRateLimit(userId);
            System.out.println(i);
        }
    }
}