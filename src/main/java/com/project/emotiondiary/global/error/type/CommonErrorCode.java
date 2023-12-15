package com.project.emotiondiary.global.error.type;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

	INPUT_VALUE_INVALID(BAD_REQUEST, "E001", "입력값이 올바르지 않습니다.");

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
