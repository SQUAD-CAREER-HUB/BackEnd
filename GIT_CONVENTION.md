# HaruHana

# Commit Convention

- ✅ Test - 테스트 업무
- 🗂️ File - 파일 이동 또는 삭제, 파일명 변경
- 📝 Docs - md, yml 등의 문서 작업
- 🔧 Chore - 이외의 애매하거나 자잘한 수정
- ⚙️ Setting - 빌드,패키지,인프라 등 프로젝트 설정
- 💫 Feat - 기능 추가
- 🔨 Refactor - 코드 리팩토링
- 🐛 Fix - 버그 수정

# Branch

- `main`: 배포용 branch
- `develop`: 개발용 branch
- `feat` : 기능 구현용 branch
  - `Issue_종류/#Issue_번호` : branch 생성
  - 예시) Feat/#1
  - 병합 후 삭제

# PR

- 요청 순서
  1. **하나의 Pull Request**는 **하나의 Issue**를 담당한다.
  2. 본인 제외 1명 이상의 리뷰 승인 후 **Squash And Merge** 방식으로 병합한다.
  3. Pull Request 완료 후 관련 브랜치는 삭제한다.
- PR 제목
  - 이슈 제목의 내용과 동일하게 작성한다.
  - ex) 이슈 → `✅Feat: 카카오 로그인` / pr → `Feat: 카카오 로그인`

- **_Pull Request_** 등록 전 다음 항목을 **반드시** 체크한다.
  - [ ] 코드 컨벤션을 준수했습니다.
  - [ ] 테스트 코드를 작성했습니다. (또는 기존 테스트가 통과합니다.)
  - [ ] 빌드 및 실행이 정상적으로 동작합니다.
  - [ ] 관련 문서를 업데이트했습니다. (README, API 문서 등)
  - [ ] 리뷰어를 지정했습니다. (Assignees, Reviewers)
  - [ ] 라벨을 추가했습니다. (Label)
  - [ ] 브랜치명이 컨벤션을 따릅니다. (예: `Feat/#42`)

# Git flow 전략

1. 작업할 내용에 대해 이슈를 판다.
2. 내 로컬에서 develop 브랜치가 최신화 되어있는지 확인한다. (develop 브랜치는 항상 pull을 받아 최신화를 시키자)
3. develop 브랜치로부터 새 브랜치를 만든다. (브랜치명은 커밋타입/#이슈번호)
4. 만든 브랜치에서 작업을 한다
5. 커밋은 쪼개서 작성하며 커밋 메시지는 컨벤션을 따른다.
6. 작업할 내용을 다 끝내면 에러 없이 정상 동작 하는지 확인 후 push 한다.
7. PR을 작성한 후, 리뷰나 수정사항을 반영해준 뒤 develop에 merge 한다.
