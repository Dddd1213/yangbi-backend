package com.example.yangbibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class YangbiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YangbiBackendApplication.class, args);
    }

}
