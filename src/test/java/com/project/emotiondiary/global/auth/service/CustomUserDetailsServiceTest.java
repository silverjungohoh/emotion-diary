package com.project.emotiondiary.global.auth.service;

import static com.project.emotiondiary.domain.member.entity.Role.*;
import static com.project.emotiondiary.global.error.type.AuthErrorCode.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.repository.MemberRepository;
import com.project.emotiondiary.global.auth.model.CustomUserDetails;
import com.project.emotiondiary.global.error.exception.AuthException;
import com.project.emotiondiary.helper.IntegrationTestSupport;

class CustomUserDetailsServiceTest extends IntegrationTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@DisplayName("조회 시 회원이 존재하면 사용자 정보를 반환한다.")
	@Test
	void loadUserByUsername() {
		// given
		String email = "test@test.com";
		String password = passwordEncoder.encode("zxc123");

		Member member = Member.builder()
			.email("test@test.com")
			.role(ROLE_USER)
			.password(password)
			.build();

		memberRepository.save(member);

		// when
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		// then
		assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
		assertThat(userDetails.getPassword()).isEqualTo(password);
		assertThat(userDetails.getUsername()).isEqualTo(email);
		assertThat(userDetails.isEnabled()).isTrue();

		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		assertThat(authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))).isTrue();
	}

	@DisplayName("조회 시 회원이 존재하지 않는 경우 예외를 던진다.")
	@Test
	void loadUserByUsernameNotFound() {
		// given
		String email = "test@test.com";

		// when & then
		assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
			.isInstanceOf(AuthException.class)
			.hasMessage(AUTHENTICATION_FAILED.getMessage());
	}
}