package com.project.emotiondiary.global.error.type;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	EXPIRED_TOKEN(UNAUTHORIZED, "E201", "만료된 토큰입니다."),
	INVALID_TOKEN(UNAUTHORIZED, "E202", "유효하지 않은 토큰입니다."),
	WRONG_TOKEN(UNAUTHORIZED, "E203", "잘못된 토큰입니다."),
	AUTHENTICATION_FAILED(UNAUTHORIZED, "E204", "사용자 인증에 실패하였습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return status.value();
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getCode() {
		return code;
	}
}
