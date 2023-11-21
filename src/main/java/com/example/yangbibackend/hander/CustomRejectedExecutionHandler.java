package com.example.yangbibackend.hander;

import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        // 自定义处理逻辑，例如记录日志或抛出异常
        System.out.println("任务被拒绝: " + r.toString());

        // 可以选择抛出异常或者其他处理方式
        throw new RejectedExecutionException("任务被拒绝: " + r.toString());
    }
}
