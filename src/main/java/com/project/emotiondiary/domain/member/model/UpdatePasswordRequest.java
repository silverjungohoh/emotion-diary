package com.project.emotiondiary.domain.member.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;

	@NotBlank(message = "새로운 비밀번호를 입력해주세요.")
	private String newPassword;

	private String newPasswordCheck;
}
