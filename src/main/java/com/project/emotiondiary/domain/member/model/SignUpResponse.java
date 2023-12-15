package com.project.emotiondiary.domain.member.model;

import com.project.emotiondiary.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {

	private String nickname;

	public static SignUpResponse from (Member member) {
		return new SignUpResponse(member.getNickname());
	}
}
