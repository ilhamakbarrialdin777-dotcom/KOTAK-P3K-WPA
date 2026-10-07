package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BoxConditionCheck
import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionEntity
import com.example.data.model.InspectionItem
import com.example.data.repository.InspectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InspectionRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = InspectionRepository(db.inspectionDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    // List of all inspections from database
    val allInspections: StateFlow<List<InspectionEntity>> = repository.allInspections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search and filter for recap screen
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("SEMUA") // "SEMUA", "LENGKAP", "BELUM_LENGKAP"
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    val filteredInspections: StateFlow<List<InspectionEntity>> = combine(
        allInspections,
        searchQuery,
        statusFilter
    ) { list, query, filter ->
        list.filter { item ->
            val matchQuery = query.isBlank() ||
                item.siteLocation.contains(query, ignoreCase = true) ||
                item.inspectorName.contains(query, ignoreCase = true) ||
                item.periodMonthYear.contains(query, ignoreCase = true) ||
                item.inspectionDate.contains(query, ignoreCase = true)

            val matchFilter = when (filter) {
                "LENGKAP" -> item.conclusionStatus == "LENGKAP"
                "BELUM_LENGKAP" -> item.conclusionStatus == "BELUM_LENGKAP"
                else -> true
            }

            matchQuery && matchFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStatusFilter(filter: String) {
        _statusFilter.value = filter
    }

    // ----------------------------------------------------
    // Active Form State
    // ----------------------------------------------------
    var editingId = MutableStateFlow(0L)
        private set

    var siteLocation = MutableStateFlow("Workshop Utama")
        private set

    var boxPosition = MutableStateFlow("Di Tempel di dinding")
        private set

    var inspectionDate = MutableStateFlow(DefaultDataHelper.getCurrentDateString())
        private set

    var periodMonthYear = MutableStateFlow(DefaultDataHelper.getCurrentPeriodString())
        private set

    var inspectorName = MutableStateFlow("ILHAM AKBAR RIALDIN")
        private set

    var inspectorRole = MutableStateFlow("HSE Officer")
        private set

    var conditionChecks = MutableStateFlow<List<BoxConditionCheck>>(DefaultDataHelper.getDefaultConditionChecks())
        private set

    var items = MutableStateFlow<List<InspectionItem>>(DefaultDataHelper.getDefaultStandardItems())
        private set

    var conclusionStatus = MutableStateFlow("LENGKAP")
        private set

    var replacementNotes = MutableStateFlow("")
        private set

    var signatureData = MutableStateFlow("")
        private set

    // Active Wizard Tab (0 = Info Umum, 1 = Kondisi Kotak, 2 = Isi Kotak P3K, 3 = Kesimpulan & Paraf)
    var currentStep = MutableStateFlow(0)
        private set

    fun setCurrentStep(step: Int) {
        currentStep.value = step.coerceIn(0, 3)
    }

    fun startNewInspection(defaultSite: String = "") {
        editingId.value = 0L
        siteLocation.value = if (defaultSite.isNotBlank()) defaultSite else "Workshop Utama"
        boxPosition.value = "Di Tempel di dinding"
        inspectionDate.value = DefaultDataHelper.getCurrentDateString()
        periodMonthYear.value = DefaultDataHelper.getCurrentPeriodString()
        inspectorName.value = "ILHAM AKBAR RIALDIN"
        inspectorRole.value = "HSE Officer"
        conditionChecks.value = DefaultDataHelper.getDefaultConditionChecks()
        items.value = DefaultDataHelper.getDefaultStandardItems()
        conclusionStatus.value = "LENGKAP"
        replacementNotes.value = ""
        signatureData.value = ""
        currentStep.value = 0
    }

    fun loadInspectionForEdit(entity: InspectionEntity) {
        editingId.value = entity.id
        siteLocation.value = entity.siteLocation
        boxPosition.value = entity.boxPosition
        inspectionDate.value = entity.inspectionDate
        periodMonthYear.value = entity.periodMonthYear
        inspectorName.value = entity.inspectorName
        inspectorRole.value = entity.inspectorRole
        conditionChecks.value = entity.parseConditionChecks().ifEmpty { DefaultDataHelper.getDefaultConditionChecks() }
        items.value = entity.parseItems().ifEmpty { DefaultDataHelper.getDefaultStandardItems() }
        conclusionStatus.value = entity.conclusionStatus
        replacementNotes.value = entity.replacementNotes
        signatureData.value = entity.signatureData
        currentStep.value = 0
    }

    fun updateSiteLocation(value: String) { siteLocation.value = value }
    fun updateBoxPosition(value: String) { boxPosition.value = value }
    fun updateInspectionDate(value: String) { inspectionDate.value = value }
    fun updatePeriodMonthYear(value: String) { periodMonthYear.value = value }
    fun updateInspectorName(value: String) { inspectorName.value = value }
    fun updateInspectorRole(value: String) { inspectorRole.value = value }
    fun updateConclusionStatus(value: String) { conclusionStatus.value = value }
    fun updateReplacementNotes(value: String) { replacementNotes.value = value }
    fun updateSignatureData(value: String) { signatureData.value = value }

    // Section A: Condition Checklist
    fun setConditionIsYes(no: Int, isYes: Boolean) {
        conditionChecks.value = conditionChecks.value.map {
            if (it.no == no) it.copy(isYes = isYes) else it
        }
        recalculateConclusionRecommendation()
    }

    fun setConditionNote(no: Int, note: String) {
        conditionChecks.value = conditionChecks.value.map {
            if (it.no == no) it.copy(note = note) else it
        }
    }

    fun setAllConditionsYes() {
        conditionChecks.value = conditionChecks.value.map {
            it.copy(isYes = true)
        }
        recalculateConclusionRecommendation()
    }

    // Section B: Items
    fun updateItemCurrentQty(no: Int, qty: Int) {
        val safeQty = qty.coerceAtLeast(0)
        items.value = items.value.map {
            if (it.no == no) it.copy(currentQty = safeQty) else it
        }
        recalculateConclusionRecommendation()
    }

    fun incrementItemQty(no: Int) {
        items.value = items.value.map {
            if (it.no == no) it.copy(currentQty = it.currentQty + 1) else it
        }
        recalculateConclusionRecommendation()
    }

    fun decrementItemQty(no: Int) {
        items.value = items.value.map {
            if (it.no == no) it.copy(currentQty = (it.currentQty - 1).coerceAtLeast(0)) else it
        }
        recalculateConclusionRecommendation()
    }

    fun setItemCondition(no: Int, isGood: Boolean) {
        items.value = items.value.map {
            if (it.no == no) it.copy(conditionIsGood = isGood) else it
        }
        recalculateConclusionRecommendation()
    }

    fun updateItemExpiry(no: Int, dateStr: String) {
        items.value = items.value.map {
            if (it.no == no) it.copy(expiryDate = dateStr) else it
        }
    }

    fun updateItemNote(no: Int, note: String) {
        items.value = items.value.map {
            if (it.no == no) it.copy(note = note) else it
        }
    }

    fun setAllItemsStandardAndGood() {
        items.value = items.value.map {
            it.copy(currentQty = it.standardQty, conditionIsGood = true)
        }
        recalculateConclusionRecommendation()
    }

    fun addCustomItem(name: String, usage: String, standardQty: Int, unit: String) {
        val nextNo = (items.value.maxOfOrNull { it.no } ?: 19) + 1
        val newItem = InspectionItem(
            no = nextNo,
            name = name.ifBlank { "Barang Tambahan #$nextNo" },
            category = "6. BARANG LAIN (jika ada tambahan)",
            usage = usage,
            standardQty = standardQty.coerceAtLeast(1),
            unit = unit.ifBlank { "buah" },
            currentQty = standardQty.coerceAtLeast(1),
            conditionIsGood = true
        )
        items.value = items.value + newItem
    }

    fun deleteItem(no: Int) {
        items.value = items.value.filter { it.no != no }
        recalculateConclusionRecommendation()
    }

    // Auto calculate if status is complete or needs refill
    private fun recalculateConclusionRecommendation() {
        val allConditionsOk = conditionChecks.value.all { it.isYes }
        val allItemsFulfilled = items.value.all { it.isFulfilled }

        conclusionStatus.value = if (allConditionsOk && allItemsFulfilled) {
            "LENGKAP"
        } else {
            "BELUM_LENGKAP"
        }
    }

    fun autoGenerateNotesFromDeficits() {
        val deficits = mutableListOf<String>()

        // Check condition issues
        conditionChecks.value.filter { !it.isYes }.forEach {
            deficits.add("[Kondisi Kotak] ${it.itemText}")
        }

        // Check items
        items.value.forEach { item ->
            if (item.currentQty < item.standardQty) {
                val diff = item.standardQty - item.currentQty
                deficits.add("${item.name}: kurang $diff ${item.unit} (ada ${item.currentQty}/${item.standardQty})")
            }
            if (!item.conditionIsGood) {
                deficits.add("${item.name}: Kondisi RUSAK / perlu ganti")
            }
            if (item.expiryDate.isNotBlank()) {
                deficits.add("${item.name}: Exp: ${item.expiryDate}")
            }
        }

        replacementNotes.value = if (deficits.isEmpty()) {
            "Kotak P3K dalam kondisi prima, semua barang terisi sesuai standar dan layak pakai."
        } else {
            "Perlu pengisian ulang / perbaikan:\n• " + deficits.joinToString("\n• ")
        }
    }

    fun saveInspection(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val entity = InspectionEntity(
                id = editingId.value,
                siteLocation = siteLocation.value.ifBlank { "Lokasi Tidak Ditentukan" },
                boxPosition = boxPosition.value.ifBlank { "Di Tempel di dinding" },
                inspectionDate = inspectionDate.value,
                periodMonthYear = periodMonthYear.value,
                inspectorName = inspectorName.value.ifBlank { "ILHAM AKBAR RIALDIN" },
                inspectorRole = inspectorRole.value.ifBlank { "HSE Officer" },
                conditionChecksJson = InspectionEntity.encodeConditionChecks(conditionChecks.value),
                itemsJson = InspectionEntity.encodeItems(items.value),
                conclusionStatus = conclusionStatus.value,
                replacementNotes = replacementNotes.value,
                signatureData = signatureData.value
            )
            val savedId = repository.saveInspection(entity)
            onSuccess(savedId)
        }
    }

    fun deleteInspection(id: Long) {
        viewModelScope.launch {
            repository.deleteInspectionById(id)
        }
    }

    fun generateOfficialReportText(entity: InspectionEntity): String {
        val parsedConditions = entity.parseConditionChecks()
        val parsedItems = entity.parseItems()

        val sb = StringBuilder()
        sb.appendLine("========================================")
        sb.appendLine("  ${DefaultDataHelper.COMPANY_NAME}")
        sb.appendLine("  ${DefaultDataHelper.DEPARTMENT_NAME}")
        sb.appendLine("  ${DefaultDataHelper.FORM_TITLE}")
        sb.appendLine("========================================")
        sb.appendLine("Lokasi / Site     : ${entity.siteLocation}")
        sb.appendLine("Letak Kotak P3K   : ${entity.boxPosition}")
        sb.appendLine("Tanggal Periksa   : ${entity.inspectionDate}")
        sb.appendLine("Periode (Bln/Thn) : ${entity.periodMonthYear}")
        sb.appendLine("Pemeriksa         : ${entity.inspectorName} (${entity.inspectorRole})")
        sb.appendLine("----------------------------------------")
        sb.appendLine("A. KONDISI FISIK KOTAK P3K:")
        parsedConditions.forEach { c ->
            val status = if (c.isYes) "[✓] YA" else "[✗] TIDAK"
            val noteStr = if (c.note.isNotBlank()) " (Ket: ${c.note})" else ""
            sb.appendLine("${c.no}. ${c.itemText}: $status$noteStr")
        }
        sb.appendLine("----------------------------------------")
        sb.appendLine("B. PEMERIKSAAN ISI KOTAK P3K:")
        var currentCat = ""
        parsedItems.forEach { it ->
            if (it.category != currentCat) {
                currentCat = it.category
                sb.appendLine("\n[$currentCat]")
            }
            val condStr = if (it.conditionIsGood) "Baik" else "RUSAK"
            val expStr = if (it.expiryDate.isNotBlank()) " | Exp: ${it.expiryDate}" else ""
            val noteStr = if (it.note.isNotBlank()) " | Ket: ${it.note}" else ""
            val deficitMark = if (it.currentQty < it.standardQty) " [KURANG]" else ""
            sb.appendLine("${it.no}. ${it.name}: Jml Saat Ini ${it.currentQty}/${it.standardQty} ${it.unit} ($condStr)$deficitMark$expStr$noteStr")
        }
        sb.appendLine("----------------------------------------")
        sb.appendLine("KESIMPULAN PEMERIKSAAN:")
        val statusText = if (entity.conclusionStatus == "LENGKAP") {
            "[✓] KOTAK P3K LENGKAP DAN LAYAK PAKAI"
        } else {
            "[✗] KOTAK P3K BELUM LENGKAP (Perlu Pengisian Ulang / Perbaikan)"
        }
        sb.appendLine("Status: $statusText")
        if (entity.replacementNotes.isNotBlank()) {
            sb.appendLine("Catatan / Tindak Lanjut:\n${entity.replacementNotes}")
        }
        sb.appendLine("\nDiperiksa Oleh: ${entity.inspectorName} (${entity.inspectorRole})")
        sb.appendLine("Paraf: ${if (entity.signatureData.isNotBlank()) "Telah Ditandatangani Secara Digital" else "Belum Ada Paraf"}")
        sb.appendLine("----------------------------------------")
        sb.appendLine("Sesuai ${DefaultDataHelper.REGULATION_REF}")
        sb.appendLine("========================================")
        return sb.toString()
    }
}
