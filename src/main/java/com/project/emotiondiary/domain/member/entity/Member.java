package com.project.emotiondiary.domain.member.entity;

import com.project.emotiondiary.global.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(unique = true)
	private String email;

	private String password;

	@Column(unique = true)
	private String nickname;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Builder
	private Member(String email, String password, String nickname, Gender gender) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.gender = gender;
	}
}
