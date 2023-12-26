package com.project.emotiondiary.domain.diary.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.emotiondiary.domain.diary.entity.Diary;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

	boolean existsByDate(LocalDate date);
}
