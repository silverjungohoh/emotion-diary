package com.project.emotiondiary.domain.diary.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.domain.diary.model.DiaryDetailResponse;
import com.project.emotiondiary.domain.diary.model.DiaryListResponse;
import com.project.emotiondiary.domain.diary.model.DiaryRequestParam;
import com.project.emotiondiary.domain.diary.model.UpdateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.UpdateDiaryResponse;
import com.project.emotiondiary.domain.diary.service.DiaryService;
import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.global.auth.annotation.AuthRequired;
import com.project.emotiondiary.global.auth.annotation.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/diaries")
@RestController
@RequiredArgsConstructor
public class DiaryController {

	private final DiaryService diaryService;

	@AuthRequired
	@PostMapping
	public ResponseEntity<CreateDiaryResponse> writeDiary(@AuthUser Member member,
		@Valid @RequestBody CreateDiaryRequest request) {

		CreateDiaryResponse response = diaryService.writeDiary(member, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@AuthRequired
	@DeleteMapping("/{diaryId}")
	public ResponseEntity<Map<String, String>> deleteDiary(@AuthUser Member member,
		@PathVariable(name = "diaryId") Long diaryId) {

		Map<String, String> response = diaryService.deleteDiary(member, diaryId);
		return ResponseEntity.ok(response);
	}

	@AuthRequired
	@GetMapping("/{diaryId}")
	public ResponseEntity<DiaryDetailResponse> getDiaryDetail(@AuthUser Member member,
		@PathVariable(name = "diaryId") Long diaryId) {

		DiaryDetailResponse response = diaryService.getDiaryDetail(member, diaryId);
		return ResponseEntity.ok(response);
	}

	@AuthRequired
	@PatchMapping("/{diaryId}")
	public ResponseEntity<UpdateDiaryResponse> updateDiary(@AuthUser Member member,
		@PathVariable(name = "diaryId") Long diaryId, @Valid @RequestBody UpdateDiaryRequest request) {

		UpdateDiaryResponse response = diaryService.updateDiary(member, diaryId, request);
		return ResponseEntity.ok(response);
	}

	@AuthRequired
	@GetMapping
	public ResponseEntity<DiaryListResponse> getDiaryList(@AuthUser Member member,
		@ModelAttribute DiaryRequestParam param) {
		DiaryListResponse response = diaryService.getMyDiaryListByMonth(member, param);
		return ResponseEntity.ok(response);
	}
}
