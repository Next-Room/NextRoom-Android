# CodeRabbit AI Code Review 설정 가이드

이 문서는 [CodeRabbit](https://coderabbit.ai)을 활용한 자동 PR 리뷰 설정 방법을 설명합니다.

> 이전에는 Gemini API + GitHub Actions(`pr-review.yml`)로 리뷰를 수행했으나, CodeRabbit GitHub App으로 전환했습니다.
> 별도의 워크플로우 파일이나 API 키 Secret이 필요하지 않습니다.

## 개요

CodeRabbit은 GitHub App으로 동작합니다. PR이 열리거나 새 커밋이 푸시되면 자동으로:

- PR 요약 및 변경 파일 워크스루 작성
- 라인 단위 리뷰 코멘트 + 적용 가능한 코드 수정 제안
- 시퀀스 다이어그램 생성
- 리뷰 코멘트에 대한 대화형 응답 (`@coderabbitai` 멘션)

## 설치 방법

### 1. CodeRabbit 계정 생성

GitHub App 설치만으로는 동작하지 않습니다. **먼저 CodeRabbit 계정을 생성해야 합니다.**
별도의 이메일/비밀번호 가입은 없고 GitHub 계정으로 OAuth 로그인합니다. 신용카드는 필요 없습니다.

1. [https://app.coderabbit.ai/login](https://app.coderabbit.ai/login) 접속
2. **Login with GitHub** 클릭
3. CodeRabbit의 GitHub 접근 권한 승인

### 2. 저장소 연결 (GitHub App 설치)

1. CodeRabbit 대시보드에서 `Next-Room` 조직 선택
2. 리뷰할 저장소로 `NextRoom-Android` 추가
3. 이어지는 GitHub App 설치 화면에서 저장소 범위 선택
   - **Only select repositories** → `NextRoom-Android` (권장)
   - 또는 **All repositories**
4. 권한 승인 후 설치 완료

> `Next-Room`은 조직 계정이므로, 조직 owner가 아닌 경우 App 설치에 owner의 승인이 필요합니다.
> 이 경우 GitHub에서 설치 요청이 owner에게 전달됩니다.

### 3. 설정 파일 확인

저장소 루트의 `.coderabbit.yaml`이 리뷰 동작을 정의합니다. 이 파일은 **main 브랜치에 병합되어 있어야**
적용됩니다.

주요 설정:

| 항목                                  | 값                               | 설명                                                          |
|-------------------------------------|---------------------------------|-------------------------------------------------------------|
| `language`                          | `ko-KR`                         | 리뷰 코멘트를 한국어로 작성                                             |
| `reviews.profile`                   | `chill`                         | 사소한 지적을 줄이고 핵심 이슈 위주로 리뷰                                    |
| `reviews.auto_review.base_branches` | `main`, `develop`, `release/*`  | 해당 브랜치를 대상으로 하는 PR 자동 리뷰                                    |
| `reviews.auto_review.drafts`        | `false`                         | Draft PR은 리뷰하지 않음                                           |
| `reviews.path_filters`              | `build/`, 이미지, lint-baseline 제외 | 불필요한 파일 리뷰 방지                                               |
| `reviews.path_instructions`         | 모듈별 규칙                          | Clean Architecture 규칙, safeNavigate, Orbit 제거 등 프로젝트 컨벤션 반영 |
| `reviews.tools.gitleaks`            | `true`                          | 시크릿 유출 탐지                                                   |
| `reviews.tools.actionlint`          | `true`                          | GitHub Actions 워크플로우 검증                                     |

### 4. 요금제

- **Free 플랜**: 퍼블릭 저장소 무제한, 프라이빗 저장소는 요약 기능 중심으로 제한
- **Pro 플랜**: 프라이빗 저장소 전체 리뷰 기능 (개발자당 월 과금)

프라이빗 저장소에서 라인 단위 리뷰가 나오지 않는다면 플랜을 확인하세요.

## 사용법

### 자동 리뷰

```bash
git checkout -b feature/NR-123
# ... 코드 작성 ...
git push origin feature/NR-123
# GitHub에서 develop 또는 main으로 PR 생성 → CodeRabbit이 자동 리뷰
```

### 대화형 명령어

PR 코멘트에 다음과 같이 입력합니다:

| 명령어                              | 동작                            |
|----------------------------------|-------------------------------|
| `@coderabbitai review`           | 증분 리뷰 실행                      |
| `@coderabbitai full review`      | 전체 파일 재리뷰                     |
| `@coderabbitai summary`          | 요약 재생성                        |
| `@coderabbitai resolve`          | CodeRabbit이 남긴 코멘트 일괄 resolve |
| `@coderabbitai pause` / `resume` | 해당 PR 리뷰 일시 중지 / 재개           |
| `@coderabbitai ignore`           | 해당 PR 리뷰 제외                   |
| `@coderabbitai configuration`    | 현재 적용 중인 설정 출력                |
| `@coderabbitai help`             | 전체 명령어 안내                     |

특정 리뷰 코멘트에 답글로 질문하면 그 맥락에서 답변합니다. 수정 제안은 GitHub의
**Commit suggestion** 버튼으로 바로 반영할 수 있습니다.

## 문제 해결

### 리뷰가 실행되지 않는 경우

1. [CodeRabbit 대시보드](https://app.coderabbit.ai)에 저장소가 추가되어 있는지 확인
   (계정만 만들고 저장소를 연결하지 않으면 리뷰가 실행되지 않습니다)
2. GitHub App이 해당 저장소에 설치되어 있는지 확인
   (Settings → Integrations → GitHub Apps → CodeRabbit → Configure)
3. `.coderabbit.yaml`이 **main 브랜치**에 존재하는지 확인 (feature 브랜치에만 있으면 적용되지 않음)
4. PR이 Draft 상태가 아닌지 확인 (`drafts: false` 설정)
5. PR의 base 브랜치가 `main`, `develop`, `release/*` 중 하나인지 확인

### 설정이 반영되지 않는 경우

PR에 `@coderabbitai configuration` 코멘트를 남기면 현재 적용 중인 설정 전문을 출력합니다.
YAML 문법 오류가 있으면 기본 설정으로 폴백하므로, 스키마 검증을 권장합니다:

```yaml
# yaml-language-server: $schema=https://coderabbit.ai/integrations/schema.v2.json
```

이 주석이 파일 최상단에 있으면 IDE에서 자동 완성 및 검증이 동작합니다.

### 리뷰가 너무 많거나 적은 경우

`.coderabbit.yaml`의 `reviews.profile` 값을 조정합니다:

- `chill` (현재): 핵심 이슈 위주, 코멘트 수 적음
- `assertive`: 사소한 개선점까지 적극적으로 지적

## 참고 자료

- [CodeRabbit 공식 문서](https://docs.coderabbit.ai)
- [설정 파일 레퍼런스](https://docs.coderabbit.ai/reference/configuration)
- [대화형 명령어 목록](https://docs.coderabbit.ai/guides/commands)
