package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionEntity
import com.example.ui.viewmodel.InspectionViewModel

@Composable
fun InspectionRecapScreen(
    viewModel: InspectionViewModel,
    onNavigateToNewInspection: () -> Unit,
    onViewDetailDocument: (InspectionEntity) -> Unit,
    onEditInspection: (InspectionEntity) -> Unit,
    onShareInspection: (InspectionEntity) -> Unit
) {
    val allInspections by viewModel.allInspections.collectAsState()
    val filteredInspections by viewModel.filteredInspections.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    var inspectionToDelete by remember { mutableStateOf<InspectionEntity?>(null) }

    val totalCount = allInspections.size
    val completeCount = allInspections.count { it.conclusionStatus == "LENGKAP" }
    val refillCount = allInspections.count { it.conclusionStatus == "BELUM_LENGKAP" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header: Bagian C CATATAN PEMERIKSAAN BERKALA
        item {
            Column {
                Text(
                    text = "C. CATATAN PEMERIKSAAN BERKALA (REKAP)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    text = "Riwayat pemantauan rutin pemeriksaan kotak P3K",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Total",
                    count = totalCount,
                    containerColor = Color(0xFFECEFF1),
                    textColor = Color(0xFF37474F),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Lengkap",
                    count = completeCount,
                    containerColor = Color(0xFFE8F5E9),
                    textColor = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Isi Ulang",
                    count = refillCount,
                    containerColor = Color(0xFFFFEBEE),
                    textColor = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = { Text("Cari lokasi, tanggal, pemeriksa...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus pencarian")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_recap_input")
            )
        }

        // Status Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filters = listOf(
                    "SEMUA" to "Semua",
                    "LENGKAP" to "Lengkap (✓)",
                    "BELUM_LENGKAP" to "Perlu Isi Ulang (⚠️)"
                )
                filters.forEach { (key, label) ->
                    FilterChip(
                        selected = statusFilter == key,
                        onClick = { viewModel.setStatusFilter(key) },
                        label = { Text(label, fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_chip_$key")
                    )
                }
            }
        }

        // List of Inspection Records
        if (filteredInspections.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Tidak ada pemeriksaan yang cocok" else "Belum ada riwayat pemeriksaan",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tekan tombol di bawah untuk membuat pemeriksaan berkala baru.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateToNewInspection,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("start_first_inspection_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mulai Pemeriksaan Baru")
                        }
                    }
                }
            }
        } else {
            itemsIndexed(filteredInspections, key = { _, item -> item.id }) { index, item ->
                RecapItemCard(
                    index = index + 1,
                    inspection = item,
                    onViewDetail = { onViewDetailDocument(item) },
                    onEdit = { onEditInspection(item) },
                    onShare = { onShareInspection(item) },
                    onDelete = { inspectionToDelete = item }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    inspectionToDelete?.let { entity ->
        AlertDialog(
            onDismissRequest = { inspectionToDelete = null },
            title = { Text("Hapus Riwayat Pemeriksaan?") },
            text = {
                Text("Apakah Anda yakin ingin menghapus data pemeriksaan di '${entity.siteLocation}' pada tanggal ${entity.inspectionDate}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInspection(entity.id)
                        inspectionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectionToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    count: Int,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$count", fontSize = 20.sp, fontWeight = FontWeight.Black, color = textColor)
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
    }
}

@Composable
private fun RecapItemCard(
    index: Int,
    inspection: InspectionEntity,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val isLengkap = inspection.conclusionStatus == "LENGKAP"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetail() }
            .testTag("recap_card_${inspection.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp,
            if (isLengkap) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Row 1: Index, Date, and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = inspection.inspectionDate,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${inspection.periodMonthYear})",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isLengkap) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isLengkap) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isLengkap) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLengkap) "LENGKAP" else "ISI ULANG",
                            color = if (isLengkap) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Location & Box Position
            Text(
                text = inspection.siteLocation,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF1B5E20)
            )
            Text(
                text = inspection.boxPosition,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            // Row 3: Replacement / Deficit Preview (Column "Barang yang Diisi Ulang / Diganti")
            if (inspection.replacementNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    border = BorderStroke(0.5.dp, Color(0xFFE0E0E0))
                ) {
                    Text(
                        text = "Barang Perlu Diisi Ulang / Diganti:\n${inspection.replacementNotes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 4: Inspector & Paraf Status & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pemeriksa: ${inspection.inspectorName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (inspection.signatureData.isNotBlank()) "✓ Paraf Lengkap" else "○ Belum Ada Paraf",
                        fontSize = 10.sp,
                        color = if (inspection.signatureData.isNotBlank()) Color(0xFF2E7D32) else Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onViewDetail, modifier = Modifier.size(32.dp).testTag("view_doc_${inspection.id}")) {
                        Icon(Icons.Default.Description, contentDescription = "Lihat Form", tint = Color(0xFF1B5E20))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp).testTag("share_${inspection.id}")) {
                        Icon(Icons.Default.Share, contentDescription = "Bagikan", tint = Color(0xFF00695C))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).testTag("edit_${inspection.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF455A64))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_${inspection.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
