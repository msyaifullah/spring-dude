package lib.yyggee.jupiter.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

  private final RedisProperties redisProperties;

  public RedisConfig(RedisProperties redisProperties) {
    this.redisProperties = redisProperties;
  }

  @Bean
  @ConditionalOnMissingBean
  public JedisPoolConfig jedisPoolConfig() {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
    poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
    poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
    poolConfig.setMaxWait(Duration.ofMillis(redisProperties.getPool().getMaxWait()));
    poolConfig.setTestOnBorrow(true);
    poolConfig.setTestOnReturn(true);
    poolConfig.setTestWhileIdle(true);
    return poolConfig;
  }

  @Bean
  @ConditionalOnMissingBean
  public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
    RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
    redisConfig.setHostName(redisProperties.getHost());
    redisConfig.setPort(redisProperties.getPort());
    redisConfig.setDatabase(redisProperties.getDatabase());
    if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
      redisConfig.setPassword(redisProperties.getPassword());
    }

    JedisClientConfiguration clientConfig =
        JedisClientConfiguration.builder()
            .usePooling()
            .poolConfig(jedisPoolConfig)
            .and()
            .readTimeout(Duration.ofMillis(redisProperties.getTimeout()))
            .connectTimeout(Duration.ofMillis(redisProperties.getTimeout()))
            .build();

    return new JedisConnectionFactory(redisConfig, clientConfig);
  }

  @Bean
  @ConditionalOnMissingBean
  public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
}
