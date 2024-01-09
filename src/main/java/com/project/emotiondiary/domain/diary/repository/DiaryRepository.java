package com.project.emotiondiary.domain.diary.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.emotiondiary.domain.diary.entity.Diary;
import com.project.emotiondiary.domain.diary.entity.EmotionType;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

	boolean existsByDate(LocalDate date);

	@Query(value = "select d from Diary d"
		+ " where MONTH(d.date) = :month and YEAR(d.date) = :year"
		+ " and d.member.id = :memberId"
		+ " and d.emotionType in :emotionTypes"
		+ " and (:lastDate is null or d.date < :lastDate)"
	)
	Slice<Diary> findDiaryList(
		@Param("year") Integer year,
		@Param("month") Integer month,
		@Param("memberId") Long memberId,
		@Param("emotionTypes") List<EmotionType> emotionTypes,
		@Param("lastDate") LocalDate lastDate,
		Pageable pageable
	);

	@Modifying
	@Query(value = "delete from Diary d where d.member.id = :memberId")
	void deleteAllByMemberId(@Param("memberId") Long memberId);
}
