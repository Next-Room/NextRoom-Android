# AI Code Review 설정 가이드

이 문서는 Gemini API를 활용한 자동 PR 리뷰 기능을 설정하는 방법을 설명합니다.

## 개요

PR이 `main` 브랜치로 생성되거나 업데이트될 때마다, Gemini AI가 자동으로 코드를 분석하고 리뷰 코멘트를 작성합니다.

### 리뷰 항목

- ✅ 코드 품질 및 베스트 프랙티스
- ✅ 버그 및 잠재적 이슈 탐지
- ✅ 프로젝트 규칙 준수 (CLAUDE.md 기반)
- ✅ 성능 최적화 제안

## 사전 요구사항

### 1. Gemini API Key 발급

1. [Google AI Studio](https://makersuite.google.com/app/apikey)에 접속합니다
2. Google 계정으로 로그인합니다
3. "Get API Key" 또는 "Create API Key" 버튼을 클릭합니다
4. 새 API 키를 생성하거나 기존 API 키를 복사합니다
5. API 키를 안전한 곳에 보관합니다

**주의사항:**

- API 키는 절대 코드에 직접 포함하지 마세요
- API 키가 노출되면 즉시 재발급하세요
- Gemini API는 일일 요청 제한이 있으니 [할당량](https://ai.google.dev/pricing)을 확인하세요

### 2. GitHub Secrets 설정

1. GitHub 저장소로 이동합니다
2. **Settings** → **Secrets and variables** → **Actions**로 이동합니다
3. **New repository secret** 버튼을 클릭합니다
4. 다음 Secret을 추가합니다:

    - **Name:** `GEMINI_API_KEY`
    - **Value:** 위에서 발급받은 Gemini API 키

5. **Add secret** 버튼을 클릭하여 저장합니다

## 워크플로우 동작 방식

### 트리거 조건

다음 상황에서 자동으로 실행됩니다:

- PR이 `main` 브랜치로 생성될 때
- PR에 새로운 커밋이 푸시될 때
- PR이 재오픈될 때

### 실행 과정

1. **코드 체크아웃**: PR의 변경사항을 가져옵니다
2. **Python 환경 설정**: 필요한 라이브러리를 설치합니다
3. **프로젝트 컨텍스트 로드**: `CLAUDE.md` 파일을 읽어 프로젝트 규칙을 파악합니다
4. **변경사항 분석**: PR의 diff를 추출합니다
5. **Gemini API 호출**: AI에게 코드 리뷰를 요청합니다
6. **리뷰 코멘트 작성**: PR에 자동으로 리뷰 코멘트를 작성합니다

## 사용 예시

### PR 생성 후 자동 리뷰

```bash
# feature 브랜치에서 작업
git checkout -b feature/new-feature
# ... 코드 작성 ...
git add .
git commit -m "feat: Add new feature"
git push origin feature/new-feature

# GitHub에서 main 브랜치로 PR 생성
# → AI Code Review가 자동으로 실행됨
```

### 리뷰 결과 확인

1. GitHub PR 페이지로 이동합니다
2. **Checks** 탭에서 "AI Code Review" 워크플로우 상태를 확인합니다
3. 실행이 완료되면 PR 코멘트에 리뷰가 자동으로 작성됩니다

### 리뷰 코멘트 형식

```markdown
## 🤖 AI Code Review (Gemini)

[리뷰 내용]

- 🔴 Critical: 심각한 문제
- 🟡 Warning: 주의가 필요한 사항
- 💡 Suggestion: 개선 제안

---
*This review was automatically generated using Gemini AI.
Please use your judgment when addressing the feedback.*
```

## 문제 해결

### 워크플로우가 실행되지 않는 경우

1. PR의 target 브랜치가 `main`인지 확인하세요
2. `.github/workflows/pr-review.yml` 파일이 main 브랜치에 존재하는지 확인하세요
3. GitHub Actions 권한이 활성화되어 있는지 확인하세요
    - **Settings** → **Actions** → **General** → **Allow all actions**

### "Missing required environment variables" 에러

- `GEMINI_API_KEY` Secret이 올바르게 설정되었는지 확인하세요
- Secret 이름의 대소문자가 정확한지 확인하세요

### "API rate limit exceeded" 에러

- Gemini API의 일일 할당량을 초과한 경우입니다
- [Google AI Studio](https://makersuite.google.com/app/apikey)에서 사용량을 확인하세요
- 필요한 경우 유료 플랜으로 업그레이드하거나 다음 날까지 기다리세요

### 리뷰 품질 개선

리뷰 품질을 높이려면 `.github/scripts/ai_code_review.py`에서 다음을 조정할 수 있습니다:

1. **모델 변경**: `gemini-1.5-pro` → `gemini-1.5-pro-latest`
2. **Temperature 조정**: 현재 0.3 (낮을수록 일관적, 높을수록 창의적)
3. **Max tokens 증가**: 더 자세한 리뷰를 원하는 경우

## 비용 관련

### Gemini API 가격 (2024년 12월 기준)

- **Gemini 1.5 Pro**:
    - 무료 할당량: 일 50 requests, 분당 2 requests
    - 유료: $0.00025 / 1K tokens (입력), $0.0005 / 1K tokens (출력)

### 예상 비용

- 평균 PR 리뷰당: 약 4,000~8,000 tokens
- 월 100개 PR 기준: 무료 할당량 내에서 사용 가능 (하루 2~3개 PR)
- 더 많은 PR이 필요한 경우: 유료 플랜 전환 검토

## 추가 설정 옵션

### 특정 파일 제외

`.github/scripts/ai_code_review.py`의 `get_pr_diff()` 함수에서 필터링을 추가할 수 있습니다:

```python
def get_pr_diff(pr: PullRequest.PullRequest) -> str:
    files = pr.get_files()
    diff_text = ""

    # 제외할 파일 패턴
    exclude_patterns = ['.md', '.txt', 'build.gradle', 'gradle-wrapper.properties']

    for file in files:
        # 특정 파일 제외
        if any(pattern in file.filename for pattern in exclude_patterns):
            continue
        # ... 나머지 코드
```

### 리뷰 언어 변경

`create_review_prompt()` 함수에서 다음 줄을 수정하세요:

```python
- Use Korean language for the review comments  # 한국어
- Use English language for the review comments  # 영어
```

## 참고 자료

- [Gemini API 문서](https://ai.google.dev/docs)
- [GitHub Actions 문서](https://docs.github.com/en/actions)
- [PyGithub 문서](https://pygithub.readthedocs.io/)
