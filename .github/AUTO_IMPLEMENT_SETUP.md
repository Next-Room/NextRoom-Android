# 🤖 자동 구현 시스템 설정 가이드

GitHub 이슈를 작성하면 Gemini AI가 자동으로 코드를 작성하고 PR을 생성하는 시스템입니다.

---

## 📋 목차

1. [개요](#개요)
2. [사전 준비](#사전-준비)
3. [설정 방법](#설정-방법)
4. [사용 방법](#사용-방법)
5. [비용 안내](#비용-안내)
6. [문제 해결](#문제-해결)

---

## 개요

### 🎯 작동 방식

```
GitHub 이슈 생성 (auto-implement 라벨)
    ↓
GitHub Actions 자동 실행
    ↓
Gemini AI가 이슈 분석
    ↓
Kotlin 코드 자동 생성
    ↓
새 브랜치에 커밋 & 푸시
    ↓
Pull Request 자동 생성
    ↓
이슈에 결과 코멘트
```

### ✨ 장점

- ⏱️ 반복 작업 자동화 (2-5분 내 완료)
- 💰 저렴한 비용 (이슈 1건당 $0.01~0.05)
- 🏗️ Clean Architecture 자동 준수
- 📝 일관된 코드 스타일
- 🔄 24시간 작동

---

## 사전 준비

### 1. Gemini API Key 발급

1. **Google AI Studio 접속**
   - https://aistudio.google.com/app/apikey 방문

2. **API Key 생성**
   - "Create API Key" 클릭
   - 새 프로젝트 생성 또는 기존 프로젝트 선택
   - API Key 복사 (다시 볼 수 없으니 안전하게 보관!)

3. **비용 확인**
   - Gemini 2.0 Flash: 무료 할당량 제공
   - 초과 시: 입력 $0.10/MTok, 출력 $0.40/MTok
   - 대부분 무료 범위 내에서 사용 가능

### 2. GitHub Personal Access Token 확인

- GitHub Actions에서 자동으로 제공되는 `GITHUB_TOKEN` 사용
- 추가 발급 불필요 (기본 권한으로 충분)

---

## 설정 방법

### Step 1: GitHub Secrets 등록

1. **Repository Settings 이동**
   ```
   GitHub Repository 페이지
   → Settings (상단 탭)
   → Secrets and variables (왼쪽 메뉴)
   → Actions
   ```

2. **New repository secret 클릭**

3. **Gemini API Key 등록**
   - Name: `GEMINI_API_KEY`
   - Secret: 발급받은 API Key 붙여넣기
   - Add secret 클릭

4. **완료 확인**
   - Repository secrets에 `GEMINI_API_KEY`가 표시되면 성공

### Step 2: GitHub Labels 설정

1. **Labels 페이지 이동**
   ```
   GitHub Repository 페이지
   → Issues (상단 탭)
   → Labels (오른쪽)
   ```

2. **New label 클릭**
   - Label name: `auto-implement`
   - Description: `🤖 Gemini AI가 자동으로 구현합니다`
   - Color: `#7057ff` (보라색) 또는 원하는 색상
   - Create label 클릭

### Step 3: 권한 설정 확인

1. **Settings → Actions → General 이동**

2. **Workflow permissions 확인**
   - ✅ "Read and write permissions" 선택
   - ✅ "Allow GitHub Actions to create and approve pull requests" 체크
   - Save 클릭

---

## 사용 방법

### 1️⃣ 이슈 작성

1. **Issues 페이지에서 New issue 클릭**

2. **"🤖 자동 구현 요청" 템플릿 선택**
   - 템플릿이 자동으로 로드됨
   - `auto-implement` 라벨이 자동으로 추가됨

3. **이슈 내용 작성**
   ```markdown
   ## 📋 구현 요청 내용

   ### 🎯 목적
   사용자 닉네임 변경 기능 추가

   ### 📝 상세 설명

   **현재 상황:**
   - 사용자 프로필에서 닉네임을 볼 수만 있고 변경할 수 없음

   **원하는 결과:**
   - 프로필 화면에 "닉네임 변경" 버튼 추가
   - 클릭 시 다이얼로그에서 새 닉네임 입력
   - API 호출하여 서버에 저장
   - 성공 시 Toast 메시지 표시

   **참고 사항:**
   - API: PATCH /api/v1/users/nickname
   - Request: { "nickname": "새닉네임" }
   - MyPageFragment에 구현

   ### 🏗️ 아키텍처 가이드
   - [x] Domain Layer: UpdateNicknameUseCase
   - [x] Data Layer: UserRepository에 updateNickname 추가
   - [x] Presentation Layer: MyPageViewModel 수정
   ```

4. **Submit new issue 클릭**

### 2️⃣ 자동 실행 확인

1. **Actions 탭에서 진행 상황 확인**
   ```
   Repository → Actions → "Auto Implement from Issue" 워크플로우
   ```

2. **2-5분 후 완료**
   - ✅ 성공 시: 이슈에 코멘트 + PR 생성
   - ❌ 실패 시: 이슈에 오류 메시지

### 3️⃣ Pull Request 리뷰

1. **Pull Requests 탭 확인**
   - 자동 생성된 PR 확인

2. **코드 리뷰**
   - Files changed 탭에서 변경사항 확인
   - 필요 시 추가 수정

3. **테스트**
   - 로컬에서 브랜치 pull 받아 빌드 테스트
   - 기능 동작 확인

4. **Merge**
   - 문제없으면 Merge pull request

---

## 비용 안내

### Gemini API 요금제

| 모델 | 입력 | 출력 | 무료 할당량 |
|------|------|------|------------|
| Gemini 2.0 Flash | $0.10/MTok | $0.40/MTok | 1,500 req/day |

### 예상 비용

**이슈 1건당:**
- 간단한 기능 (파일 1-2개): ~$0.01
- 중간 복잡도 (파일 3-5개): ~$0.03
- 복잡한 기능 (파일 6개 이상): ~$0.05-0.10

**월 사용 예상:**
- 하루 5건 처리: 무료 범위 내
- 월 100건 처리: ~$3-5
- 월 200건 처리: ~$6-10

💡 **팁**: 대부분 무료 할당량 내에서 사용 가능합니다!

---

## 작성 팁

### ✅ 좋은 이슈 작성법

1. **구체적으로 작성**
   ```markdown
   ❌ 나쁨: "로그인 기능 추가"
   ✅ 좋음: "Google 로그인 버튼을 LoginFragment에 추가하고,
            클릭 시 Google Sign-In API 호출하여 토큰을 받아
            서버 POST /api/v1/auth/google로 전송"
   ```

2. **현재 상황과 원하는 결과 명시**
   ```markdown
   **현재 상황:**
   - MainActivity에서 RecyclerView로 테마 목록 표시
   - 클릭 이벤트 없음

   **원하는 결과:**
   - 아이템 클릭 시 ThemeDetailFragment로 이동
   - 테마 ID를 SafeArgs로 전달
   ```

3. **파일 위치 명시**
   ```markdown
   - 수정할 파일: presentation/ui/main/MainActivity.kt
   - 참고할 파일: presentation/ui/theme/ThemeDetailFragment.kt
   ```

4. **API 정보 포함 (있다면)**
   ```markdown
   - Endpoint: GET /api/v1/themes/{id}
   - Response: { id: Int, name: String, ... }
   ```

### ❌ 피해야 할 작성법

- 너무 추상적인 설명
- 여러 기능을 한 이슈에 포함
- 파일 위치나 구조 정보 없음
- "잘 만들어줘", "알아서 해줘" 같은 모호한 표현

---

## 문제 해결

### ❓ 자주 묻는 질문

**Q1. 이슈를 생성했는데 아무 반응이 없어요**

- `auto-implement` 라벨이 제대로 추가되었는지 확인
- Actions 탭에서 워크플로우 실행 여부 확인
- Secrets에 `GEMINI_API_KEY`가 등록되었는지 확인

**Q2. "API Key가 유효하지 않습니다" 오류**

- Gemini API Key를 다시 확인
- Google AI Studio에서 API Key 재발급
- GitHub Secrets 업데이트

**Q3. PR은 생성되었는데 빌드가 안 돼요**

- AI가 생성한 코드는 완벽하지 않을 수 있음
- 생성된 코드를 리뷰하고 수동으로 수정
- 이슈에 더 구체적인 정보 추가 후 재시도

**Q4. 원하는 결과와 다른 코드가 생성되었어요**

- 이슈 내용을 더 구체적으로 작성
- 참고할 기존 코드 파일 경로 명시
- PR에 코멘트로 수정 요청 작성 후 수동 수정

**Q5. 여러 파일을 수정해야 하는데 일부만 생성되었어요**

- 한 이슈에 너무 많은 작업을 포함하지 말 것
- 큰 기능은 여러 개의 작은 이슈로 분할
- 각 이슈는 하나의 명확한 목표를 가지도록

**Q6. 비용이 얼마나 나올지 걱정돼요**

- Google AI Studio Console에서 사용량 모니터링 가능
- 무료 할당량(1,500 req/day)이 크므로 대부분 무료
- 초과하더라도 이슈 1건당 몇 센트 수준

### 🐛 디버깅

**Actions 로그 확인:**
```
Repository → Actions → 실패한 워크플로우 클릭 → 각 Step 로그 확인
```

**로컬 테스트:**
```bash
# 생성된 브랜치 확인
git fetch origin
git checkout feature/issue-XX-브랜치명

# 빌드 테스트
./gradlew assembleDebug
```

---

## 📞 지원

문제가 해결되지 않으면:

1. **GitHub Issues에 문의**
   - 라벨: `bug` 또는 `question`
   - 오류 로그 첨부

2. **Actions 로그 첨부**
   - 실패한 워크플로우의 전체 로그 복사

---

## 🎉 다음 단계

설정이 완료되었습니다! 이제:

1. 테스트 이슈를 작성해보세요
2. 자동 생성된 PR을 확인하세요
3. 피드백을 반영하여 이슈 작성 스타일을 개선하세요

**Happy Coding! 🚀**
