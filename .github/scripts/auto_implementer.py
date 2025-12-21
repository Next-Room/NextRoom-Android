#!/usr/bin/env python3
"""
Auto Implementation Bot using Gemini API
GitHub 이슈를 분석하고 자동으로 코드를 생성하여 PR을 만듭니다.
"""

import os
import sys
import json
import base64
import subprocess
from pathlib import Path
from google import genai
from github import Github

# 환경 변수 읽기
GEMINI_API_KEY = os.environ.get('GEMINI_API_KEY')
GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN')
ISSUE_NUMBER = os.environ.get('ISSUE_NUMBER')
ISSUE_TITLE = os.environ.get('ISSUE_TITLE')
ISSUE_BODY = os.environ.get('ISSUE_BODY')
REPOSITORY = os.environ.get('REPOSITORY')

# API 클라이언트 생성
client = genai.Client(api_key=GEMINI_API_KEY)

def read_project_context():
    """프로젝트 컨텍스트 읽기 (CLAUDE.md)"""
    claude_md_path = Path('CLAUDE.md')
    if claude_md_path.exists():
        return claude_md_path.read_text(encoding='utf-8')
    return ""

def get_codebase_structure():
    """주요 디렉토리 구조 파악"""
    result = subprocess.run(
        ['find', '.', '-type', 'd', '-maxdepth', '3',
         '-not', '-path', '*/.*', '-not', '-path', '*/build/*'],
        capture_output=True, text=True
    )
    return result.stdout

def analyze_and_implement(issue_title, issue_body, project_context, structure):
    """Gemini API를 사용하여 이슈 분석 및 구현"""

    prompt = f"""당신은 Android 개발 전문가입니다. NextRoom Android 프로젝트의 GitHub 이슈를 분석하고 구현해야 합니다.

# 프로젝트 정보
{project_context}

# 프로젝트 구조
{structure}

# 이슈 정보
제목: {issue_title}
내용:
{issue_body}

# 작업 지시사항
1. 이슈를 분석하고 무엇을 구현해야 하는지 파악하세요
2. 프로젝트의 아키텍처(Clean Architecture, MVI)를 따르세요
3. 기존 코드 스타일과 패턴을 유지하세요
4. 필요한 파일 변경사항을 JSON 형식으로 출력하세요

# 출력 형식 (JSON만 출력, 다른 텍스트 포함 금지)
{{
  "analysis": "이슈 분석 내용 (한글)",
  "implementation_plan": "구현 계획 (한글)",
  "files": [
    {{
      "path": "상대 경로 (예: presentation/src/main/java/.../SomeFragment.kt)",
      "action": "create|modify|delete",
      "content_base64": "파일 내용을 Base64로 인코딩한 문자열",
      "reason": "변경 이유 (한글)"
    }}
  ],
  "branch_name": "feature/issue-{ISSUE_NUMBER}-브랜치명",
  "commit_message": "커밋 메시지 (한글)",
  "pr_description": "PR 설명 (한글, 마크다운 형식)"
}}

주의사항:
- Kotlin으로 작성하세요
- Hilt, Orbit MVI, View Binding 사용
- 패키지 구조를 준수하세요 (com.nextroom.nextroom.*)
- 파일 경로는 프로젝트 루트 기준 상대 경로
- **중요**: content_base64 필드에는 파일의 전체 내용을 Base64로 인코딩하여 포함하세요
- Base64 인코딩 방법: 파일 내용을 UTF-8 바이트로 변환 후 Base64 인코딩
- JSON 형식만 출력하고 다른 설명은 포함하지 마세요
"""

    try:
        response = client.models.generate_content(
            model='gemini-2.5-flash-lite',
            contents=prompt
        )
        response_text = response.text.strip()

        # JSON 추출 (마크다운 코드 블록 제거)
        if response_text.startswith('```'):
            # ```json ... ``` 형식 처리
            lines = response_text.split('\n')
            # 첫 줄(```json)과 마지막 줄(```) 제거
            if lines[0].startswith('```'):
                lines = lines[1:]
            if lines and lines[-1].strip() == '```':
                lines = lines[:-1]
            response_text = '\n'.join(lines)

        # 응답 저장 (디버깅용)
        print(f"\n📝 Gemini 응답 (처음 500자):")
        print(response_text[:500])
        print("...")

        result = json.loads(response_text)
        return result
    except json.JSONDecodeError as e:
        print(f"❌ JSON 파싱 오류: {e}")
        print(f"\n전체 응답 내용:")
        print(response_text)

        # 파일로 저장
        with open('/tmp/gemini_response.txt', 'w', encoding='utf-8') as f:
            f.write(response_text)
        print("\n응답이 /tmp/gemini_response.txt에 저장되었습니다.")
        raise
    except Exception as e:
        print(f"Error in Gemini API call: {e}")
        print(f"Response: {response.text if 'response' in locals() else 'No response'}")
        raise

