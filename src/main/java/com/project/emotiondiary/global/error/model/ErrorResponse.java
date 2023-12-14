package com.project.emotiondiary.global.error.model;

import com.project.emotiondiary.global.error.type.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

	private int status;

	private String message;

	private String code;

	public static ErrorResponse of (ErrorCode error) {
		return ErrorResponse.builder()
			.status(error.getStatus())
			.message(error.getMessage())
			.code(error.getCode())
			.build();
	}
}
