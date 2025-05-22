package com.example.shared.model

import java.util.Locale.Category

data class TimerOption(
    val id : Int = -1,
    val name : String = "",
    val category: String = "",
    val description: String = "",
    val workTime : Int = -1,
    val restTime : Int = -1,
)
object TimerOptions{
    val option = listOf(
        TimerOption(
            id = 1,
            name = "10/2 Rule",
            category = "암기",
            description = "짧은 집중과 미니 휴식의 반복으로 지속적인 집중력 유지",
            workTime = 10,
            restTime = 2
        ),
        TimerOption(
            id = 2,
            name = "포모도로 기법",
            category = "암기",
            description = "25분 집중 후 5분 휴식, 4회 반복 후 긴 휴식(15-30분)",
            workTime = 25,
            restTime = 5
        ),
        TimerOption(
            id = 3,
            name = "Ultradian Rhythm",
            description = "인체 생체리듬에 맞춘 90-120분 집중과 20-30분 휴식",
            workTime = 90,
            restTime = 20
        ),
        TimerOption(
            id = 4,
            name = "52/17 Rule",
            description = "통계 기반의 최적 집중/휴식 비율로 52분 집중 후 17분 완전한 휴식",
            workTime = 52,
            restTime = 17
        ),
        TimerOption(
            id = 5,
            name = "Time Blocking",
            description = "하루 일정을 미리 시간 블록으로 나누어 계획하는 방식(가변적)",
            workTime = 60,
            restTime = 10
        ),
        TimerOption(
            id = 6,
            name = "90/30 Rule",
            description = "90분 집중 작업 후 30분 충분한 휴식으로 깊은 집중 상태 유도",
            workTime = 90,
            restTime = 30
        ),
        TimerOption(
            id = 7,
            name = "2시간 집중법",
            description = "2시간 동안 방해 요소 차단하고 한 가지 작업에 집중",
            workTime = 120,
            restTime = 30
        ),
        TimerOption(
            id = 8,
            name = "Flowtime 기법",
            description = "자연스러운 집중 흐름을 존중하며 필요할 때 휴식(가변적)",
            workTime = 45,
            restTime = 15
        ),
        TimerOption(
            id = 9,
            name = "Timeboxing",
            description = "각 작업에 정해진 시간을 배정하고 그 안에 완료하는 방식(가변적)",
            workTime = 45,
            restTime = 10
        )
    )
}