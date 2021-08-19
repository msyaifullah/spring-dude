package com.yyggee.eggs;

import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;

import static org.mockito.Mockito.when;

/**
 * Mock out the entire RedisTemplate
 */
@Profile("gitlab")
@TestConfiguration
@Slf4j
public class RedisTemplateMocker {

    @Bean
    @Primary
    public RedisTemplate<?, ?> redisTemplate() {

        RedisTemplate<String, Object> redisTemplate = Mockito.mock(RedisTemplate.class);

        ValueOperations valueOperations = Mockito.mock(ValueOperations.class);
        SetOperations setOperations = Mockito.mock(SetOperations.class);
        HashOperations hashOperations = redisTemplate.opsForHash();
        ListOperations listOperations = redisTemplate.opsForList();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        RedisOperations redisOperations = Mockito.mock(RedisOperations.class);
        RedisConnection redisConnection = Mockito.mock(RedisConnection.class);
        RedisConnectionFactory redisConnectionFactory = Mockito.mock(RedisConnectionFactory.class);
        when(redisTemplate.getConnectionFactory()).thenReturn(redisConnectionFactory);
        when(valueOperations.getOperations()).thenReturn(redisOperations);
        when(redisTemplate.getConnectionFactory().getConnection()).thenReturn(redisConnection);

        return redisTemplate;
    }
}