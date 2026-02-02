package lib.yyggee.jupiter.repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class AbstractCacheRepository<T, ID> implements CacheRepository<T, ID> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected final RedisTemplate<String, Object> redisTemplate;
  protected HashOperations<String, String, T> hashOperations;

  protected AbstractCacheRepository(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @PostConstruct
  private void init() {
    this.hashOperations = redisTemplate.opsForHash();
  }

  protected abstract String getCacheKey();

  protected abstract String getEntityId(T entity);

  @Override
  public T save(T entity) {
    try {
      hashOperations.put(getCacheKey(), getEntityId(entity), entity);
      logger.debug("Saved entity with id {} to cache {}", getEntityId(entity), getCacheKey());
      return entity;
    } catch (Exception e) {
      logger.error("Error saving entity to cache: {}", e.getMessage());
      throw new CacheOperationException("Failed to save entity to cache", e);
    }
  }

  @Override
  public T save(T entity, long ttlSeconds) {
    try {
      hashOperations.put(getCacheKey(), getEntityId(entity), entity);
      redisTemplate.expire(getCacheKey(), ttlSeconds, TimeUnit.SECONDS);
      logger.debug(
          "Saved entity with id {} to cache {} with TTL {} seconds",
          getEntityId(entity),
          getCacheKey(),
          ttlSeconds);
      return entity;
    } catch (Exception e) {
      logger.error("Error saving entity to cache: {}", e.getMessage());
      throw new CacheOperationException("Failed to save entity to cache", e);
    }
  }

  @Override
  public Optional<T> findById(ID id) {
    try {
      T entity = hashOperations.get(getCacheKey(), String.valueOf(id));
      return Optional.ofNullable(entity);
    } catch (Exception e) {
      logger.error("Error finding entity by id {}: {}", id, e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public List<T> findAll() {
    try {
      Map<String, T> entries = hashOperations.entries(getCacheKey());
      return new ArrayList<>(entries.values());
    } catch (Exception e) {
      logger.error("Error finding all entities: {}", e.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public boolean existsById(ID id) {
    try {
      return hashOperations.hasKey(getCacheKey(), String.valueOf(id));
    } catch (Exception e) {
      logger.error("Error checking existence of id {}: {}", id, e.getMessage());
      return false;
    }
  }

  @Override
  public void deleteById(ID id) {
    try {
      hashOperations.delete(getCacheKey(), String.valueOf(id));
      logger.debug("Deleted entity with id {} from cache {}", id, getCacheKey());
    } catch (Exception e) {
      logger.error("Error deleting entity by id {}: {}", id, e.getMessage());
      throw new CacheOperationException("Failed to delete entity from cache", e);
    }
  }

  @Override
  public void deleteAll() {
    try {
      Set<String> keys = hashOperations.keys(getCacheKey());
      if (!keys.isEmpty()) {
        hashOperations.delete(getCacheKey(), keys.toArray());
      }
      logger.debug("Deleted all entities from cache {}", getCacheKey());
    } catch (Exception e) {
      logger.error("Error deleting all entities: {}", e.getMessage());
      throw new CacheOperationException("Failed to delete all entities from cache", e);
    }
  }

  @Override
  public long count() {
    try {
      return hashOperations.size(getCacheKey());
    } catch (Exception e) {
      logger.error("Error counting entities: {}", e.getMessage());
      return 0L;
    }
  }
}
