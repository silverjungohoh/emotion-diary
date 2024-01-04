package com.project.emotiondiary.global.auth.model;

import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@RedisHash("invalidated")
@NoArgsConstructor
public class InvalidatedAccessToken {

	@Id
	private String token;

	private String email;

	@TimeToLive(unit = TimeUnit.MILLISECONDS)
	private Long expiration;

	@Builder
	private InvalidatedAccessToken(String token, String email, Long expiration) {
		this.token = token;
		this.email = email;
		this.expiration = expiration;
	}
}
