package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionEntity
import com.example.ui.components.parseSvgOrPoints

@Composable
fun InspectionDocumentPreviewDialog(
    inspection: InspectionEntity,
    onDismiss: () -> Unit,
    onShareText: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val conditions = inspection.parseConditionChecks()
    val items = inspection.parseItems()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "HSE Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lembar Dokumen Resmi P3K",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_preview_dialog_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Scrollable Document Body Styled to match the physical paper form in the screenshots
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Formal Company Letterhead
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = DefaultDataHelper.COMPANY_NAME,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1B5E20),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = DefaultDataHelper.DEPARTMENT_NAME,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E7D32),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .background(Color(0xFF1B5E20))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = DefaultDataHelper.FORM_TITLE,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = DefaultDataHelper.FORM_SUBTITLE,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Meta Information Grid
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F1)),
                        border = BorderStroke(1.dp, Color(0xFF81C784))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaRowItem("Lokasi / Site", inspection.siteLocation, Modifier.weight(1f))
                                MetaRowItem("Tanggal Periksa", inspection.inspectionDate, Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaRowItem("Letak Kotak P3K", inspection.boxPosition, Modifier.weight(1f))
                                MetaRowItem("Periode (Bln/Thn)", inspection.periodMonthYear, Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MetaRowItem("Nama Pemeriksa", inspection.inspectorName, Modifier.weight(1f))
                                MetaRowItem("Jabatan", inspection.inspectorRole, Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION A: KONDISI KOTAK P3K
                    SectionTitleBanner("A. KONDISI KOTAK P3K")
                    Spacer(modifier = Modifier.height(6.dp))

                    conditions.forEach { cond ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color(0xFFCCCCCC))
                                .background(if (cond.no % 2 == 0) Color(0xFFFAFAFA) else Color.White)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${cond.no}. ${cond.itemText}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val statusColor = if (cond.isYes) Color(0xFF1B5E20) else Color(0xFFBA1A1A)
                            Text(
                                text = if (cond.isYes) "✓ YA" else "✗ TIDAK",
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                style = MaterialTheme.typography.labelMedium
                            )
                            if (cond.note.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${cond.note})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION B: PEMERIKSAAN ISI KOTAK P3K
                    SectionTitleBanner("B. PEMERIKSAAN ISI KOTAK P3K")
                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontal scrollable table container for complete clarity on small phones
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1B5E20), RoundedCornerShape(4.dp))
                    ) {
                        Column {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1B5E20))
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("No", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(24.dp))
                                Text("Nama Barang", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.3f))
                                Text("Jml Std", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(46.dp), textAlign = TextAlign.Center)
                                Text("Ada", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center)
                                Text("Kondisi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.width(50.dp), textAlign = TextAlign.Center)
                            }

                            // Items categorized
                            var lastCat = ""
                            items.forEach { item ->
                                if (item.category != lastCat) {
                                    lastCat = item.category
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE8F5E9))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = lastCat,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(0.5.dp, Color(0xFFE0E0E0))
                                        .background(if (item.no % 2 == 0) Color(0xFFFCFCFC) else Color.White)
                                        .padding(horizontal = 6.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${item.no}", fontSize = 11.sp, modifier = Modifier.width(24.dp))
                                    Column(modifier = Modifier.weight(1.3f)) {
                                        Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        if (item.note.isNotBlank() || item.expiryDate.isNotBlank()) {
                                            Text(
                                                text = listOfNotNull(
                                                    if (item.expiryDate.isNotBlank()) "Exp: ${item.expiryDate}" else null,
                                                    if (item.note.isNotBlank()) item.note else null
                                                ).joinToString(" • "),
                                                fontSize = 9.sp,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                    Text("${item.standardQty} ${item.unit}", fontSize = 10.sp, modifier = Modifier.width(46.dp), textAlign = TextAlign.Center)

                                    val isShort = item.currentQty < item.standardQty
                                    Text(
                                        text = "${item.currentQty}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isShort) Color(0xFFBA1A1A) else Color(0xFF1B5E20),
                                        modifier = Modifier.width(40.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = if (item.conditionIsGood) "Baik" else "RUSAK",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.conditionIsGood) Color(0xFF2E7D32) else Color(0xFFBA1A1A),
                                        modifier = Modifier.width(50.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION KESIMPULAN PEMERIKSAAN
                    SectionTitleBanner("KESIMPULAN PEMERIKSAAN")
                    Spacer(modifier = Modifier.height(8.dp))

                    val isLengkap = inspection.conclusionStatus == "LENGKAP"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLengkap) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        border = BorderStroke(1.dp, if (isLengkap) Color(0xFF4CAF50) else Color(0xFFE57373))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isLengkap) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isLengkap) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isLengkap) {
                                    "[✓] Kotak P3K LENGKAP dan LAYAK PAKAI"
                                } else {
                                    "[✗] Kotak P3K BELUM LENGKAP, perlu pengisian ulang / perbaikan"
                                },
                                fontWeight = FontWeight.Bold,
                                color = if (isLengkap) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    if (inspection.replacementNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Catatan / Barang yang perlu diganti atau ditambah:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                            border = BorderStroke(0.5.dp, Color(0xFFD0D0D0))
                        ) {
                            Text(
                                text = inspection.replacementNotes,
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sign-off Box with Inspector Name and Signature / Paraf
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.End),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFF1B5E20))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Diperiksa oleh,",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Render signature if available
                            val strokes = parseSvgOrPoints(inspection.signatureData)
                            if (strokes.isNotEmpty()) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .background(Color(0xFFFAFAFA), RoundedCornerShape(4.dp))
                                ) {
                                    for (stroke in strokes) {
                                        if (stroke.size > 1) {
                                            val path = Path().apply {
                                                moveTo(stroke[0].x * 0.7f, stroke[0].y * 0.7f)
                                                for (i in 1 until stroke.size) {
                                                    lineTo(stroke[i].x * 0.7f, stroke[i].y * 0.7f)
                                                }
                                            }
                                            drawPath(
                                                path = path,
                                                color = Color(0xFF1B5E20),
                                                style = Stroke(
                                                    width = 2.5f,
                                                    cap = StrokeCap.Round,
                                                    join = StrokeJoin.Round
                                                )
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "[ Paraf Petugas ]",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = inspection.inspectorName,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = inspection.inspectorRole,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "• Daftar barang disusun sesuai Standar P3K di Tempat Kerja Permenakertrans No. PER.15/MEN/VIII/2008.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                // Bottom Action Buttons
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val reportText = shareReportGenerator(inspection)
                            clipboardManager.setText(AnnotatedString(reportText))
                            Toast.makeText(context, "Laporan disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_report_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin Teks", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val reportText = shareReportGenerator(inspection)
                            onShareText(reportText)
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("share_whatsapp_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan Laporan", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaRowItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
        Text(text = value.ifBlank { "-" }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SectionTitleBanner(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B5E20), RoundedCornerShape(4.dp))
            .padding(vertical = 6.dp, horizontal = 10.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun shareReportGenerator(entity: InspectionEntity): String {
    val sb = StringBuilder()
    sb.appendLine("*${DefaultDataHelper.COMPANY_NAME}*")
    sb.appendLine("*${DefaultDataHelper.DEPARTMENT_NAME}*")
    sb.appendLine("*${DefaultDataHelper.FORM_TITLE}*")
    sb.appendLine("----------------------------------------")
    sb.appendLine("📍 Lokasi / Site     : ${entity.siteLocation}")
    sb.appendLine("📦 Letak Kotak P3K   : ${entity.boxPosition}")
    sb.appendLine("📅 Tanggal Periksa   : ${entity.inspectionDate}")
    sb.appendLine("🗓️ Periode           : ${entity.periodMonthYear}")
    sb.appendLine("👷 Pemeriksa         : ${entity.inspectorName} (${entity.inspectorRole})")
    sb.appendLine("----------------------------------------")
    val isLengkap = entity.conclusionStatus == "LENGKAP"
    sb.appendLine("STATUS AKHIR: ${if (isLengkap) "✅ KOTAK P3K LENGKAP & LAYAK PAKAI" else "⚠️ PERLU PENGISIAN ULANG / PERBAIKAN"}")
    if (entity.replacementNotes.isNotBlank()) {
        sb.appendLine("\n📝 *Catatan & Tindak Lanjut:*\n${entity.replacementNotes}")
    }
    sb.appendLine("\nStandar Permenakertrans No. PER.15/MEN/VIII/2008")
    return sb.toString()
}
