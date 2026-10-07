package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "inspections")
data class InspectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val siteLocation: String,
    val boxPosition: String = "Di Tempel di dinding",
    val inspectionDate: String,
    val periodMonthYear: String,
    val inspectorName: String = "ILHAM AKBAR RIALDIN",
    val inspectorRole: String = "HSE Officer",
    val conditionChecksJson: String,
    val itemsJson: String,
    val conclusionStatus: String, // "LENGKAP" or "BELUM_LENGKAP"
    val replacementNotes: String = "",
    val signatureData: String = "", // signature vector points string
    val createdAt: Long = System.currentTimeMillis()
) {
    fun parseConditionChecks(): List<BoxConditionCheck> {
        val result = mutableListOf<BoxConditionCheck>()
        if (conditionChecksJson.isBlank()) return result
        try {
            val array = JSONArray(conditionChecksJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    BoxConditionCheck(
                        no = obj.optInt("no", i + 1),
                        itemText = obj.optString("itemText", ""),
                        isYes = obj.optBoolean("isYes", true),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return result
    }

    fun parseItems(): List<InspectionItem> {
        val result = mutableListOf<InspectionItem>()
        if (itemsJson.isBlank()) return result
        try {
            val array = JSONArray(itemsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                result.add(
                    InspectionItem(
                        no = obj.optInt("no", i + 1),
                        name = obj.optString("name", ""),
                        category = obj.optString("category", ""),
                        usage = obj.optString("usage", ""),
                        standardQty = obj.optInt("standardQty", 1),
                        unit = obj.optString("unit", "buah"),
                        currentQty = obj.optInt("currentQty", 1),
                        conditionIsGood = obj.optBoolean("conditionIsGood", true),
                        expiryDate = obj.optString("expiryDate", ""),
                        note = obj.optString("note", "")
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }
        return result
    }

    companion object {
        fun encodeConditionChecks(list: List<BoxConditionCheck>): String {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("no", item.no)
                obj.put("itemText", item.itemText)
                obj.put("isYes", item.isYes)
                obj.put("note", item.note)
                array.put(obj)
            }
            return array.toString()
        }

        fun encodeItems(list: List<InspectionItem>): String {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("no", item.no)
                obj.put("name", item.name)
                obj.put("category", item.category)
                obj.put("usage", item.usage)
                obj.put("standardQty", item.standardQty)
                obj.put("unit", item.unit)
                obj.put("currentQty", item.currentQty)
                obj.put("conditionIsGood", item.conditionIsGood)
                obj.put("expiryDate", item.expiryDate)
                obj.put("note", item.note)
                array.put(obj)
            }
            return array.toString()
        }
    }
}
