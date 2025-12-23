package com.yyggee.eggs.repositories.dc1;



import com.yyggee.eggs.model.ds2.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CurrenciesCacheRepository implements CacheRepository<Currency> {
    private final String KEY = "CURRENCIES_";
    private final RedisTemplate<String, Currency> redisTemplate;
    private HashOperations<String, String, Currency> hashOperations;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CurrenciesCacheRepository(RedisTemplate<String, Currency> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Currency save(Currency model) throws Exception {
        try {
            hashOperations.put(KEY, model.getCode(), model);
        } catch (Exception e) {
            logger.error("error save cache {}", e.getMessage());
            throw new Exception("error when saving data on cache");
        }
        return model;
    }

    @Override
    public List<Currency> findAll() {
        Map<String, Currency> data = hashOperations.entries(KEY);
        return new ArrayList<>(data.values());
    }

    @Override
    public Currency findByCode(String code) {
        return hashOperations.get(KEY, code);
    }

    @Override
    public Currency update(Currency model) {
        if (Objects.nonNull(model)) {
            hashOperations.put(KEY, model.getCode(), model);
            return model;
        }
        return null;
    }

    @Override
    public boolean delete(String code) {
        hashOperations.delete(KEY, code);
        return true;
    }
}
