package com.example.shared.model

object TimerOptions{
    val list = listOf(
        // === 암기 카테고리 (짧은 집중) ===
        TimerOption(
            id = 0,
            name = "5/1 Rule",
            category = "암기",
            description = "매우 짧은 집중으로 단어, 용어 암기에 최적화",
            workTime = 5 * 60,   // 300초
            restTime = 1 * 60    // 60초
        ),
        TimerOption(
            id = 1,
            name = "10/2 Rule",
            category = "암기",
            description = "짧은 집중과 미니 휴식의 반복으로 지속적인 집중력 유지",
            workTime = 10 * 60,  // 600초
            restTime = 2 * 60    // 120초
        ),
        TimerOption(
            id = 2,
            name = "15/3 Rule",
            category = "암기",
            description = "공식이나 개념 암기를 위한 적당한 길이의 집중 사이클",
            workTime = 15 * 60,  // 900초
            restTime = 3 * 60    // 180초
        ),
        TimerOption(
            id = 3,
            name = "20/5 Rule",
            category = "암기",
            description = "긴 텍스트나 복잡한 내용 암기를 위한 확장된 집중",
            workTime = 20 * 60,  // 1200초
            restTime = 5 * 60    // 300초
        ),
        TimerOption(
            id = 4,
            name = "12/3 Rule",
            category = "암기",
            description = "시험 직전 집중 암기를 위한 최적화된 사이클",
            workTime = 12 * 60,  // 720초
            restTime = 3 * 60    // 180초
        ),
        TimerOption(
            id = 5,
            name = "8/2 Rule",
            category = "암기",
            description = "빠른 반복 암기로 단기 기억을 장기 기억으로 전환",
            workTime = 8 * 60,   // 480초
            restTime = 2 * 60    // 120초
        ),
        TimerOption(
            id = 6,
            name = "간격 반복법",
            category = "암기",
            description = "과학적 망각 곡선 기반 7분 집중 후 3분 복습",
            workTime = 7 * 60,   // 420초
            restTime = 3 * 60    // 180초
        ),

        // === 이해 카테고리 (중간 집중) ===
        TimerOption(
            id = 7,
            name = "포모도로 기법",
            category = "이해",
            description = "25분 집중 후 5분 휴식, 4회 반복 후 긴 휴식(15-30분)",
            workTime = 25 * 60,  // 1500초
            restTime = 5 * 60    // 300초
        ),
        TimerOption(
            id = 8,
            name = "30/8 Rule",
            category = "이해",
            description = "개념 이해와 문제 풀이를 위한 균형잡힌 학습 사이클",
            workTime = 30 * 60,  // 1800초
            restTime = 8 * 60    // 480초
        ),
        TimerOption(
            id = 9,
            name = "35/10 Rule",
            category = "이해",
            description = "강의 시청이나 교재 읽기에 적합한 집중 시간",
            workTime = 35 * 60,  // 2100초
            restTime = 10 * 60   // 600초
        ),
        TimerOption(
            id = 10,
            name = "40/15 Rule",
            category = "이해",
            description = "복잡한 개념 학습과 충분한 휴식의 조화",
            workTime = 40 * 60,  // 2400초
            restTime = 15 * 60   // 900초
        ),
        TimerOption(
            id = 11,
            name = "52/17 Rule",
            category = "이해",
            description = "통계 기반의 최적 집중/휴식 비율로 52분 집중 후 17분 완전한 휴식",
            workTime = 52 * 60,  // 3120초
            restTime = 17 * 60   // 1020초
        ),
        TimerOption(
            id = 12,
            name = "Flowtime 기법",
            category = "이해",
            description = "자연스러운 집중 흐름을 존중하며 필요할 때 휴식(가변적)",
            workTime = 45 * 60,  // 2700초
            restTime = 15 * 60   // 900초
        ),
        TimerOption(
            id = 13,
            name = "Time Blocking",
            category = "이해",
            description = "하루 일정을 미리 시간 블록으로 나누어 계획하는 방식(가변적)",
            workTime = 60 * 60,  // 3600초
            restTime = 10 * 60   // 600초
        ),

        // === 논리 카테고리 (긴 집중) ===
        TimerOption(
            id = 14,
            name = "75/25 Rule",
            category = "논리",
            description = "논리적 사고가 필요한 문제 해결을 위한 집중 시간",
            workTime = 75 * 60,  // 4500초
            restTime = 25 * 60   // 1500초
        ),
        TimerOption(
            id = 15,
            name = "Ultradian Rhythm",
            category = "논리",
            description = "인체 생체리듬에 맞춘 90-120분 집중과 20-30분 휴식",
            workTime = 90 * 60,  // 5400초
            restTime = 20 * 60   // 1200초
        ),
        TimerOption(
            id = 16,
            name = "90/30 Rule",
            category = "논리",
            description = "90분 집중 작업 후 30분 충분한 휴식으로 깊은 집중 상태 유도",
            workTime = 90 * 60,  // 5400초
            restTime = 30 * 60   // 1800초
        ),
        TimerOption(
            id = 17,
            name = "100/20 Rule",
            category = "논리",
            description = "복잡한 알고리즘이나 수학 문제 해결을 위한 긴 집중",
            workTime = 100 * 60, // 6000초
            restTime = 20 * 60   // 1200초
        ),
        TimerOption(
            id = 18,
            name = "2시간 집중법",
            category = "논리",
            description = "2시간 동안 방해 요소 차단하고 한 가지 작업에 집중",
            workTime = 120 * 60, // 7200초
            restTime = 30 * 60   // 1800초
        ),
        TimerOption(
            id = 19,
            name = "딥워크 세션",
            category = "논리",
            description = "3시간 연속 깊은 사고가 필요한 프로젝트나 연구 작업",
            workTime = 180 * 60, // 10800초
            restTime = 45 * 60   // 2700초
        ),
        TimerOption(
            id = 20,
            name = "마라톤 집중법",
            category = "논리",
            description = "4시간 장시간 몰입으로 대규모 프로젝트 완성",
            workTime = 240 * 60, // 14400초
            restTime = 60 * 60   // 3600초
        )
    )

}