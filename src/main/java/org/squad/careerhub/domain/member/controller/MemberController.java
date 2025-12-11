package org.squad.careerhub.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.member.controller.dto.MemberProfileUpdateRequest;
import org.squad.careerhub.domain.member.service.MemberService;
import org.squad.careerhub.domain.member.service.dto.MemberActivityPageResponse;
import org.squad.careerhub.domain.member.service.dto.MemberProfileResponse;
import org.squad.careerhub.global.annotation.LoginMember;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/members")
public class MemberController extends MemberDocsController {

    private final MemberService memberService;

    @Override
    @GetMapping("/profile")
    public ResponseEntity<MemberProfileResponse> getMyProfile(@LoginMember Long memberId) {
        return ResponseEntity.ok(MemberProfileResponse.mock());
    }

    @Override
    @PatchMapping("/profile")
    public ResponseEntity<MemberProfileResponse> updateMyProfile(
        @Valid @RequestBody MemberProfileUpdateRequest request,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(MemberProfileResponse.mock());
    }

    @Override
    @GetMapping("/activities")
    public ResponseEntity<MemberActivityPageResponse> getMyActivities(
        @RequestParam(value = "lastCursorId", required = false) Long lastCursorId,
        @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
        @LoginMember Long memberId
    ) {

        return ResponseEntity.ok(MemberActivityPageResponse.mock());
    }

    @Override
    @PostMapping("/withdrawal")
    public ResponseEntity<Void> withdraw(@LoginMember Long memberId) {
        return ResponseEntity.noContent().build();
    }
}