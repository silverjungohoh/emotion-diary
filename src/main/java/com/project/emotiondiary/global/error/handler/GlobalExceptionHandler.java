package com.project.emotiondiary.global.error.handler;

import static com.project.emotiondiary.global.error.type.CommonErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.emotiondiary.global.error.exception.BusinessException;
import com.project.emotiondiary.global.error.model.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {

		return ResponseEntity.status(e.getErrorCode().getStatus())
			.body(ErrorResponse.of(e.getErrorCode()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(ErrorResponse.of(INPUT_VALUE_INVALID, ErrorResponse.FieldError.of(e.getBindingResult())));
	}
}
