package com.project.emotiondiary.domain.diary.model;

import java.time.LocalDate;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiaryResponse {

	private Long id;

	private String title;

	private String content;

	private EmotionType emotionType;

	private LocalDate date;

	public static UpdateDiaryResponse from (Diary diary) {
		return UpdateDiaryResponse.builder()
			.id(diary.getId())
			.title(diary.getTitle())
			.content(diary.getContent())
			.emotionType(diary.getEmotionType())
			.date(diary.getDate())
			.build();
	}
}
