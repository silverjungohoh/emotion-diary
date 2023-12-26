package com.project.emotiondiary.global.error.exception;

import com.project.emotiondiary.global.error.type.ErrorCode;

public class DiaryException extends BusinessException {

	public DiaryException(ErrorCode errorCode) {
		super(errorCode);
	}
}
