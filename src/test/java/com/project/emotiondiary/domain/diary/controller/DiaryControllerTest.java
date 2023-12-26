package com.project.emotiondiary.domain.diary.controller;

import static com.project.emotiondiary.global.error.type.CommonErrorCode.*;
import static com.project.emotiondiary.global.error.type.DiaryErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.global.error.exception.DiaryException;
import com.project.emotiondiary.helper.ControllerTestSupport;

class DiaryControllerTest extends ControllerTestSupport {

	@DisplayName("해당 날짜에 이미 작성된 일기가 존재하면 예외를 던진다.")
	@Test
	void writeDiaryAlreadyExistDate() throws Exception {
		// given
		CreateDiaryRequest request = CreateDiaryRequest.builder()
			.title("제목")
			.content("내용")
			.date(LocalDate.now())
			.score(4)
			.build();

		given(diaryService.writeDiary(any(), any())).willThrow(new DiaryException(ONE_DIARY_PER_ONE_DAY));

		// when & then
		mockMvc.perform(post("/api/diaries")
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(ONE_DIARY_PER_ONE_DAY.getStatus()))
			.andExpect(jsonPath("$.message").value(ONE_DIARY_PER_ONE_DAY.getMessage()))
			.andExpect(jsonPath("$.code").value(ONE_DIARY_PER_ONE_DAY.getCode()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("title").description("일기 제목"),
						fieldWithPath("content").description("일기 내용"),
						fieldWithPath("date").description("날짜"),
						fieldWithPath("score").description("감정 점수")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보")
					)
				)
			);
	}

	@DisplayName("일기 작성 시 유효성 검증에 실패한 경우 예외를 던진다.")
	@Test
	void writeDiaryWithInvalid() throws Exception {
		// given
		CreateDiaryRequest request = CreateDiaryRequest.builder()
			.title("")
			.content("")
			.date(LocalDate.now().plusDays(1))
			.score(4)
			.build();

		given(diaryService.writeDiary(any(), any())).willThrow(new DiaryException(ONE_DIARY_PER_ONE_DAY));

		// when & then
		mockMvc.perform(post("/api/diaries")
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(INPUT_VALUE_INVALID.getCode()))
			.andExpect(jsonPath("$.status").value(INPUT_VALUE_INVALID.getStatus()))
			.andExpect(jsonPath("$.message").value(INPUT_VALUE_INVALID.getMessage()))
			.andExpect(jsonPath("$.fieldErrors.size()").value(3))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("title").description("일기 제목"),
						fieldWithPath("content").description("일기 내용"),
						fieldWithPath("date").description("날짜"),
						fieldWithPath("score").description("감정 점수")
					),
					responseFields(
						fieldWithPath("status").description("HTTP 상태 코드"),
						fieldWithPath("message").description("에러 메세지"),
						fieldWithPath("code").description("에러 코드"),
						fieldWithPath("fieldErrors").description("유효성 검증 실패에 대한 정보"),
						fieldWithPath("fieldErrors[].field").description("유효성 검증 실패한 필드 이름"),
						fieldWithPath("fieldErrors[].reason").description("유효성 검증 실패한 필드의 메세지")
					)
				)
			);
	}

	@DisplayName("일기를 성공적으로 작성한다.")
	@Test
	void writeDiary() throws Exception {
		// given
		CreateDiaryRequest request = CreateDiaryRequest.builder()
			.title("제목")
			.content("내용")
			.date(LocalDate.now())
			.score(4)
			.build();

		CreateDiaryResponse response = CreateDiaryResponse.builder()
			.id(1L)
			.title("제목")
			.content("내용")
			.date(LocalDate.now())
			.emotionType(EmotionType.GOOD)
			.build();

		given(diaryService.writeDiary(any(), any())).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/diaries")
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.title").value("제목"))
			.andExpect(jsonPath("$.content").value("내용"))
			.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
			.andExpect(jsonPath("$.emotionType").value(EmotionType.GOOD.name()))
			.andDo(
				restDocs.document(
					requestFields(
						fieldWithPath("title").description("일기 제목"),
						fieldWithPath("content").description("일기 내용"),
						fieldWithPath("date").description("날짜"),
						fieldWithPath("score").description("감정 점수")
					),
					responseFields(
						fieldWithPath("id").description("일기 고유 번호"),
						fieldWithPath("title").description("일기 제목"),
						fieldWithPath("content").description("일기 내용"),
						fieldWithPath("date").description("날짜"),
						fieldWithPath("emotionType").description("감정 점수에 기반한 감정 타입 (WORST/BAD/NORMAL/GOOD/BEST)")
					)
				)
			);
	}
}