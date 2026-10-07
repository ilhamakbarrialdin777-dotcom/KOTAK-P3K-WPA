package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BoxConditionCheck
import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionItem
import com.example.ui.components.SignaturePad
import com.example.ui.viewmodel.InspectionViewModel

@Composable
fun InspectionFormScreen(
    viewModel: InspectionViewModel,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val currentStep by viewModel.currentStep.collectAsState()
    val editingId by viewModel.editingId.collectAsState()

    val siteLocation by viewModel.siteLocation.collectAsState()
    val boxPosition by viewModel.boxPosition.collectAsState()
    val inspectionDate by viewModel.inspectionDate.collectAsState()
    val periodMonthYear by viewModel.periodMonthYear.collectAsState()
    val inspectorName by viewModel.inspectorName.collectAsState()
    val inspectorRole by viewModel.inspectorRole.collectAsState()

    val conditionChecks by viewModel.conditionChecks.collectAsState()
    val items by viewModel.items.collectAsState()
    val conclusionStatus by viewModel.conclusionStatus.collectAsState()
    val replacementNotes by viewModel.replacementNotes.collectAsState()
    val signatureData by viewModel.signatureData.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Form Wizard Step Bar
        val stepTitles = listOf("1. Info Site", "2. Kondisi Kotak", "3. Isi Kotak P3K", "4. Kesimpulan & Paraf")
        ScrollableTabRow(
            selectedTabIndex = currentStep,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            stepTitles.forEachIndexed { index, title ->
                Tab(
                    selected = currentStep == index,
                    onClick = { viewModel.setCurrentStep(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (currentStep == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("form_tab_$index")
                )
            }
        }

        // Active Content Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentStep) {
                0 -> StepInfoScreen(
                    siteLocation = siteLocation,
                    boxPosition = boxPosition,
                    inspectionDate = inspectionDate,
                    periodMonthYear = periodMonthYear,
                    inspectorName = inspectorName,
                    inspectorRole = inspectorRole,
                    isEditing = editingId != 0L,
                    onUpdateSite = viewModel::updateSiteLocation,
                    onUpdateBoxPos = viewModel::updateBoxPosition,
                    onUpdateDate = viewModel::updateInspectionDate,
                    onUpdatePeriod = viewModel::updatePeriodMonthYear,
                    onUpdateInspector = viewModel::updateInspectorName,
                    onUpdateRole = viewModel::updateInspectorRole
                )
                1 -> StepConditionScreen(
                    conditions = conditionChecks,
                    onToggleYes = viewModel::setConditionIsYes,
                    onUpdateNote = viewModel::setConditionNote,
                    onSetAllYes = viewModel::setAllConditionsYes
                )
                2 -> StepItemsScreen(
                    items = items,
                    onIncrement = viewModel::incrementItemQty,
                    onDecrement = viewModel::decrementItemQty,
                    onToggleGood = viewModel::setItemCondition,
                    onUpdateQty = viewModel::updateItemCurrentQty,
                    onUpdateExpiry = viewModel::updateItemExpiry,
                    onUpdateNote = viewModel::updateItemNote,
                    onDeleteCustomItem = viewModel::deleteItem,
                    onSetAllStandard = viewModel::setAllItemsStandardAndGood,
                    onOpenAddDialog = { showAddItemDialog = true }
                )
                3 -> StepConclusionScreen(
                    conclusionStatus = conclusionStatus,
                    replacementNotes = replacementNotes,
                    signatureData = signatureData,
                    inspectorName = inspectorName,
                    inspectorRole = inspectorRole,
                    onSelectConclusion = viewModel::updateConclusionStatus,
                    onUpdateNotes = viewModel::updateReplacementNotes,
                    onSignatureChanged = viewModel::updateSignatureData,
                    onAutoGenerateNotes = {
                        viewModel.autoGenerateNotesFromDeficits()
                        Toast.makeText(context, "Catatan barang kurang berhasil dirangkum otomatis!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Bottom Navigation Action Bar
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { viewModel.setCurrentStep(currentStep - 1) },
                        modifier = Modifier.testTag("prev_step_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sebelumnya")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (currentStep < 3) {
                    Button(
                        onClick = { viewModel.setCurrentStep(currentStep + 1) },
                        modifier = Modifier.testTag("next_step_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Selanjutnya")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.saveInspection {
                                Toast.makeText(context, "Formulir Pemeriksaan P3K Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                                onSaveSuccess()
                            }
                        },
                        modifier = Modifier.testTag("save_inspection_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (editingId == 0L) "Simpan Pemeriksaan" else "Perbarui Pemeriksaan")
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddCustomItemDialog(
            onDismiss = { showAddItemDialog = false },
            onAdd = { name, usage, qty, unit ->
                viewModel.addCustomItem(name, usage, qty, unit)
                showAddItemDialog = false
                Toast.makeText(context, "Barang tambahan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ----------------------------------------------------------------------------
// STEP 0: Informasi Umum / Site
// ----------------------------------------------------------------------------
@Composable
private fun StepInfoScreen(
    siteLocation: String,
    boxPosition: String,
    inspectionDate: String,
    periodMonthYear: String,
    inspectorName: String,
    inspectorRole: String,
    isEditing: Boolean,
    onUpdateSite: (String) -> Unit,
    onUpdateBoxPos: (String) -> Unit,
    onUpdateDate: (String) -> Unit,
    onUpdatePeriod: (String) -> Unit,
    onUpdateInspector: (String) -> Unit,
    onUpdateRole: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = DefaultDataHelper.COMPANY_NAME,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = DefaultDataHelper.DEPARTMENT_NAME,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Informasi Lokasi & Pemeriksa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
        }

        item {
            OutlinedTextField(
                value = siteLocation,
                onValueChange = onUpdateSite,
                label = { Text("Lokasi / Site Kotak P3K") },
                placeholder = { Text("Contoh: Workshop Mekanikal, Kantor Site, Gudang B") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_site_location")
            )
        }

        // Quick Site Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val suggestions = listOf("Workshop Utama", "Kantor Site", "Gudang Logistik", "Area Tambang")
                suggestions.forEach { suggestion ->
                    FilterChip(
                        selected = siteLocation == suggestion,
                        onClick = { onUpdateSite(suggestion) },
                        label = { Text(suggestion, fontSize = 11.sp) },
                        modifier = Modifier.testTag("chip_site_$suggestion")
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = boxPosition,
                onValueChange = onUpdateBoxPos,
                label = { Text("Letak Kotak P3K") },
                placeholder = { Text("Di Tempel di dinding") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_box_position")
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = inspectionDate,
                    onValueChange = onUpdateDate,
                    label = { Text("Tanggal Periksa") },
                    placeholder = { Text("DD/MM/YYYY") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_inspection_date")
                )
                OutlinedTextField(
                    value = periodMonthYear,
                    onValueChange = onUpdatePeriod,
                    label = { Text("Periode (Bln/Thn)") },
                    placeholder = { Text("Oktober 2026") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_period_month")
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = inspectorName,
                    onValueChange = onUpdateInspector,
                    label = { Text("Nama Pemeriksa") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("input_inspector_name")
                )
                OutlinedTextField(
                    value = inspectorRole,
                    onValueChange = onUpdateRole,
                    label = { Text("Jabatan") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(0.8f)
                        .testTag("input_inspector_role")
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F1)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Text(
                    text = "💡 Petunjuk: Periksa kotak P3K minimal 1 (satu) kali setiap bulan, atau segera setelah digunakan saat penanganan luka di tempat kerja.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 1: Kondisi Fisik Kotak P3K (Bagian A)
// ----------------------------------------------------------------------------
@Composable
private fun StepConditionScreen(
    conditions: List<BoxConditionCheck>,
    onToggleYes: (Int, Boolean) -> Unit,
    onUpdateNote: (Int, String) -> Unit,
    onSetAllYes: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "A. Kondisi Kotak P3K",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Text(
                        text = "Periksa kelayakan fisik kotak penyimpanan",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Quick Shortcut Button: "Set Semua Ya"
                OutlinedButton(
                    onClick = onSetAllYes,
                    modifier = Modifier.testTag("set_all_conditions_yes_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20))
                ) {
                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Semua Ya", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(conditions, key = { it.no }) { cond ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("condition_card_${cond.no}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (cond.isYes) Color(0xFFF7FBF7) else Color(0xFFFFF7F7)
                ),
                border = BorderStroke(
                    1.dp,
                    if (cond.isYes) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (cond.isYes) Color(0xFF1B5E20) else Color(0xFFBA1A1A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${cond.no}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = cond.itemText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Yes / No Segmented Choice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onToggleYes(cond.no, true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (cond.isYes) Color(0xFF1B5E20) else Color(0xFFEEEEEE),
                                contentColor = if (cond.isYes) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("condition_${cond.no}_yes")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("YA (Sesuai)", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onToggleYes(cond.no, false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!cond.isYes) Color(0xFFBA1A1A) else Color(0xFFEEEEEE),
                                contentColor = if (!cond.isYes) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("condition_${cond.no}_no")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TIDAK", fontSize = 12.sp)
                        }
                    }

                    // Optional Note
                    if (!cond.isYes || cond.note.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cond.note,
                            onValueChange = { onUpdateNote(cond.no, it) },
                            label = { Text("Keterangan Tambahan (Penyebab / Masalah)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("condition_note_${cond.no}")
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 2: Pemeriksaan Isi Kotak P3K (Bagian B)
// ----------------------------------------------------------------------------
@Composable
private fun StepItemsScreen(
    items: List<InspectionItem>,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
    onToggleGood: (Int, Boolean) -> Unit,
    onUpdateQty: (Int, Int) -> Unit,
    onUpdateExpiry: (Int, String) -> Unit,
    onUpdateNote: (Int, String) -> Unit,
    onDeleteCustomItem: (Int) -> Unit,
    onSetAllStandard: () -> Unit,
    onOpenAddDialog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "B. Pemeriksaan Isi Kotak P3K",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Cek kuantitas & kondisi barang",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    // Fast Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = onSetAllStandard,
                            modifier = Modifier.testTag("set_all_standard_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20))
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Std", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onOpenAddDialog,
                            modifier = Modifier.testTag("open_add_item_dialog_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Tambah", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Categorized list of items
        var lastCategory = ""
        items.forEach { item ->
            if (item.category != lastCategory) {
                lastCategory = item.category
                val currentCategory = lastCategory
                item(key = "header_$currentCategory") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentCategory,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item(key = "item_${item.no}") {
                ItemCheckCard(
                    item = item,
                    onIncrement = { onIncrement(item.no) },
                    onDecrement = { onDecrement(item.no) },
                    onToggleGood = { onToggleGood(item.no, it) },
                    onUpdateExpiry = { onUpdateExpiry(item.no, it) },
                    onUpdateNote = { onUpdateNote(item.no, it) },
                    onDelete = if (item.category.contains("6. BARANG LAIN")) {
                        { onDeleteCustomItem(item.no) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun ItemCheckCard(
    item: InspectionItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onToggleGood: (Boolean) -> Unit,
    onUpdateExpiry: (String) -> Unit,
    onUpdateNote: (String) -> Unit,
    onDelete: (() -> Unit)?
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isDeficit = item.currentQty < item.standardQty
    val isDamaged = !item.conditionIsGood

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("item_card_${item.no}"),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDeficit || isDamaged -> Color(0xFFFFF8F8)
                else -> Color.White
            }
        ),
        border = BorderStroke(
            1.dp,
            when {
                isDeficit || isDamaged -> Color(0xFFFFCDD2)
                else -> Color(0xFFE0E0E0)
            }
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item number badge
                Text(
                    text = "${item.no}.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.width(24.dp)
                )

                // Item Name and Usage
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.usage.isNotBlank()) {
                        Text(
                            text = item.usage,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            lineHeight = 13.sp
                        )
                    }
                    Text(
                        text = "Standar: ${item.standardQty} ${item.unit}",
                        fontSize = 11.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Delete custom button if custom item
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("delete_custom_item_${item.no}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Counter Row & Condition Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Counter +/-
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFF1F8F1), RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(36.dp).testTag("dec_item_${item.no}")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurang", tint = Color(0xFF1B5E20))
                    }

                    Text(
                        text = "${item.currentQty}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = if (isDeficit) Color(0xFFBA1A1A) else Color(0xFF1B5E20),
                        modifier = Modifier
                            .width(36.dp)
                            .testTag("qty_text_${item.no}"),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(36.dp).testTag("inc_item_${item.no}")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color(0xFF1B5E20))
                    }
                }

                // Condition Toggle (Baik vs Rusak)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(8.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .background(if (item.conditionIsGood) Color(0xFF1B5E20) else Color.Transparent)
                            .clickable { onToggleGood(true) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("item_${item.no}_good")
                    ) {
                        Text(
                            text = "Baik",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.conditionIsGood) Color.White else Color.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(if (!item.conditionIsGood) Color(0xFFBA1A1A) else Color.Transparent)
                            .clickable { onToggleGood(false) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("item_${item.no}_damaged")
                    ) {
                        Text(
                            text = "Rusak",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!item.conditionIsGood) Color.White else Color.Black
                        )
                    }
                }
            }

            // Status Badge line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isDeficit || isDamaged) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFBA1A1A), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDeficit) "Kurang ${item.standardQty - item.currentQty} ${item.unit}!" else "Kondisi Rusak!",
                            color = Color(0xFFBA1A1A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text(
                        text = "✓ Sesuai Standar",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }

                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Tutup Detail ▲" else "Exp / Catatan ▼",
                        fontSize = 11.sp
                    )
                }
            }

            // Collapsible details (Expiry Date & Notes)
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = item.expiryDate,
                        onValueChange = onUpdateExpiry,
                        label = { Text("Tgl Kadaluarsa (opsional)") },
                        placeholder = { Text("Contoh: 12/2027") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = item.note,
                        onValueChange = onUpdateNote,
                        label = { Text("Keterangan / Tindak Lanjut") },
                        placeholder = { Text("Contoh: Habis dipakai shift malam") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 3: Kesimpulan Pemeriksaan & Paraf Petugas (Bagian Kesimpulan)
// ----------------------------------------------------------------------------
@Composable
private fun StepConclusionScreen(
    conclusionStatus: String,
    replacementNotes: String,
    signatureData: String,
    inspectorName: String,
    inspectorRole: String,
    onSelectConclusion: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onSignatureChanged: (String) -> Unit,
    onAutoGenerateNotes: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "KESIMPULAN PEMERIKSAAN",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
        }

        // Conclusion Radio Cards
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectConclusion("LENGKAP") }
                    .testTag("conclusion_lengkap_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (conclusionStatus == "LENGKAP") Color(0xFFE8F5E9) else Color.White
                ),
                border = BorderStroke(
                    if (conclusionStatus == "LENGKAP") 2.dp else 1.dp,
                    if (conclusionStatus == "LENGKAP") Color(0xFF2E7D32) else Color(0xFFCCCCCC)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = conclusionStatus == "LENGKAP",
                        onClick = { onSelectConclusion("LENGKAP") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Kotak P3K LENGKAP dan LAYAK PAKAI",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Kondisi fisik bersih, isi barang sesuai standar kuantitas dan higienis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectConclusion("BELUM_LENGKAP") }
                    .testTag("conclusion_belum_lengkap_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (conclusionStatus == "BELUM_LENGKAP") Color(0xFFFFEBEE) else Color.White
                ),
                border = BorderStroke(
                    if (conclusionStatus == "BELUM_LENGKAP") 2.dp else 1.dp,
                    if (conclusionStatus == "BELUM_LENGKAP") Color(0xFFC62828) else Color(0xFFCCCCCC)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = conclusionStatus == "BELUM_LENGKAP",
                        onClick = { onSelectConclusion("BELUM_LENGKAP") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Kotak P3K BELUM LENGKAP",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB71C1C)
                        )
                        Text(
                            text = "Perlu pengisian ulang barang yang kurang atau perbaikan fisik kotak.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Auto Summary Helper Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catatan / Barang yang Perlu Diganti / Diisi Ulang:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onAutoGenerateNotes,
                    modifier = Modifier.testTag("auto_generate_notes_button")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto-Rangkum", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            OutlinedTextField(
                value = replacementNotes,
                onValueChange = onUpdateNotes,
                placeholder = { Text("Tuliskan barang yang kurang, rusak, atau perlu diajukan pengadaan ulang...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("input_replacement_notes"),
                maxLines = 5
            )
        }

        // Inspector signature section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Diperiksa Oleh:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                    Text(
                        text = inspectorName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = inspectorRole,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bubuhkan Paraf Pemeriksa (Digital):",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    SignaturePad(
                        initialSignaturePoints = signatureData,
                        onSignatureChanged = onSignatureChanged,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// Dialog Tambah Barang Tambahan (Kategori 6)
// ----------------------------------------------------------------------------
@Composable
private fun AddCustomItemDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, usage: String, qty: Int, unit: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var usage by remember { mutableStateOf("") }
    var qtyString by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("buah") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Barang P3K") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Barang") },
                    placeholder = { Text("Misal: Termometer Digital") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_item_name_input")
                )
                OutlinedTextField(
                    value = usage,
                    onValueChange = { usage = it },
                    label = { Text("Kegunaan (opsional)") },
                    placeholder = { Text("Mengukur suhu tubuh korban") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_item_usage_input")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = qtyString,
                        onValueChange = { qtyString = it },
                        label = { Text("Jml Standar") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("custom_item_qty_input")
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Satuan") },
                        placeholder = { Text("buah / set / pak") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("custom_item_unit_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val qty = qtyString.toIntOrNull() ?: 1
                        onAdd(name, usage, qty, unit)
                    }
                },
                modifier = Modifier.testTag("confirm_add_item_button")
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
