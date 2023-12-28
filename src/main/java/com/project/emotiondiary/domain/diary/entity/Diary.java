package com.project.emotiondiary.domain.diary.entity;

import java.time.LocalDate;

import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "diary_id")
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	private EmotionType emotionType;

	private LocalDate date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false)
	private Member member;

	@Builder
	private Diary(String title, String content, EmotionType emotionType, LocalDate date, Member member) {
		this.title = title;
		this.content = content;
		this.emotionType = emotionType;
		this.date = date;
		this.member = member;
	}

	public void update(String title, String content, EmotionType emotionType, LocalDate date) {
		this.title = title;
		this.content = content;
		this.emotionType = emotionType;
		this.date = date;
	}
}
