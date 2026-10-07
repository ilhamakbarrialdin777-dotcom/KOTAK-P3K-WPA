package com.example

import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class InspectionDataUnitTest {

    @Test
    fun testDefaultStandardItemsCount() {
        val items = DefaultDataHelper.getDefaultStandardItems()
        assertEquals(19, items.size)
        // Check first item: Plester gulung, 2 roll
        assertEquals("Plester gulung", items[0].name)
        assertEquals(2, items[0].standardQty)
        assertEquals("roll", items[0].unit)
        assertTrue(items[0].isFulfilled)
    }

    @Test
    fun testDefaultConditionChecksCount() {
        val conditions = DefaultDataHelper.getDefaultConditionChecks()
        assertEquals(5, conditions.size)
        assertTrue(conditions.all { it.isYes })
    }

    @Test
    fun testInspectionEntityJsonEncodingDecoding() {
        val stdConditions = DefaultDataHelper.getDefaultConditionChecks()
        val stdItems = DefaultDataHelper.getDefaultStandardItems()

        val encodedConditions = InspectionEntity.encodeConditionChecks(stdConditions)
        val encodedItems = InspectionEntity.encodeItems(stdItems)

        val entity = InspectionEntity(
            id = 1,
            siteLocation = "Workshop Site",
            boxPosition = "Di Tempel di dinding",
            inspectionDate = "07/10/2026",
            periodMonthYear = "Oktober 2026",
            inspectorName = "ILHAM AKBAR RIALDIN",
            inspectorRole = "HSE Officer",
            conditionChecksJson = encodedConditions,
            itemsJson = encodedItems,
            conclusionStatus = "LENGKAP",
            replacementNotes = "Semua lengkap"
        )

        val parsedConditions = entity.parseConditionChecks()
        val parsedItems = entity.parseItems()

        assertEquals(5, parsedConditions.size)
        assertEquals(19, parsedItems.size)
        assertEquals("Plester gulung", parsedItems[0].name)
        assertEquals("Workshop Site", entity.siteLocation)
    }

    @Test
    fun testDeficitItemCalculation() {
        val item = DefaultDataHelper.getDefaultStandardItems()[0] // 2 roll
        val itemDeficit = item.copy(currentQty = 1)
        assertTrue(itemDeficit.hasDeficit)
        assertFalse(itemDeficit.isFulfilled)

        val itemDamaged = item.copy(conditionIsGood = false)
        assertTrue(itemDamaged.isDamaged)
        assertFalse(itemDamaged.isFulfilled)
    }
}
