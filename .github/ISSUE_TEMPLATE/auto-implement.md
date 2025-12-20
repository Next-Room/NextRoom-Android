---
name: 🤖 자동 구현 요청
about: Gemini AI가 자동으로 코드를 작성합니다
title: '[AUTO] '
labels: 'auto-implement'
assignees: ''
---

## 📋 구현 요청 내용

> **중요**: 이슈 생성 후 자동으로 Gemini AI가 코드를 분석하고 구현합니다.
> 가능한 자세하고 명확하게 작성해주세요.

### 🎯 목적
<!-- 무엇을 구현하거나 수정하고 싶은가요? -->


### 📝 상세 설명
<!-- 구현 내용을 자세히 설명해주세요 -->

**현재 상황:**
<!-- 현재 어떤 문제가 있나요? 또는 어떤 기능이 없나요? -->

**원하는 결과:**
<!-- 구현 후 어떻게 동작하길 원하나요? -->

**참고 사항:**
<!-- 추가로 고려해야 할 사항이 있나요? (기존 코드 위치, 참고할 파일 등) -->


### 🏗️ 아키텍처 가이드
<!-- 해당하는 항목에 체크해주세요 -->

- [ ] **Domain Layer**: 비즈니스 로직, 엔티티, Use Case
- [ ] **Data Layer**: Repository 구현, API 연동, 로컬 DB
- [ ] **Presentation Layer**: UI, ViewModel, Fragment
- [ ] **기타**: 유틸리티, 확장 함수 등

### 💡 예상 파일 위치
<!-- 어느 모듈/패키지에서 작업이 이루어질까요? (선택사항) -->

- 모듈: `presentation` / `data` / `domain` / `commonutil`
- 패키지: `com.nextroom.nextroom.___`
- 파일명: (있다면)

---

## 📚 작성 예시

<details>
<summary>예시 1: 새로운 기능 추가</summary>

### 🎯 목적
사용자 프로필 편집 기능 추가

### 📝 상세 설명

**현재 상황:**
- 사용자 프로필을 볼 수는 있지만 편집할 수 없음
- MyPageFragment에서 프로필 정보를 표시만 함

**원하는 결과:**
- MyPageFragment에 "편집" 버튼 추가
- 버튼 클릭 시 ProfileEditFragment로 이동
- 닉네임, 소개 문구 수정 가능
- 저장 시 API 호출하여 서버에 반영

**참고 사항:**
- API 엔드포인트: `PATCH /api/v1/users/profile`
- Request: `{ nickname: String, bio: String }`
- 기존 MyPageFragment는 `presentation/ui/mypage/` 경로에 있음

### 🏗️ 아키텍처 가이드
- [x] Presentation Layer: ProfileEditFragment, ProfileEditViewModel
- [x] Data Layer: UserRepository에 updateProfile 메서드 추가
- [x] Domain Layer: UpdateProfileUseCase

</details>

<details>
<summary>예시 2: 버그 수정</summary>

### 🎯 목적
힌트 요청 시 크래시 수정

### 📝 상세 설명

**현재 상황:**
- 힌트를 연속으로 빠르게 요청하면 앱이 크래시됨
- `HintViewModel`에서 `NullPointerException` 발생

**원하는 결과:**
- 중복 요청 방지 로직 추가
- 힌트 요청 중일 때는 버튼 비활성화
- 안전한 null 처리

**참고 사항:**
- 크래시 로그: Firebase Crashlytics에서 확인
- 파일 위치: `presentation/ui/hint/HintViewModel.kt`

### 🏗️ 아키텍처 가이드
- [x] Presentation Layer: HintViewModel 수정

</details>

---

## ⚙️ 자동 구현 프로세스

이슈 생성 시 다음 과정이 자동으로 진행됩니다:

1. ✅ GitHub Actions 트리거
2. 🤖 Gemini AI가 이슈 분석
3. 📝 코드 자동 생성
4. 🌿 새 브랜치 생성 (`feature/issue-{번호}-제목`)
5. 📤 Pull Request 자동 생성
6. 💬 이슈에 결과 코멘트

**예상 소요 시간**: 2-5분

---

## ⚠️ 주의사항

- 자동 생성된 코드는 **반드시 리뷰**가 필요합니다
- 복잡한 로직은 수동 수정이 필요할 수 있습니다
- 테스트 코드는 자동 생성되지 않습니다
- 빌드 오류가 발생할 수 있으니 확인해주세요
