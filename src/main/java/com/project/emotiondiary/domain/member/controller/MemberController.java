package com.project.emotiondiary.domain.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.emotiondiary.domain.member.service.MemberService;

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
}
