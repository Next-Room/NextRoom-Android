#!/usr/bin/env python3
"""
AI Code Review Script using Gemini API
Analyzes PR changes and posts review comments
"""

import os
import sys
from typing import List, Dict
import google.generativeai as genai
from github import Github, PullRequest


def get_project_context() -> str:
    """Read CLAUDE.md for project context"""
    claude_md_path = "CLAUDE.md"

    try:
        with open(claude_md_path, 'r', encoding='utf-8') as f:
            return f.read()
    except FileNotFoundError:
        print("Warning: CLAUDE.md not found, proceeding without project context")
        return ""


def get_pr_diff(pr: PullRequest.PullRequest) -> str:
    """Get the full diff of the PR"""
    files = pr.get_files()
    diff_text = ""

    for file in files:
        diff_text += f"\n\n{'='*80}\n"
        diff_text += f"File: {file.filename}\n"
        diff_text += f"Status: {file.status}\n"
        diff_text += f"Changes: +{file.additions} -{file.deletions}\n"
        diff_text += f"{'='*80}\n"

        if file.patch:
            diff_text += file.patch
        else:
            diff_text += "(Binary file or no diff available)\n"

    return diff_text


def create_review_prompt(project_context: str, pr_title: str, pr_body: str, diff: str) -> str:
    """Create a comprehensive review prompt for Gemini"""

    prompt = f"""You are an expert Android developer conducting a thorough code review for a Pull Request.

# Project Context
{project_context}

# Pull Request Information
**Title:** {pr_title}
**Description:** {pr_body or "(No description provided)"}

# Review Guidelines
Please review the following code changes with focus on:

1. **Code Quality & Best Practices**
   - Kotlin coding conventions and idioms
   - Android development best practices
   - Clean Architecture principles adherence
   - Proper use of Kotlin coroutines and Flow
   - View binding patterns
   - Orbit MVI state management patterns

2. **Potential Bugs & Issues**
   - Null safety violations
   - Memory leaks (especially with coroutines, fragments, views)
   - Thread safety issues
   - Resource leaks
   - Edge cases not handled

3. **Project Guidelines Compliance**
   - Module architecture (domain/data/presentation separation)
   - Navigation using safeNavigate() instead of navigate()
   - Proper use of Result wrapper for network calls
   - Repository pattern implementation
   - Hilt dependency injection patterns
   - Proper error handling with Orbit

4. **Performance Optimization**
   - Unnecessary object allocations
   - Inefficient data structures or algorithms
   - UI performance issues (overdraw, layout complexity)
   - Database query optimization
   - Network call efficiency

# Code Changes
{diff}

# Instructions for Review
- Provide specific, actionable feedback
- Reference file names and line numbers when possible
- Categorize issues by severity: 🔴 Critical, 🟡 Warning, 💡 Suggestion
- Highlight positive aspects of the code when applicable
- If no issues found, provide a brief summary of what was reviewed
- Use Korean language for the review comments
- Format your response in markdown

Please provide a comprehensive code review:
"""

    return prompt


def get_gemini_review(prompt: str, api_key: str) -> str:
    """Call Gemini API to get code review"""

    genai.configure(api_key=api_key)

    # Using Gemini 3 Pro
    model = genai.GenerativeModel('gemini-3-pro')

    try:
        response = model.generate_content(
            prompt,
            generation_config=genai.types.GenerationConfig(
                temperature=0.3,  # Lower temperature for more focused reviews
                top_p=0.95,
                top_k=40,
                max_output_tokens=8192,
            )
        )

        return response.text

    except Exception as e:
        print(f"Error calling Gemini API: {e}")
        raise


def post_review_comment(pr: PullRequest.PullRequest, review_text: str):
    """Post the review as a PR comment"""

    comment_body = f"""## 🤖 AI Code Review (Gemini)

{review_text}

---
*This review was automatically generated using Gemini AI. Please use your judgment when addressing the feedback.*
"""

    pr.create_issue_comment(comment_body)
    print("✅ Review comment posted successfully")


def main():
    """Main execution function"""

    # Get environment variables
    github_token = os.getenv('GITHUB_TOKEN')
    gemini_api_key = os.getenv('GEMINI_API_KEY')
    pr_number = os.getenv('PR_NUMBER')
    repo_name = os.getenv('REPO_NAME')

    # Validate required environment variables
    if not all([github_token, gemini_api_key, pr_number, repo_name]):
        print("❌ Error: Missing required environment variables")
        print(f"GITHUB_TOKEN: {'✓' if github_token else '✗'}")
        print(f"GEMINI_API_KEY: {'✓' if gemini_api_key else '✗'}")
        print(f"PR_NUMBER: {'✓' if pr_number else '✗'}")
        print(f"REPO_NAME: {'✓' if repo_name else '✗'}")
        sys.exit(1)

    print(f"🔍 Analyzing PR #{pr_number} in {repo_name}")

    # Initialize GitHub client
    g = Github(github_token)
    repo = g.get_repo(repo_name)
    pr = repo.get_pull(int(pr_number))

    print(f"📝 PR Title: {pr.title}")
    print(f"📊 Files changed: {pr.changed_files}")
    print(f"➕ Additions: {pr.additions}")
    print(f"➖ Deletions: {pr.deletions}")

    # Get project context and PR diff
    print("\n📖 Reading project context...")
    project_context = get_project_context()

    print("📥 Fetching PR diff...")
    diff = get_pr_diff(pr)

    # Check if diff is too large
    if len(diff) > 100000:  # ~100KB limit
        print("⚠️  Warning: Diff is very large, truncating...")
        diff = diff[:100000] + "\n\n... (diff truncated due to size)"

    # Create review prompt
    print("✨ Creating review prompt...")
    prompt = create_review_prompt(
        project_context=project_context,
        pr_title=pr.title,
        pr_body=pr.body,
        diff=diff
    )

    # Get AI review
    print("🤖 Calling Gemini API for code review...")
    review_text = get_gemini_review(prompt, gemini_api_key)

    print("\n" + "="*80)
    print("REVIEW RESULT:")
    print("="*80)
    print(review_text)
    print("="*80 + "\n")

    # Post review comment
    print("💬 Posting review comment to PR...")
    post_review_comment(pr, review_text)

    print("\n✅ Code review completed successfully!")


if __name__ == "__main__":
    main()
