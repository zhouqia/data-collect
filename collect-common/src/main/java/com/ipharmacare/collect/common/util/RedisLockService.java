package com.ipharmacare.collect.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {
    private static final String baseKey = "iphms:lock:";

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisLockService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void tryLock(final String bizKey, final String lock) {
        String key = baseKey + bizKey;
        while (true) {
            boolean locked = Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, lock, 1L, TimeUnit.MINUTES));
            if (locked) {
                return;
            }
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                //
            }
        }
    }

    public void releaseLock(final String bizKey, final String lock) {
        String key = baseKey + bizKey;
        String s = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.equals(lock, s)) {
            stringRedisTemplate.delete(key);
        }
    }
}