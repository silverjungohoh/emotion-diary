package com.project.emotiondiary.domain.diary.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryListResponse {

	private List<DiaryResponse> diaryList;

	private boolean hasNext;
}
