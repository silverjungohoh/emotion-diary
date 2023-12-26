package com.project.emotiondiary.domain.diary.service;

import static com.project.emotiondiary.global.error.type.DiaryErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.domain.diary.repository.DiaryRepository;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.error.exception.DiaryException;

@SpringBootTest
@ActiveProfiles("test")
class DiaryServiceTest {

	@Autowired
	private DiaryService diaryService;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private MemberRepository memberRepository;

	@AfterEach
	void tearDown() {
		diaryRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@DisplayName("일기를 성공적으로 작성한다.")
	@Test
	void writeDiary() {
		// given
		Member member = createMember();
		CreateDiaryRequest request = CreateDiaryRequest.builder()
			.title("제목")
			.content("내용")
			.date(LocalDate.of(2023, 12, 25))
			.score(4)
			.build();

		// when
		CreateDiaryResponse response = diaryService.writeDiary(member, request);

		// then
		assertThat(response)
			.extracting("id", "title", "content", "date", "emotionType")
			.contains(1L, "제목", "내용", LocalDate.of(2023, 12, 25), EmotionType.GOOD);
	}

	@DisplayName("해당 날짜에 이미 작성된 일기가 존재하면 예외를 던진다.")
	@Test
	void writeDiaryAlreadyExistDate() {
		// given
		Member member = createMember();

		Diary diary = Diary.builder()
			.title("제목")
			.content("내용")
			.date(LocalDate.of(2023, 12, 25))
			.build();
		diaryRepository.save(diary);

		CreateDiaryRequest request = CreateDiaryRequest.builder()
			.title("제목입니다.")
			.content("내용입니다.")
			.date(LocalDate.of(2023, 12, 25))
			.build();

		// when & then
		assertThatThrownBy(() -> diaryService.writeDiary(member, request))
			.isInstanceOf(DiaryException.class)
			.hasMessage(ONE_DIARY_PER_ONE_DAY.getMessage());
	}

	private Member createMember() {
		Member member = Member.builder()
			.email("test@test.com")
			.nickname("hello")
			.build();
		return memberRepository.save(member);
	}
}