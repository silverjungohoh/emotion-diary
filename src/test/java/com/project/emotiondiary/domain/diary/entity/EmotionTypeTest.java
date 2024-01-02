package com.project.emotiondiary.domain.diary.entity;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmotionTypeTest {


	@DisplayName("알맞은 점수를 넣으면 감정 타입을 반환한다.")
	@Test
	void getEmotionType1() {
		// given
		int score = 3;
		// when
		EmotionType type = EmotionType.getEmotionType(score);
		// then
		assertThat(type).isEqualTo(EmotionType.NORMAL);
	}

	@DisplayName("범위를 벗어난 점수를 넣으면 null 반환한다.")
	@Test
	void getEmotionType2() {
		// given
		int score = 0;
		// when
		EmotionType type = EmotionType.getEmotionType(score);
		// then
		assertThat(type).isNull();
	}

	@DisplayName("나쁜 감정 타입들만 조회한다.")
	@Test
	void getBadTypes() {
		// when
		List<EmotionType> list = EmotionType.forBad();
		// then
		assertThat(list).hasSize(2)
			.containsExactlyInAnyOrder(EmotionType.WORST, EmotionType.BAD);
	}

	@DisplayName("좋은 감정 타입들만 조회한다.")
	@Test
	void getGoodTypes() {
		// when
		List<EmotionType> list = EmotionType.forGood();
		// then
		assertThat(list).hasSize(3)
			.containsExactlyInAnyOrder(EmotionType.NORMAL, EmotionType.GOOD, EmotionType.BEST);
	}
}