package lib.yyggee.jupiter.repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class AbstractValueCacheRepository<T, ID> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  protected final RedisTemplate<String, Object> redisTemplate;

  protected AbstractValueCacheRepository(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  protected abstract String getKeyPrefix();

  protected String buildKey(ID id) {
    return getKeyPrefix() + id;
  }

  public T save(T entity, ID id) {
    try {
      BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps(buildKey(id));
      ops.set(entity);
      logger.debug("Saved entity with id {} to cache", id);
      return entity;
    } catch (Exception e) {
      logger.error("Error saving entity to cache: {}", e.getMessage());
      throw new CacheOperationException("Failed to save entity to cache", e);
    }
  }

  public T save(T entity, ID id, long ttlSeconds) {
    try {
      BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps(buildKey(id));
      ops.set(entity);
      ops.expire(ttlSeconds, TimeUnit.SECONDS);
      logger.debug("Saved entity with id {} to cache with TTL {} seconds", id, ttlSeconds);
      return entity;
    } catch (Exception e) {
      logger.error("Error saving entity to cache: {}", e.getMessage());
      throw new CacheOperationException("Failed to save entity to cache", e);
    }
  }

  public T save(T entity, ID id, long ttl, TimeUnit timeUnit) {
    try {
      BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps(buildKey(id));
      ops.set(entity);
      ops.expire(ttl, timeUnit);
      logger.debug("Saved entity with id {} to cache with TTL {} {}", id, ttl, timeUnit);
      return entity;
    } catch (Exception e) {
      logger.error("Error saving entity to cache: {}", e.getMessage());
      throw new CacheOperationException("Failed to save entity to cache", e);
    }
  }

  @SuppressWarnings("unchecked")
  public Optional<T> findById(ID id) {
    try {
      BoundValueOperations<String, Object> ops = redisTemplate.boundValueOps(buildKey(id));
      T entity = (T) ops.get();
      return Optional.ofNullable(entity);
    } catch (Exception e) {
      logger.error("Error finding entity by id {}: {}", id, e.getMessage());
      return Optional.empty();
    }
  }

  public boolean existsById(ID id) {
    try {
      return Boolean.TRUE.equals(redisTemplate.hasKey(buildKey(id)));
    } catch (Exception e) {
      logger.error("Error checking existence of id {}: {}", id, e.getMessage());
      return false;
    }
  }

  public void deleteById(ID id) {
    try {
      redisTemplate.delete(buildKey(id));
      logger.debug("Deleted entity with id {} from cache", id);
    } catch (Exception e) {
      logger.error("Error deleting entity by id {}: {}", id, e.getMessage());
      throw new CacheOperationException("Failed to delete entity from cache", e);
    }
  }

  public Long getExpire(ID id) {
    try {
      return redisTemplate.getExpire(buildKey(id));
    } catch (Exception e) {
      logger.error("Error getting expiration for id {}: {}", id, e.getMessage());
      return null;
    }
  }
}
