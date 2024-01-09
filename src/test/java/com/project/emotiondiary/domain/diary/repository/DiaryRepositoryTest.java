package com.project.emotiondiary.domain.diary.repository;

import static com.project.emotiondiary.domain.diary.entity.EmotionType.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.helper.IntegrationTestSupport;

class DiaryRepositoryTest extends IntegrationTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private DiaryRepository diaryRepository;

	@DisplayName("회원이 작성한 특정 연도와 월에 해당하는 일기 목록 중 좋은 감정만 내림차순으로 조회한다.")
	@Test
	void findDiaryListByMonth() {
		// given
		Member member = createMember();

		LocalDate date = LocalDate.of(2023, 12, 1);

		for (int i = 0; i < 20; i++) {
			EmotionType type = i % 2 == 0 ? BAD : GOOD;
			Diary diary = createDiary(date.plusDays(i), type, member);
			diaryRepository.save(diary);
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "date");
		Pageable pageable = PageRequest.of(0, 10, sort);

		// when
		Slice<Diary> diarySlice = diaryRepository.findDiaryList(2023, 12, 1L, EmotionType.forGood(), null, pageable);

		// then
		List<Diary> diaryList = diarySlice.getContent();
		assertThat(diaryList).hasSize(10);
		assertThat(diaryList.get(0))
			.extracting("date", "emotionType", "id")
			.contains(LocalDate.of(2023, 12, 20), GOOD, 20L);
		assertThat(diaryList.get(diaryList.size() - 1))
			.extracting("date", "emotionType", "id")
			.contains(LocalDate.of(2023, 12, 2), GOOD, 2L);
		assertThat(diarySlice.hasNext()).isFalse();
	}

	@DisplayName("특정 회원이 작성한 일기들을 일괄적으로 삭제한다.")
	@Test
	@Transactional
	void deleteAllByMemberId() {
		// given
		Member member = createMember();
		memberRepository.save(member);

		LocalDate date = LocalDate.of(2024, 1, 1);

		for(int i = 0; i < 10; i++) {
			EmotionType type = i % 2 == 0 ? BAD : GOOD;
			Diary diary = createDiary(date.plusDays(i), type, member);
			diaryRepository.save(diary);
		}

		// when
		diaryRepository.deleteAllByMemberId(1L);

		// then
		List<Diary> diaryList = diaryRepository.findAll();
		assertThat(diaryList).hasSize(0);
	}

	private static Diary createDiary(LocalDate date, EmotionType type, Member member) {
		return Diary.builder()
			.title("제목")
			.content("내용")
			.emotionType(type)
			.date(date)
			.member(member)
			.build();
	}

	private Member createMember() {
		Member member = Member.builder()
			.email("test@test.com")
			.nickname("hello")
			.build();
		return memberRepository.save(member);
	}
}