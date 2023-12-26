package com.project.emotiondiary.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmotionType {

	WORST(1),
	BAD(2),
	NORMAL(3),
	GOOD(4),
	BEST(5);

	private final int score;
}