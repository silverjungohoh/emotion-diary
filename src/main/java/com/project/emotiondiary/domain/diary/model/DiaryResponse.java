package com.project.emotiondiary.domain.diary.model;

import java.time.LocalDate;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryResponse {

	private Long id;

	private String title;

	private LocalDate date;

	private EmotionType emotionType;

	public static DiaryResponse from (Diary diary) {
		return DiaryResponse.builder()
			.id(diary.getId())
			.title(diary.getTitle())
			.date(diary.getDate())
			.emotionType(diary.getEmotionType())
			.build();
	}
}
