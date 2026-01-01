package org.squad.careerhub.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.squad.careerhub.IntegrationTestSupport;
import org.squad.careerhub.domain.member.MemberFixture;
import org.squad.careerhub.domain.member.repository.MemberJpaRepository;

@RequiredArgsConstructor
@Transactional
class MemberManagerIntegrationTest extends IntegrationTestSupport {

    final MemberJpaRepository memberJpaRepository;
    final MemberManager memberManager;
    final EntityManager entityManager;

    @Test
    void 회원의_RefreshToken을_업데이트한다() {
        // given
        var member = MemberFixture.createMember();
        memberJpaRepository.save(member);

        assertThat(member.getRefreshToken()).isNull();

        // when
        var newRefreshToken = "newRefreshToken";
        memberManager.updateRefreshToken(member.getId(), newRefreshToken);

        entityManager.flush();

        // then
        var updatedMember = memberJpaRepository.findById(member.getId()).orElseThrow();
        assertThat(updatedMember.getRefreshToken()).isEqualTo(newRefreshToken);
    }

}