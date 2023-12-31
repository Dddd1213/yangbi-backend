package com.example.yangbibackend.config;

import com.example.yangbibackend.hander.CustomRejectedExecutionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author 31067
 */
@Configuration
public class ThreadPoolExecutorConfig {


    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){

        /**
         * 自定义线程池工厂
         */
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 1;
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("Thread"+count++);
                return thread;
            }
        };

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,2,100,
                TimeUnit.SECONDS,new ArrayBlockingQueue<>(10),threadFactory, new CustomRejectedExecutionHandler());
        return threadPoolExecutor;
    }

}
