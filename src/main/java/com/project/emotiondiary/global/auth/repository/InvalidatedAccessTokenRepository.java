package com.project.emotiondiary.global.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.project.emotiondiary.global.auth.model.InvalidatedAccessToken;

public interface InvalidatedAccessTokenRepository extends CrudRepository<InvalidatedAccessToken, String> {
}
