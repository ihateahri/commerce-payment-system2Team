package com.example.commercepaymentsystem2team.domain.member.controller;


import com.example.commercepaymentsystem2team.domain.member.dto.MemberResponse;
import com.example.commercepaymentsystem2team.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberService.getMe(memberId));
    }
}