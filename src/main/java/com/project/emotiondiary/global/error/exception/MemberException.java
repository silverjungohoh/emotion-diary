package com.project.emotiondiary.global.error.exception;

import com.project.emotiondiary.global.error.type.MemberErrorCode;

public class MemberException extends BusinessException {

	public MemberException(MemberErrorCode errorCode) {
		super(errorCode);
	}
}