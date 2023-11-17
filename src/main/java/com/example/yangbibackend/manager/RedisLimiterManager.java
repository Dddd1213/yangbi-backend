package com.example.yangbibackend.manager;

import com.example.yangbibackend.common.enumeration.ErrorCode;
import com.example.yangbibackend.common.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 提供一个通用的能力
 * TODO：放进通用模板中
 */
@Service
public class RedisLimiterManager {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * key 用来区分不同的限流，如不同的id分别统计限流
     * @param key
     */
    public void doRateLimit(String key){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //每秒允许访问两次
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);

        //令牌桶：每个操作取几个令牌
        //应用场景：比如会员可以设置更少的令牌，让它能更快处理；普通用户一个操作消耗更多令牌
        boolean acquire = rateLimiter.tryAcquire(1);

        if(!acquire){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

    }



}
