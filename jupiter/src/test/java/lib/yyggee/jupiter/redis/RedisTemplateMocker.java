package lib.yyggee.jupiter.redis;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

public class RedisTemplateMocker {

  private RedisTemplateMocker() {}

  @SuppressWarnings("unchecked")
  public static RedisTemplate<String, Object> createMockRedisTemplate() {
    RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);

    ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
    SetOperations<String, Object> setOperations = mock(SetOperations.class);
    HashOperations<String, Object, Object> hashOperations = mock(HashOperations.class);
    ListOperations<String, Object> listOperations = mock(ListOperations.class);
    ZSetOperations<String, Object> zSetOperations = mock(ZSetOperations.class);
    BoundValueOperations<String, Object> boundValueOperations = mock(BoundValueOperations.class);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(redisTemplate.opsForSet()).thenReturn(setOperations);
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    when(redisTemplate.opsForList()).thenReturn(listOperations);
    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    when(redisTemplate.boundValueOps(anyString())).thenReturn(boundValueOperations);

    RedisOperations<String, Object> redisOperations = mock(RedisOperations.class);
    RedisConnection redisConnection = mock(RedisConnection.class);
    RedisConnectionFactory redisConnectionFactory = mock(RedisConnectionFactory.class);

    when(redisTemplate.getConnectionFactory()).thenReturn(redisConnectionFactory);
    when(valueOperations.getOperations()).thenReturn(redisOperations);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);

    return redisTemplate;
  }

  @SuppressWarnings("unchecked")
  public static <V> RedisTemplate<String, V> createTypedMockRedisTemplate(Class<V> valueType) {
    RedisTemplate<String, V> redisTemplate = mock(RedisTemplate.class);

    ValueOperations<String, V> valueOperations = mock(ValueOperations.class);
    SetOperations<String, V> setOperations = mock(SetOperations.class);
    HashOperations<String, Object, Object> hashOperations = mock(HashOperations.class);
    ListOperations<String, V> listOperations = mock(ListOperations.class);
    ZSetOperations<String, V> zSetOperations = mock(ZSetOperations.class);
    BoundValueOperations<String, V> boundValueOperations = mock(BoundValueOperations.class);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    when(redisTemplate.opsForSet()).thenReturn(setOperations);
    when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    when(redisTemplate.opsForList()).thenReturn(listOperations);
    when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    when(redisTemplate.boundValueOps(anyString())).thenReturn(boundValueOperations);

    RedisOperations<String, V> redisOperations = mock(RedisOperations.class);
    RedisConnection redisConnection = mock(RedisConnection.class);
    RedisConnectionFactory redisConnectionFactory = mock(RedisConnectionFactory.class);

    when(redisTemplate.getConnectionFactory()).thenReturn(redisConnectionFactory);
    when(valueOperations.getOperations()).thenReturn(redisOperations);
    when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);

    return redisTemplate;
  }
}
