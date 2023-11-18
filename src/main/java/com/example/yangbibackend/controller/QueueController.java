package com.example.yangbibackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","local"})
public class QueueController {

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    /**
     * 提交任务到线程池
     * @param name
     */
    @GetMapping("/add")
    public void add(String name){
        CompletableFuture.runAsync(()->{
            System.out.println("任务执行中: "+name+"  执行人: "+Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },threadPoolExecutor);

    }

    /**
     * 查看线程池状态
     */
    @GetMapping("/get")
    public void get(){
        HashMap<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        long taskCount = threadPoolExecutor.getTaskCount();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("任务长度",size);
        map.put("任务总数",taskCount);
        map.put("已完成任务数",completedTaskCount);
        map.put("正在工作的线程数",activeCount);


        System.out.println(map.toString());
    }


}
