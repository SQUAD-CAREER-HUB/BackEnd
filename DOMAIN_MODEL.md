# 도메인 모델

## 공통 속성

- `id`: Long
- `createdAt` :생성 시각
- `updatedAt` : 수정 시각
- `status` : 활성 상태(ENUM)
  - **ACTIVE**, **DELETED**

## 공통 행위

- `isActive()`  : 활성 여부
- `isDeleted()` : 삭제 여부

---

## 회원(Member)

---

### 속성

- `socialAccount`: 회원 소셜 계정
- `nickname`: 닉네임
- `role`: 회원 권한
- `refreshToken` : 리프레쉬 토큰

### 행위

- `static create()` : 회원 생성
- `updateProfile()`:  회원 정보를 수정한다.
- `updateRefreshToken()`: JWT 리프레쉬 토큰 업데이트
- `clearRefreshToken()`: JWT 리프레쉬 토큰 초기화

### 규칙
- 이메일은 중복을 허용하지 않는다.
- 닉네임은 최대 30글자를 허용한다.

---

## 회원 소셜 계정(MemberSocialAccount)(VO)
### 속성

- `email`: 소셜 이메일
- `provider`: 소셜 제공자
- `socialId`: 소셜 제공 ID

### 행위
- `static create()` : 소셜 계정 생성

---

## 지원서 (Application)

---

### 속성

- `authorId` : 작성자 ID
- `jobPostingUrl` : 채용 공고 URL
- `company`: 회사명
- `position` :  직무
- `jobLocation` : 근무지
- `deadline` : 마감일
- `applicationStatus` : 지원 상태
- `submittedAt` : 지원서 제출 날짜
- `applicationMethod` : 지원 방식
- `memo` : 메모

### 행위
- `static register()` : 지원서 등록
- `changeApplicationStatus()` : 지원 상태 변경
- `updateMemo()` : 메모 변경
- `isDeadlinePassed()` : 마감일 경과 여부 확인

### 규칙
- 채용 공고 URL은 필수 값이 아니
## 학습 참여자(Learning_participant)

---

### 속성

- `learning_id` :  학습 id
- `member_id`  : 회원 id
- `role` : 학습 역할(**Learner**, **Leader**)
- `joined_at` : 참여 시점
- `left_at` : 탈퇴 시점

### 행위

- `create()` : 참여자 생성
- `left()` : 탈퇴하다
- `isLeader()` : 방장 확인 여부
- `isLeft()` : 탈퇴 여부

### 규칙

- 학습에 참여한 시점부터 문제 조회, 풀이가 가능하다.
- 참여 시점 전 문제는 볼 수 없다.

## 문제(Problem)

---

### 속성
- `learning_id` : 학습 id
- `content` : 내용
- `explanation` : 해설
- `category` : 카테고리(ENUM)
- `difficulty` : 난이도
- `prompt_version` : 프롬프트 버전

### 행위

### 규칙

---

## 일일 학습 과제(DailyAssignment)

---

### 속성

- `learning_id`: 학습 id
- `problem_id`: 문제 id
- `member_id` : 할당된 멤버 id
- `scheduled_at` : 발송 예정 시각
- `sentStatus` :발송 상태
- `sent_at` : 실제 발송 시각

### 행위

- `isSent()` : 발송 여부 조회
- `sent()` : 발송

### 규칙

## 답변(Answer)

---

### 속성

- `daily_assignment_id` : 일일 학습 과제 Id
- `content` : 내용
- `submitted_at` : 답변 시각
- `answerStatus` : 답변 상태(SUBMITTED / LATE / NOT_SUBMITTED)

### 행위

- `submit()` : 답변

### 규칙

## 초대 링크(InviteLink)

---

### 속성

- `learningId` : 학습 id
- `member_id` : 발급자 id
- `link` : 링크
- `expires_at` :만료기간

### 행위

### 규칙

## 즐겨찾기(favorite)

---

### 속성

- `memberId` : 회원 id
- `problemId`: 문제 id

### 행위

### 규칙

## 알림(Notification)

---

### 속성

### 행위

### 규칙

---

## QnA

---

### 속성

- 작성자 id
-
- 내용
- 답변
- 프

### 행위

### 규칙

---

## 좋아요

---

### 속성

### 행위

### 규칙