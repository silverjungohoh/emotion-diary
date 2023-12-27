package com.project.emotiondiary.domain.diary.service;

import static com.project.emotiondiary.global.error.type.DiaryErrorCode.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.domain.diary.repository.DiaryRepository;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.error.exception.DiaryException;
import com.project.emotiondiary.helper.IntegrationTestSupport;

class DiaryServiceTest extends IntegrationTestSupport {

	@Autowired
	private DiaryService diaryService;

	@Autowired
	private DiaryRepository diaryRepository;

	@Autowired
	private MemberRepository memberRepository;

	@DisplayName("일기를 성공적으로 작성한다.")
	@Test
	void writeDiary() {
		// given
		Member member = createMember("test@test.com", "hello");
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
		Member member = createMember("test@test.com", "hello");

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

	@DisplayName("일기 삭제 권한이 없는 경우 예외를 던진다.")
	@Test
	void deleteDiaryWithNoAuthority() {
		// given
		Member member1 = createMember("test@test.com", "hello");
		Member member2 = createMember("java@java.com", "hi");

		Diary diary = Diary.builder()
			.title("제목")
			.content("내용")
			.member(member1)
			.build();

		diaryRepository.save(diary);

		// when & then
		assertThatThrownBy(() -> diaryService.deleteDiary(member2, 1L))
			.isInstanceOf(DiaryException.class)
			.hasMessage(NO_AUTHORITY_TO_DELETE.getMessage());
	}

	@DisplayName("삭제하려는 일기가 존재하지 않는 경우 예외를 던진다.")
	@Test
	void deleteDiaryWithNoDiary() {
		// given
		Member member = createMember("test@test.com", "hello");

		// when & then
		assertThatThrownBy(() -> diaryService.deleteDiary(member, 100L))
			.isInstanceOf(DiaryException.class)
			.hasMessage(DIARY_NOT_FOUND.getMessage());
	}

	@DisplayName("일기를 성공적으로 삭제한다.")
	@Test
	void deleteDiary() {
		// given
		Member member = createMember("test@test.com", "hello");

		Diary diary = Diary.builder()
			.title("제목")
			.content("내용")
			.member(member)
			.build();

		diaryRepository.save(diary);

		// when
		Map<String, String> response = diaryService.deleteDiary(member, 1L);

		// then
		assertThat(response.get("message")).isEqualTo("일기가 삭제되었습니다.");
	}

	private Member createMember(String email, String nickname) {
		Member member = Member.builder()
			.email(email)
			.nickname(nickname)
			.build();

		return memberRepository.save(member);
	}
}