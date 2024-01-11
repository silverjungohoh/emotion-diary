package com.project.emotiondiary.domain.member.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.emotiondiary.domain.member.entity.Member;
import com.project.emotiondiary.domain.member.model.LoginRequest;
import com.project.emotiondiary.domain.member.model.LoginResponse;
import com.project.emotiondiary.domain.member.model.ReissueResponse;
import com.project.emotiondiary.domain.member.model.SignUpRequest;
import com.project.emotiondiary.domain.member.model.SignUpResponse;
import com.project.emotiondiary.domain.member.model.UpdatePasswordRequest;
import com.project.emotiondiary.domain.member.model.WithDrawRequest;
import com.project.emotiondiary.domain.member.service.MemberService;
import com.project.emotiondiary.global.auth.annotation.AuthRequired;
import com.project.emotiondiary.global.auth.annotation.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/check/email")
	public ResponseEntity<Map<String, String>> checkEmailDuplicated(@RequestParam(name = "email") String email) {

		Map<String, String> response = memberService.checkEmailDuplicated(email);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/check/nickname")
	public ResponseEntity<Map<String, String>> checkNicknameDuplicated(
		@RequestParam(name = "nickname") String nickname) {

		Map<String, String> response = memberService.checkNicknameDuplicated(nickname);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {

		SignUpResponse response = memberService.signUp(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

		LoginResponse response = memberService.login(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<ReissueResponse> reissueToken(@RequestHeader(name = "REFRESH") String refreshToken) {

		ReissueResponse response = memberService.reissueToken(refreshToken);
		return ResponseEntity.ok(response);
	}

	@AuthRequired
	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(@RequestHeader(name = "Authorization") String bearerToken,
		@AuthUser Member member) {

		Map<String, String> response = memberService.logout(member, bearerToken);
		return ResponseEntity.ok(response);
	}

	@AuthRequired
	@DeleteMapping("/info")
	public ResponseEntity<Map<String, String>> withdraw(@AuthUser Member member, @RequestBody WithDrawRequest request) {

		Map<String, String> response = memberService.withdraw(member, request);
		return ResponseEntity.ok(response);
	}

	@AuthRequired
	@PatchMapping("/info")
	public ResponseEntity<Map<String, String>> updatePassword(@AuthUser Member member,
		@Valid @RequestBody UpdatePasswordRequest request) {

		Map<String, String> response = memberService.updatePassword(member, request);
		return ResponseEntity.ok(response);
	}
}
