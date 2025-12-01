package org.squad.careerhub.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.member.service.MemberService;

@RequiredArgsConstructor
@RestController
public class MemberController extends MemberDocsController {

    private final MemberService memberService;

}