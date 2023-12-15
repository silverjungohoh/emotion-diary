package com.project.emotiondiary.domain.member.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.emotiondiary.domain.member.entity.Gender;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

	@NotBlank(message = "이메일은 필수 입력 항목입니다.")
	@Email(message = "이메일 형식이 아닙니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
	private String passwordCheck;

	@Size(min = 2, max = 10, message = "닉네임은 최소 2자, 최대 10자까지 가능합니다")
	private String nickname;

	@NotNull(message = "성별을 선택해주세요.")
	private Gender gender;

	@AssertTrue(message = "이메일 중복 확인이 필요합니다.")
	@JsonProperty("isCheckEmail")
	private boolean checkEmail;

	@AssertTrue(message = "닉네임 중복 확인이 필요합니다.")
	@JsonProperty("isCheckNick")
	private boolean checkNick;
}
