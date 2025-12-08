package org.squad.careerhub.domain.archive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionCreateRequest;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionPageResponse;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionResponse;
import org.squad.careerhub.domain.archive.controller.dto.PersonalQuestionUpdateRequest;
import org.squad.careerhub.domain.archive.service.QuestionArchiveService;
import org.squad.careerhub.global.annotation.LoginMember;

@RestController
@RequestMapping("/v1/applications/{applicationId}/questions")
@RequiredArgsConstructor
public class QuestionArchiveController extends QuestionArchiveDocsController {

    private final QuestionArchiveService questionArchiveService;

    @Override
    @PostMapping
    public ResponseEntity<PersonalQuestionResponse> registerPersonalQuestion(
        @PathVariable Long applicationId,
        @Valid @RequestBody PersonalQuestionCreateRequest request,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(PersonalQuestionResponse.mock());
    }

    @Override
    @GetMapping
    public ResponseEntity<PersonalQuestionPageResponse> getPersonalQuestions(
        @PathVariable Long applicationId,
        @RequestParam(required = false) Long lastCursorId,
        @RequestParam(required = false, defaultValue = "20") Integer size,
        @LoginMember Long memberId
    ) {

        return ResponseEntity.ok(PersonalQuestionPageResponse.mock());
    }

    @Override
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deletePersonalQuestion(
        @PathVariable Long applicationId,
        @PathVariable Long questionId,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{questionId}")
    public ResponseEntity<PersonalQuestionResponse> updatePersonalQuestion(
        @PathVariable Long applicationId,
        @PathVariable Long questionId,
        @Valid @RequestBody PersonalQuestionUpdateRequest request,
        @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(PersonalQuestionResponse.updateMock());
    }
}