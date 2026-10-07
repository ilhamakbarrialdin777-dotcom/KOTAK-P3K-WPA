package com.example.data.model

data class BoxConditionCheck(
    val no: Int,
    val itemText: String,
    val isYes: Boolean = true,
    val note: String = ""
)
