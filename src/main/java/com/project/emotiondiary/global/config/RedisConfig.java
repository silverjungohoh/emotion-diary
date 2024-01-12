package com.project.emotiondiary.global.config;

import static org.springframework.data.redis.cache.RedisCacheConfiguration.*;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
@EnableRedisRepositories(basePackages = "com.project.emotiondiary.global.auth.repository")
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(host, port);
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.activateDefaultTyping(
			objectMapper.getPolymorphicTypeValidator(),
			ObjectMapper.DefaultTyping.NON_FINAL
		);

		GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisCacheConfiguration redisCacheConfiguration = defaultCacheConfig()
			.disableCachingNullValues()  // null 값은 캐싱 X
			.entryTtl(Duration.ofHours(3L))  // 캐시 유효 시간 설정 = 3시간
			.computePrefixWith(CacheKeyPrefix.simple())  // 캐시 키의 접두어를 간단하게 계산
			.serializeKeysWith( // 키의 직렬화 방식 설정
				RedisSerializationContext.SerializationPair
					.fromSerializer(new StringRedisSerializer())
			)
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair
					.fromSerializer(redisSerializer)
			);

		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)  // Redis 연결 설정
			.cacheDefaults(redisCacheConfiguration)  // 기본 캐시 설정
			.build();
	}
}
