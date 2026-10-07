package com.example.data.model

data class InspectionItem(
    val no: Int,
    val name: String,
    val category: String,
    val usage: String,
    val standardQty: Int,
    val unit: String,
    val currentQty: Int = standardQty,
    val conditionIsGood: Boolean = true,
    val expiryDate: String = "",
    val note: String = ""
) {
    val isFulfilled: Boolean
        get() = currentQty >= standardQty && conditionIsGood

    val hasDeficit: Boolean
        get() = currentQty < standardQty

    val isDamaged: Boolean
        get() = !conditionIsGood
}
