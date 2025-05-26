package com.example.shared.model

data class subject(
    val id: Int = -1,
    val name: String = "",
    val backgroundColor: Long = 0xFFFFFFFF,
    val recomendTimer : Int = -1,
    val selectedTimer : Int = -1,
)

fun subject.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "name" to name,
        "backgroundColor" to backgroundColor,
        "recomendTimer" to recomendTimer,
        "selectedTimer" to selectedTimer,
    )
}