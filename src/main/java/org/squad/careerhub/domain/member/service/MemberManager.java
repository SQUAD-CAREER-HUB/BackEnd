package org.squad.careerhub.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;

@RequiredArgsConstructor
@Component
public class MemberManager {

    private final MemberJpaRepository memberJpaRepository;

}