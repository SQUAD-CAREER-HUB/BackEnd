# 도메인 모델

## 공통 속성

- `id`: Long
- `createdAt` :생성 시각
- `updatedAt` : 수정 시각
- `status`    : 활성 상태(ENUM)
   - **ACTIVE**, **DELETED**

## 공통 행위

- `isActive()`  : 활성 여부
- `isDeleted()` : 삭제 여부

---

## 회원(Member)

---

### 속성

- `socialAccount`  : 회원 소셜 계정
- `nickname`       : 닉네임
- `role`           : 회원 권한
- `profileImageUrl`: 프로필 이미지
- `refreshToken`   : 리프레쉬 토큰

### 행위

- `static create()`     : 회원 생성
- `updateProfile()`.    :  회원 정보를 수정한다.
- `updateRefreshToken()`: JWT 리프레쉬 토큰 업데이트
- `clearRefreshToken()` : JWT 리프레쉬 토큰 초기화

### 규칙

- 이메일은 중복을 허용하지 않는다.
- 닉네임은 최대 30글자를 허용한다.

---

## 회원 소셜 계정(MemberSocialAccount)(VO)

### 속성

- `email`   : 소셜 이메일
- `provider`: 소셜 제공자
- `socialId`: 소셜 제공 ID

### 행위

- `static create()` : 소셜 계정 생성

---

## 지원서 (Application)

---

### 속성

- `author`            : 작성자
- `jobPostingUrl`     : 채용 공고 URL
- `company`           : 회사명
- `position`          :  직무
- `jobLocation`       : 근무지
- `deadline`          : 마감일
- `applicationStatus` : 지원 상태
- `applicationMethod` : 지원 방식
- `memo`              : 메모

### 행위

- `static register()`         : 지원서 등록
- `changeApplicationStatus()` : 지원 상태 변경
- `updateApplication()`       : 지원서 업데이트
- `isDeadlinePassed()`        : 마감일 경과 여부 확인

### 규칙

- 채용 공고 URL은 필수 값이 아니다

---

## 지원 파일 관리(ApplicationAttachment)

### 속성
- `application` : 지원카드
- `fileUrl` 	: 파일
- `fileName`    : 파일 이름
- `fileType`    : 파일 타입

---

## 지원서 전형 단계(Application Stage)

### 속성
- `application`      : 지원서
- `stageType`		 : 전형 단계

---

## 일정(Schedule)

### 속성
- `author`           : 작성자
- `applicationStage` : 지원서 전형 단계
- `scheduleName` 	 : 일정 이름
- `location`  	     : 장소
- `scheduleResult`   : 일정 결과(WAITING, PASS, PAIL)
- `submissionStatus` : 서류 제출 상태(NOT_SUBMITTED, SUBMITTED) NULL
- `startedAt` 	     : 시작 일시
- `endedAt`   	     : 종료 일시 


## 면접 후기(Interview Review)

---

### 속성

- `author`  : 작성자
- `company` : 회사명
- `position`: 직무
- `type`    : 면접 유형
- `content` : 내용

### 행위

- `static create()` : 면접 후기 생성
- `updateReview()` : 면접 후기 변경

### 규칙

## 면접 질문(InterviewQuestion)

---

### 속성

- `interviewReview`  : 면접 후기
- `question`         : 질문 내용

### 행위

- `static create()`  : 면접 질문 생성
- `updateQuestion()` : 질문 변경

### 규칙