def apply_file_changes(files):
    """파일 변경사항 적용"""
    changed_files = []

    for file_info in files:
        file_path = Path(file_info['path'])
        action = file_info['action']

        try:
            if action == 'create' or action == 'modify':
                # 디렉토리 생성
                file_path.parent.mkdir(parents=True, exist_ok=True)

                # Base64 디코딩
                if 'content_base64' in file_info:
                    # Base64로 인코딩된 내용 디코딩
                    content_bytes = base64.b64decode(file_info['content_base64'])
                    content = content_bytes.decode('utf-8')
                elif 'content' in file_info:
                    # 하위 호환성: 일반 텍스트 content 지원
                    content = file_info['content']
                else:
                    print(f"⚠️ {file_path}: content나 content_base64가 없습니다. 건너뜁니다.")
                    continue

                # 파일 작성
                file_path.write_text(content, encoding='utf-8')
                print(f"✓ {action.upper()}: {file_path}")
                changed_files.append(str(file_path))

            elif action == 'delete':
                if file_path.exists():
                    file_path.unlink()
                    print(f"✓ DELETE: {file_path}")
                    changed_files.append(str(file_path))
        except Exception as e:
            print(f"✗ Error processing {file_path}: {e}")
            import traceback
            traceback.print_exc()

    return changed_files

def create_branch_and_commit(branch_name, commit_message, changed_files):
    """브랜치 생성 및 커밋"""
    try:
        # 브랜치 생성
        subprocess.run(['git', 'checkout', '-b', branch_name], check=True)
        print(f"✓ Created branch: {branch_name}")

        # 파일 추가
        for file in changed_files:
            subprocess.run(['git', 'add', file], check=True)

        # 커밋
        full_commit_message = f"""{commit_message}

🤖 Generated with Gemini AI from Issue #{ISSUE_NUMBER}

Co-Authored-By: NextRoom Bot <bot@nextroom.app>"""

        subprocess.run(['git', 'commit', '-m', full_commit_message], check=True)
        print(f"✓ Created commit")

        # 푸시
        subprocess.run(['git', 'push', '-u', 'origin', branch_name], check=True)
        print(f"✓ Pushed to origin/{branch_name}")

        return True
    except subprocess.CalledProcessError as e:
        print(f"✗ Git operation failed: {e}")
        return False

def create_pull_request(branch_name, pr_title, pr_body):
    """Pull Request 생성"""
    try:
        g = Github(GITHUB_TOKEN)
        repo = g.get_repo(REPOSITORY)

        # PR 생성
        pr = repo.create_pull(
            title=pr_title,
            body=pr_body,
            head=branch_name,
            base='develop'
        )

        print(f"✓ Created PR: {pr.html_url}")
        return pr.html_url
    except Exception as e:
        print(f"✗ Failed to create PR: {e}")
        return None

def save_result(success, message, pr_url=None):
    """결과를 파일로 저장 (GitHub Actions에서 읽음)"""
    result_path = Path('/tmp/implementation_result.txt')

    if success:
        content = f"""✅ **자동 구현 완료!**

{message}

🔗 Pull Request: {pr_url}

---
*🤖 Powered by Gemini 2.0 Flash*
"""
    else:
        content = f"""❌ **자동 구현 실패**

{message}

자세한 내용은 [Actions 로그](https://github.com/{REPOSITORY}/actions)에서 확인하세요.
"""

    result_path.write_text(content, encoding='utf-8')

def main():
    """메인 실행 함수"""
    print("=" * 60)
    print("🤖 NextRoom Auto Implementation Bot")
    print("=" * 60)
    print(f"Issue #{ISSUE_NUMBER}: {ISSUE_TITLE}")
    print("=" * 60)

    try:
        # 1. 프로젝트 컨텍스트 읽기
        print("\n[1/6] Reading project context...")
        project_context = read_project_context()
        structure = get_codebase_structure()

        # 2. Gemini API로 분석 및 구현
        print("\n[2/6] Analyzing issue with Gemini API...")
        result = analyze_and_implement(ISSUE_TITLE, ISSUE_BODY, project_context, structure)

        print(f"\n📋 Analysis: {result['analysis']}")
        print(f"\n📝 Plan: {result['implementation_plan']}")
        print(f"\n📦 Files to change: {len(result['files'])}")

        # 3. 파일 변경사항 적용
        print("\n[3/6] Applying file changes...")
        changed_files = apply_file_changes(result['files'])

        if not changed_files:
            save_result(False, "변경할 파일이 없습니다.")
            sys.exit(1)

        # 4. 브랜치 생성 및 커밋
        print("\n[4/6] Creating branch and commit...")
        branch_name = result['branch_name']
        commit_message = result['commit_message']

        if not create_branch_and_commit(branch_name, commit_message, changed_files):
            save_result(False, "Git 작업 중 오류가 발생했습니다.")
            sys.exit(1)

        # 5. Pull Request 생성
        print("\n[5/6] Creating Pull Request...")
        pr_title = f"{ISSUE_TITLE} (#{ISSUE_NUMBER})"
        pr_body = f"""{result['pr_description']}

---

## 🤖 자동 구현 정보

- **Issue**: #{ISSUE_NUMBER}
- **Branch**: `{branch_name}`
- **Files Changed**: {len(changed_files)}

### 변경된 파일
{chr(10).join(f'- `{f}`' for f in changed_files)}

---

Closes #{ISSUE_NUMBER}
"""

        pr_url = create_pull_request(branch_name, pr_title, pr_body)

        if not pr_url:
            save_result(False, "PR 생성 중 오류가 발생했습니다.")
            sys.exit(1)

        # 6. 완료
        print("\n[6/6] Done!")
        print("=" * 60)

        save_result(
            True,
            f"**{len(changed_files)}개 파일**이 수정되었습니다.\n\n" +
            f"브랜치: `{branch_name}`",
            pr_url
        )

    except Exception as e:
        print(f"\n❌ Error: {e}")
        import traceback
        traceback.print_exc()
        save_result(False, f"오류 발생: {str(e)}")
        sys.exit(1)

if __name__ == '__main__':
    main()
