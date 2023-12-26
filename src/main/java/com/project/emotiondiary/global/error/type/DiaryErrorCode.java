package com.project.emotiondiary.global.error.type;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DiaryErrorCode implements ErrorCode {

	ONE_DIARY_PER_ONE_DAY(BAD_REQUEST, "E301", "하루에 하나만 작성 가능합니다.");


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
