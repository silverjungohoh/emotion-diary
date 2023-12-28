package com.project.emotiondiary.domain.diary.service;

import static com.project.emotiondiary.global.error.type.DiaryErrorCode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.domain.diary.model.DiaryDetailResponse;
import com.project.emotiondiary.domain.diary.model.UpdateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.UpdateDiaryResponse;
import com.project.emotiondiary.domain.diary.repository.DiaryRepository;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.global.error.exception.DiaryException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

	private final DiaryRepository diaryRepository;

	@Transactional
	public CreateDiaryResponse writeDiary(Member member, CreateDiaryRequest request) {

		if (diaryRepository.existsByDate(request.getDate())) {
			throw new DiaryException(ONE_DIARY_PER_ONE_DAY);
		}

		Diary diary = Diary.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.member(member)
			.date(request.getDate())
			.emotionType(EmotionType.getEmotionType(request.getScore()))
			.build();

		diaryRepository.save(diary);

		return CreateDiaryResponse.from(diary);
	}

	@Transactional
	public Map<String, String> deleteDiary(Member member, Long diaryId) {

		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));

		if (!Objects.equals(diary.getMember().getId(), member.getId())) {
			throw new DiaryException(NO_AUTHORITY_TO_DELETE);
		}

		diaryRepository.delete(diary);
		return getMessage("일기가 삭제되었습니다.");
	}

	@Transactional(readOnly = true)
	public DiaryDetailResponse getDiaryDetail(Member member, Long diaryId) {

		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));

		boolean isWriter = Objects.equals(diary.getMember().getId(), member.getId());

		return DiaryDetailResponse.builder()
			.id(diary.getId())
			.title(diary.getTitle())
			.content(diary.getContent())
			.date(diary.getDate())
			.emotionType(diary.getEmotionType())
			.writer(isWriter)
			.build();
	}

	@Transactional
	public UpdateDiaryResponse updateDiary(Member member, Long diaryId, UpdateDiaryRequest request) {

		Diary diary = diaryRepository.findById(diaryId)
			.orElseThrow(() -> new DiaryException(DIARY_NOT_FOUND));

		if (!Objects.equals(diary.getMember().getId(), member.getId())) {
			throw new DiaryException(NO_AUTHORITY_TO_UPDATE);
		}

		EmotionType newType = EmotionType.getEmotionType(request.getScore());
		diary.update(request.getTitle(), request.getContent(), newType, request.getDate());

		return UpdateDiaryResponse.from(diary);
	}

	private static Map<String, String> getMessage(String message) {
		Map<String, String> result = new HashMap<>();
		result.put("message", message);
		return result;
	}
}
