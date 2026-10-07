package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultDataHelper
import com.example.data.model.InspectionEntity
import com.example.ui.screens.InspectionDocumentPreviewDialog
import com.example.ui.screens.InspectionFormScreen
import com.example.ui.screens.InspectionRecapScreen
import com.example.ui.screens.SafetyGuidelinesDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.InspectionViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: InspectionViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: InspectionViewModel) {
    val context = LocalContext.current
    var selectedNavTab by remember { mutableIntStateOf(0) } // 0 = Form, 1 = Rekap Berkala
    var showGuidelinesDialog by remember { mutableStateOf(false) }
    var activePreviewInspection by remember { mutableStateOf<InspectionEntity?>(null) }

    // Intercept back press when in form or second tab
    BackHandler(enabled = selectedNavTab != 1) {
        selectedNavTab = 1
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = "P3K Logo",
                                tint = Color(0xFF1B5E20),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "PT. WATU PERKASA ABADI",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Formulir Pemeriksaan Kotak P3K",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showGuidelinesDialog = true },
                        modifier = Modifier.testTag("app_bar_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Petunjuk Pengisian",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = { selectedNavTab = 0 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Formulir") },
                    label = { Text("Formulir P3K", fontSize = 11.sp) },
                    modifier = Modifier.testTag("bottom_nav_form")
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = { selectedNavTab = 1 },
                    icon = { Icon(Icons.Default.History, contentDescription = "Rekap Berkala") },
                    label = { Text("Rekap Berkala", fontSize = 11.sp) },
                    modifier = Modifier.testTag("bottom_nav_recap")
                )
            }
        },
        floatingActionButton = {
            if (selectedNavTab == 1) {
                FloatingActionButton(
                    onClick = {
                        viewModel.startNewInspection()
                        selectedNavTab = 0
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_new_inspection")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Pemeriksaan Baru")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNavTab) {
                0 -> {
                    InspectionFormScreen(
                        viewModel = viewModel,
                        onSaveSuccess = {
                            selectedNavTab = 1 // Switch to recap after save
                        }
                    )
                }
                1 -> {
                    InspectionRecapScreen(
                        viewModel = viewModel,
                        onNavigateToNewInspection = {
                            viewModel.startNewInspection()
                            selectedNavTab = 0
                        },
                        onViewDetailDocument = { entity ->
                            activePreviewInspection = entity
                        },
                        onEditInspection = { entity ->
                            viewModel.loadInspectionForEdit(entity)
                            selectedNavTab = 0
                        },
                        onShareInspection = { entity ->
                            val reportText = viewModel.generateOfficialReportText(entity)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Laporan Pemeriksaan P3K - ${entity.siteLocation}")
                                putExtra(Intent.EXTRA_TEXT, reportText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan P3K"))
                        }
                    )
                }
            }
        }
    }

    // Safety Guidelines modal
    if (showGuidelinesDialog) {
        SafetyGuidelinesDialog(onDismiss = { showGuidelinesDialog = false })
    }

    // Official Paper Document Preview modal
    activePreviewInspection?.let { entity ->
        InspectionDocumentPreviewDialog(
            inspection = entity,
            onDismiss = { activePreviewInspection = null },
            onShareText = { text ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Laporan Pemeriksaan P3K - ${entity.siteLocation}")
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan P3K"))
            }
        )
    }
}
