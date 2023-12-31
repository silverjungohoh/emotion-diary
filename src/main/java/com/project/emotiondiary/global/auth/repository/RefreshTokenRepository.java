package com.project.emotiondiary.global.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.project.emotiondiary.global.auth.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {


}
