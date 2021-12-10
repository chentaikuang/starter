package com.xiaochen.starter.test.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    public Boolean zsetAdd(String zsetKey, Object obj, double score) {
        return redisTemplate.boundZSetOps(zsetKey).add(obj, score);
    }

    public Long zsetSize(String zsetKey) {
        return redisTemplate.boundZSetOps(zsetKey).size();
    }

    public Set range(String zsetKey, long start, long end) {
        return redisTemplate.boundZSetOps(zsetKey).range(start, end);
    }

    public void clear(String zsetKey) {
        redisTemplate.delete(zsetKey);
    }

    public Set<ZSetOperations.TypedTuple> rangeWithScores(String zsetKey, long start, long end) {
        return redisTemplate.boundZSetOps(zsetKey).rangeWithScores(start, end);
    }

    public Set rangeByScore(String zsetKey, double min, double max) {
        return redisTemplate.boundZSetOps(zsetKey).rangeByScore(min, max);
    }
}
