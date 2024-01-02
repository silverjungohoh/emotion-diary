package com.project.emotiondiary.domain.diary.controller;

import static com.project.emotiondiary.domain.diary.entity.EmotionType.*;
import static com.project.emotiondiary.global.error.type.CommonErrorCode.*;
import static com.project.emotiondiary.global.error.type.DiaryErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.project.emotiondiary.domain.diary.entity.EmotionType;
import com.project.emotiondiary.domain.diary.model.CreateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.CreateDiaryResponse;
import com.project.emotiondiary.domain.diary.model.DiaryDetailResponse;
import com.project.emotiondiary.domain.diary.model.DiaryListResponse;
import com.project.emotiondiary.domain.diary.model.DiaryResponse;
import com.project.emotiondiary.domain.diary.model.UpdateDiaryRequest;
import com.project.emotiondiary.domain.diary.model.UpdateDiaryResponse;
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

	@DisplayName("삭제하려는 일기가 존재하지 않는 경우 예외를 던진다.")
	@Test
	void deleteDiaryWithNoDiary() throws Exception {
		// given
		given(diaryService.deleteDiary(any(), anyLong())).willThrow(new DiaryException(DIARY_NOT_FOUND));

		// when & then
		mockMvc.perform(delete("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(DIARY_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.status").value(DIARY_NOT_FOUND.getStatus()))
			.andExpect(jsonPath("$.message").value(DIARY_NOT_FOUND.getMessage()))
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("일기 고유 번호")
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

	@DisplayName("일기 삭제 권한이 없는 경우 예외를 던진다.")
	@Test
	void deleteDiaryWithNoAuthority() throws Exception {
		// given
		given(diaryService.deleteDiary(any(), anyLong())).willThrow(new DiaryException(NO_AUTHORITY_TO_DELETE));

		// when & then
		mockMvc.perform(delete("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(NO_AUTHORITY_TO_DELETE.getCode()))
			.andExpect(jsonPath("$.status").value(NO_AUTHORITY_TO_DELETE.getStatus()))
			.andExpect(jsonPath("$.message").value(NO_AUTHORITY_TO_DELETE.getMessage()))
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("삭제하려는 일기 고유 번호")
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

	@DisplayName("일기를 성공적으로 삭제한다.")
	@Test
	void deleteDiary() throws Exception {
		// given
		Map<String, String> response = new HashMap<>();
		response.put("message", "일기가 삭제되었습니다.");

		given(diaryService.deleteDiary(any(), anyLong())).willReturn(response);

		// when & then
		mockMvc.perform(delete("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("삭제하려는 일기 고유 번호")
					),
					responseFields(
						fieldWithPath("message").description("응답 메세지")
					)
				)
			);
	}

	@DisplayName("상세 조회하려는 일기가 존재하지 않는 경우 예외를 던진다.")
	@Test
	void getDiaryDetailWithNoDiary() throws Exception {
		// given
		given(diaryService.getDiaryDetail(any(), anyLong())).willThrow(new DiaryException(DIARY_NOT_FOUND));

		// when & then
		mockMvc.perform(get("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("상세 조회하려는 일기 고유 번호")
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

	@DisplayName("하나의 일기를 상세 조회한다.")
	@Test
	void getDiaryDetail() throws Exception {
		// given
		DiaryDetailResponse response = DiaryDetailResponse.builder()
			.id(1L)
			.title("제목")
			.content("내용")
			.emotionType(EmotionType.GOOD)
			.date(LocalDate.now())
			.writer(true)
			.build();

		given(diaryService.getDiaryDetail(any(), anyLong())).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("상세 조회하려는 일기 고유 번호")
					),
					responseFields(
						fieldWithPath("id").description("일기 고유 번호"),
						fieldWithPath("title").description("일기 제목"),
						fieldWithPath("content").description("일기 내용"),
						fieldWithPath("date").description("날짜"),
						fieldWithPath("emotionType").description("감정 점수에 기반한 감정 타입 (WORST/BAD/NORMAL/GOOD/BEST)"),
						fieldWithPath("isWriter").description("일기 작성자 여부 (true/false)")
					)
				)
			);
	}

	@DisplayName("수정하려는 일기가 존재하지 않는 경우 예외를 던진다.")
	@Test
	void updateDiaryWithNoDiary() throws Exception {
		// given
		UpdateDiaryRequest request = UpdateDiaryRequest.builder()
			.title("수정한 제목")
			.content("수정한 내용")
			.date(LocalDate.now().minusDays(1))
			.score(3)
			.build();

		given(diaryService.updateDiary(any(), anyLong(), any())).willThrow(new DiaryException(DIARY_NOT_FOUND));

		// when & then
		mockMvc.perform(patch("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value(DIARY_NOT_FOUND.getCode()))
			.andExpect(jsonPath("$.status").value(DIARY_NOT_FOUND.getStatus()))
			.andExpect(jsonPath("$.message").value(DIARY_NOT_FOUND.getMessage()))
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("수정하려는 일기 고유 번호")
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

	@DisplayName("일기 수정 권한이 없는 경우 예외를 던진다.")
	@Test
	void updateDiaryWithNoAuthority() throws Exception {
		// given
		UpdateDiaryRequest request = UpdateDiaryRequest.builder()
			.title("수정한 제목")
			.content("수정한 내용")
			.date(LocalDate.now().minusDays(1))
			.score(3)
			.build();

		given(diaryService.updateDiary(any(), anyLong(), any())).willThrow(new DiaryException(NO_AUTHORITY_TO_UPDATE));

		// when & then
		mockMvc.perform(patch("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.code").value(NO_AUTHORITY_TO_UPDATE.getCode()))
			.andExpect(jsonPath("$.status").value(NO_AUTHORITY_TO_UPDATE.getStatus()))
			.andExpect(jsonPath("$.message").value(NO_AUTHORITY_TO_UPDATE.getMessage()))
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("수정하려는 일기 고유 번호")
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

	@DisplayName("일기를 성공적으로 수정한다.")
	@Test
	void updateDiary() throws Exception {
		// given
		UpdateDiaryRequest request = UpdateDiaryRequest.builder()
			.title("수정한 제목")
			.content("수정한 내용")
			.date(LocalDate.now().minusDays(1))
			.score(3)
			.build();

		UpdateDiaryResponse response = UpdateDiaryResponse.builder()
			.id(1L)
			.title("수정한 제목")
			.content("수정한 내용")
			.date(LocalDate.now().minusDays(1))
			.emotionType(EmotionType.NORMAL)
			.build();

		given(diaryService.updateDiary(any(), anyLong(), any())).willReturn(response);

		// when & then
		mockMvc.perform(patch("/api/diaries/{diaryId}", 1L)
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(
				restDocs.document(
					pathParameters(
						parameterWithName("diaryId").description("수정하려는 일기 고유 번호")
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

	@DisplayName("회원의 특정 연도와 월에 해당하는 일기 목록을 조회한다.")
	@Test
	void getMyDiaryListByMonth() throws Exception {
		// given

		LocalDate date = LocalDate.of(2023, 12, 1);
		List<DiaryResponse> diaryList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			EmotionType type = i % 2 == 0 ? BAD : GOOD;
			diaryList.add(
				DiaryResponse.builder()
					.id(i + 1L)
					.emotionType(type)
					.title("제목입니다.")
					.date(date.plusDays(i))
					.build()
			);
		}

		DiaryListResponse response = new DiaryListResponse(diaryList, false);
		given(diaryService.getMyDiaryListByMonth(any(), any())).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/diaries")
				.param("year", "2023")
				.param("month", "12")
				.param("emotionFilter", "all")
				.param("desc", "false")
				.header(AUTHORIZATION, String.format(BEARER_PREFIX, ACCESS_TOKEN))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.diaryList.size()").value(10))
			.andExpect(jsonPath("$.diaryList[0].id").value(1L))
			.andExpect(jsonPath("$.diaryList[0].title").value("제목입니다."))
			.andExpect(jsonPath("$.diaryList[0].emotionType").value(BAD.name()))
			.andExpect(jsonPath("$.diaryList[0].date").value(LocalDate.of(2023, 12, 1).toString()))
			.andDo(
				restDocs.document(
					queryParameters(
						parameterWithName("year").description("조회하고자 하는 연도"),
						parameterWithName("month").description("조회하고자 하는 월"),
						parameterWithName("emotionFilter")
							.description("감정 타입 필터링 - all, good(normal~best), bad(worst~bad)"),
						parameterWithName("lastDate").optional().description("이전에 조회한 일기 목록의 마지막 연도-월"),
						parameterWithName("desc").description("내림차순 조회 여부")
					),
					responseFields(
						fieldWithPath("diaryList").description("회원의 특정 연도와 월에 해당하는 일기 목록"),
						fieldWithPath("diaryList[].id").description("일기 고유 번호"),
						fieldWithPath("diaryList[].title").description("일기 제목"),
						fieldWithPath("diaryList[].date").description("날짜"),
						fieldWithPath("diaryList[].emotionType")
							.description("감정 점수에 기반한 감정 타입 (WORST/BAD/NORMAL/GOOD/BEST)"),
						fieldWithPath("hasNext").description("다음에 조회할 일기 목록이 존재하는지 여부")
					)
				)
			);
	}
}