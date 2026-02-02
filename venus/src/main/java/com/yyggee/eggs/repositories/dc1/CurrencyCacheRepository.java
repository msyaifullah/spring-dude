package com.yyggee.eggs.repositories.dc1;

import com.yyggee.eggs.model.ds2.Currency;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CurrencyCacheRepository implements CacheRepository<Currency> {
  private final String KEY = "CURRENCY_";
  private final RedisTemplate<String, Currency> redisTemplate;

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public CurrencyCacheRepository(RedisTemplate<String, Currency> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Currency save(Currency model) {
    BoundValueOperations<String, Currency> boundValueOperations =
        this.redisTemplate.boundValueOps(KEY + model.getCode());
    boundValueOperations.set(model);
    boundValueOperations.expire(60, TimeUnit.MINUTES);
    return model;
  }

  @Override
  public List<Currency> findAll() {
    return null;
  }

  @Override
  public Currency findByCode(String code) {
    BoundValueOperations<String, Currency> boundValueOperations =
        this.redisTemplate.boundValueOps(KEY + code);
    return boundValueOperations.get();
  }

  @Override
  public Currency update(Currency model) {
    return save(model);
  }

  @Override
  public boolean delete(String code) {
    this.redisTemplate.delete(KEY + code);
    return true;
  }
}
