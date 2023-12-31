package com.project.emotiondiary.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import redis.embedded.RedisServer;

@EnableRedisRepositories
@TestConfiguration
public class EmbeddedRedisConfig {

	@Value("${spring.data.redis.port}")
	private int redisPort;

	private RedisServer redisServer;


	@PostConstruct
	public void startRedis() {
		redisServer = new RedisServer(redisPort);
		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}
}
