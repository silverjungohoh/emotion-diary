package com.project.emotiondiary.global.error.type;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	ALREADY_EXIST_EMAIL(CONFLICT, "E101", "이미 존재하는 이메일입니다."),
	ALREADY_EXIST_NICKNAME(CONFLICT, "E102", "이미 존재하는 닉네임입니다."),
	MISMATCH_PASSWORD_CHECK(BAD_REQUEST, "E103", "두 비밀번호가 일치하지 않습니다."),
	MEMBER_NOT_FOUND(NOT_FOUND, "E104", "존재하지 않는 회원입니다."),
	FAIL_TO_LOGIN(UNAUTHORIZED, "E105", "회원 로그인에 실패하였습니다."),
	REISSUE_TOKEN_FAILED(UNAUTHORIZED, "E106", "토큰 재발급에 실패하였습니다."),
	WITHDRAW_FAILED(UNAUTHORIZED, "E107", "회원 탈퇴에 실패하였습니다.");

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
