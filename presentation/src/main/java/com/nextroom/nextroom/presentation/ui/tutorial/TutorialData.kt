package com.nextroom.nextroom.presentation.ui.tutorial

import java.io.Serializable

object TutorialData {
    const val DEFAULT_TIME_LIMIT_SECONDS = 3600 // 1시간

    val hints = listOf(
        TutorialHint(
            id = 1,
            code = "1111",
            progress = 33,
            hint = "벽에 걸린 시계를 자세히 살펴보세요. 시계 바늘이 가리키는 숫자들의 합이 중요한 단서입니다.",
            answer = "시계의 시침은 3, 분침은 9를 가리키고 있습니다. 3+9=12가 첫 번째 비밀번호입니다."
        ),
        TutorialHint(
            id = 2,
            code = "2222",
            progress = 66,
            hint = "책장에 있는 책들 중 색상이 다른 책이 하나 있습니다. 그 책의 페이지 번호를 확인하세요.",
            answer = "빨간색 책의 27페이지에 금고 비밀번호 '4821'이 적혀있습니다. 금고를 열어 열쇠를 획득하세요."
        ),
        TutorialHint(
            id = 3,
            code = "3333",
            progress = 100,
            hint = "마지막 문을 열기 위해서는 방 안의 모든 거울에 비친 글자들을 순서대로 읽어야 합니다.",
            answer = "거울에 비친 글자는 'ESCAPE'입니다. 문 앞의 키패드에 이 단어를 입력하면 탈출에 성공합니다!"
        )
    )

    // 랜덤한 힌트를 리턴한다.
    fun getRandomHint(code: String): TutorialHint? {
        return if (code.length == 4 && code.all { it.isDigit() }) {
            val index = code.toInt() % hints.size
            hints[index]
        } else {
            null
        }
    }
}

data class TutorialHint(
    val id: Int,
    val code: String,
    val progress: Int,
    val hint: String,
    val answer: String,
    val hintImageUrlList: List<String> = emptyList(),
    val answerImageUrlList: List<String> = emptyList()
) : Serializable
