package com.project.emotiondiary.global.error.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;

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

	private List<FieldError> fieldErrors;

	public static ErrorResponse of(ErrorCode error) {
		return ErrorResponse.builder()
			.status(error.getStatus())
			.message(error.getMessage())
			.code(error.getCode())
			.fieldErrors(new ArrayList<>())
			.build();
	}

	public static ErrorResponse of(ErrorCode error, List<FieldError> fieldErrors) {
		return ErrorResponse.builder()
			.status(error.getStatus())
			.message(error.getMessage())
			.code(error.getCode())
			.fieldErrors(fieldErrors)
			.build();
	}

	@Getter
	@AllArgsConstructor
	public static class FieldError {

		private String field;

		private String reason;

		public static List<FieldError> of(BindingResult bindingResult) {
			return bindingResult.getFieldErrors()
				.stream()
				.map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
				.collect(Collectors.toList());
		}
	}
}
