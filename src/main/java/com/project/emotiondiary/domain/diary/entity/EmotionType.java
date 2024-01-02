package com.project.emotiondiary.domain.diary.entity;

import java.util.Arrays;
import java.util.List;

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

	public static List<EmotionType> forGood() {
		return List.of(NORMAL, GOOD, BEST);
	}

	public static List<EmotionType> forBad() {
		return List.of(WORST, BAD);
	}

	public static List<EmotionType> forAll() {
		return List.of(EmotionType.values());
	}
}