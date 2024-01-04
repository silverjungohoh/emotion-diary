package com.project.emotiondiary.global.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.project.emotiondiary.global.auth.model.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findByEmail(String email);
}
