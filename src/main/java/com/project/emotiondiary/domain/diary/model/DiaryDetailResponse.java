package com.project.emotiondiary.domain.diary.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.emotiondiary.domain.diary.entity.EmotionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDetailResponse {

	private Long id;

	private String title;

	private String content;

	private EmotionType emotionType;

	private LocalDate date;

	@JsonProperty("isWriter")
	private boolean writer;
}
