package com.project.emotiondiary.global.auth.service;

import static com.project.emotiondiary.global.error.type.AuthErrorCode.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.auth.model.CustomUserDetails;
import com.project.emotiondiary.global.error.exception.AuthException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Member member = memberRepository.findByEmail(username)
			.orElseThrow(() -> new AuthException(AUTHENTICATION_FAILED));

		return new CustomUserDetails(member);
	}
}
