package com.project.emotiondiary.global.error.exception;

import com.project.emotiondiary.global.error.type.AuthErrorCode;

public class AuthException extends BusinessException {

	public AuthException(AuthErrorCode errorCode) {
		super(errorCode);
	}
}
