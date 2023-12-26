package com.project.emotiondiary.domain.diary.service;

import static com.project.emotiondiary.global.error.type.DiaryErrorCode.*;

import org.springframework.stereotype.Service;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.domain.diary.repository.DiaryRepository;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.global.error.exception.DiaryException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

	private final DiaryRepository diaryRepository;

	public CreateDiaryResponse writeDiary (Member member, CreateDiaryRequest request) {

		if(diaryRepository.existsByDate(request.getDate())) {
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
}
