package com.project.emotiondiary.domain.diary.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRequestParam {

	private Integer year;

	private Integer month;

	private String emotionFilter; // "all", "bad", "good"

	private LocalDate lastDate;

	private boolean desc;
}
