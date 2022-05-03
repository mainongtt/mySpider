package com.my;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedis {

    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void testConncect(){
        redisTemplate.opsForList().leftPush("page","1");
    }
}
