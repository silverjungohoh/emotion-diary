package com.project.emotiondiary.domain.member.service;

import static com.project.emotiondiary.global.error.type.MemberErrorCode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.entity.Role;
import com.project.emotiondiary.domain.member.model.LoginRequest;
import com.project.emotiondiary.domain.member.model.LoginResponse;
import com.project.emotiondiary.domain.member.model.SignUpRequest;
import com.project.emotiondiary.domain.member.model.SignUpResponse;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.auth.jwt.JwtProvider;
import com.project.emotiondiary.global.error.exception.MemberException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	@Transactional(readOnly = true)
	public Map<String, String> checkEmailDuplicated(String email) {

		if (memberRepository.existsByEmail(email)) {
			throw new MemberException(ALREADY_EXIST_EMAIL);
		}

		return getMessage("사용 가능한 이메일입니다.");
	}

	@Transactional(readOnly = true)
	public Map<String, String> checkNicknameDuplicated(String nickname) {

		if(memberRepository.existsByNickname(nickname)) {
			throw new MemberException(ALREADY_EXIST_NICKNAME);
		}

		return getMessage("사용 가능한 닉네임입니다.");
	}

	@Transactional
	public SignUpResponse signUp(SignUpRequest request) {

		if(!Objects.equals(request.getPassword(), request.getPasswordCheck())) {
			throw new MemberException(MISMATCH_PASSWORD_CHECK);
		}

		Member member = Member.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.nickname(request.getNickname())
			.gender(request.getGender())
			.role(Role.ROLE_USER)
			.build();

		memberRepository.save(member);
		return SignUpResponse.from(member);
	}

	@Transactional
	public LoginResponse login(LoginRequest request) {

		Member member = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

		if(!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
			throw new MemberException(FAIL_TO_LOGIN);
		}

		String accessToken = jwtProvider.generateAccessToken(request.getEmail(), member.getRole());
		String refreshToken = jwtProvider.generateRefreshToken(request.getEmail());
		return new LoginResponse(accessToken, refreshToken);
	}

	private static Map<String, String> getMessage(String message) {
		Map<String, String> result = new HashMap<>();
		result.put("message", message);
		return result;
	}
}
