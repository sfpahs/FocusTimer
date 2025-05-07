package com.example.shared.watchModel

class CronoTimeSchedule {
    companion object{
        val definiteMorningType =
            CronoTime(
                productive1 =  time(6f, 13f), 
                creative =     time(9f, 12f), 
                workout1 =   time(7f, 9f), 
                sleep =     time(21f, 22f), 
                wakeup =    time(5f, 6f), 
                description = "- 중요 업무/의사결정 오전 집중\n" +
                        "- 오후 3시 이후 덜 중요한 업무\n" +
                        "- 저녁 7시 이후 블루라이트 노출 제한\n" +
                        "- 일찍 저녁 식사 (18:00-19:00)"
            )
        val moderateMorningType =
            CronoTime(
                productive1 =  time(8f, 14f), 
                creative =     time(10f, 13f), 
                workout1 =  time(7f, 10f), 
                workout2 =  time(17f, 18f), 
                sleep =     time(22f, 23f), 
                wakeup =    time(6f, 7f), 
                description = "- 중요 회의/업무 오전 배치\n" +
                        "- 오후는 협업과 소통에 활용\n" +
                        "- 저녁 8시 이후 카페인 섭취 제한"
            )
        val intermediateType =
            CronoTime(
                productive1 =  time(9f, 12f), 
                productive2 = time(15f, 18f), 
                workout1 =  time(8f, 10f), 
                workout2 =  time(17f, 19f), 
                sleep =     time(22.5f, 23.5f),
                wakeup =    time(6.5f, 7.5f),
                description = "- 일일 에너지 패턴 모니터링\n" +
                        "- 규칙적인 수면-기상 시간 유지\n" +
                        "- 다양한 활동 시간 실험"
            )
        val moderateEveningType =
            CronoTime(
                productive1 =  time(10f, 13f),
                productive2 = time(16f, 20f),
                creative =     time(18f, 22f),
                workout1 =   time(12f, 14f),
                workout2 =  time(18f, 20f),
                sleep =     time(23.5f, 0.5f),
                wakeup =    time(7.5f, 8.5f),
                description = "- 오전 회의 최소화\n" +
                        "- 중요 의사결정 오후/저녁 배치\n" +
                        "- 아침 루틴에 충분한 시간 할당\n" +
                        "- 자연광 노출로 아침 각성 촉진"
            )
        val definiteEveningType =
            CronoTime(
                productive1 =  time(14f, 117f),
                productive2 = time(20f, 24f),
                creative =     time(20f, 1f),
                workout1 =   time(16f, 20f),
                sleep =     time(0.5f, 2f),
                wakeup =    time(8.5f, 10f),
                description = "- 유연한 근무 시간 활용\n" +
                        "- 아침 일정 최소화, 오후 중요 업무\n" +
                        "- 창의적 프로젝트 저녁/밤 집중\n" +
                        "- 주말에도 일관된 수면-기상 패턴 유지"
            )
    }
}