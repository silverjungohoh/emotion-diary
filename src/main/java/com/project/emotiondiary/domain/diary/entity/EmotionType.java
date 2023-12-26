package com.project.emotiondiary.domain.diary.entity;

import java.util.Arrays;

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

	public static EmotionType getEmotionType(int score) {
		return Arrays.stream(values())
			.filter(type -> type.getScore() == score)
			.findAny()
			.orElse(null);
	}
}