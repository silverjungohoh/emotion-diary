package com.project.emotiondiary.global.auth.model;

import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@RedisHash("refresh")
@NoArgsConstructor
public class RefreshToken {

	@Id
	private String id;

	private String email;

	@TimeToLive(unit = TimeUnit.MILLISECONDS)
	private Long expiration;

	@Builder
	private RefreshToken(String id, String email, Long expiration) {
		this.id = id;
		this.email = email;
		this.expiration = expiration;
	}
}
