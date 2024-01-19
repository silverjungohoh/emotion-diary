package com.project.emotiondiary.domain.member.model;

import java.time.LocalDate;

import com.project.emotiondiary.domain.member.entity.Gender;
import com.project.emotiondiary.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

	private Long id;

	private String email;

	private String nickname;

	private Gender gender;

	private LocalDate createdAt;

	public static ProfileResponse from (Member member) {
		return ProfileResponse.builder()
			.id(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.gender(member.getGender())
			.createdAt(member.getCreatedAt().toLocalDate())
			.build();
	}
}
