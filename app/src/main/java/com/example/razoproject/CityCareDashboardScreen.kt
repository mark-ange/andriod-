package com.example.razoproject

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette matching Screenshots
val DashboardGreenPrimary = Color(0xFF1E7A38)
val DashboardGreenDark = Color(0xFF14532D)
val DashboardBannerBg = Color(0xFFE2ECFF)
val DashboardBannerIconBg = Color(0xFF16A34A)
val DashboardCardBorder = Color(0xFFE2E8F0)
val DashboardTextDark = Color(0xFF1E293B)
val DashboardTextMuted = Color(0xFF64748B)
val DashboardHomeTabYellow = Color(0xFFFACC15)

enum class DashboardSubScreen {
    Main,
    NewReport,
    TicketDetails,
    Notifications,
    Tips
}

data class ActionGridItem(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color
)

data class WasteReportItem(
    val id: String,
    val title: String,
    val barangay: String,
    val date: String,
    val status: String, // "PENDING", "ASSIGNED", "RESOLVED"
    val description: String = "Uncollected waste piled near the drainage. Causing obstruction on the sidewalk.",
    val category: String = "Illegal Dump Site",
    val landmark: String = "Near Barangay Health Center",
    val imageBitmap: Bitmap? = null
)

data class TipItem(
    val title: String,
    val description: String,
    val imageType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityCareDashboardScreen(
    onLogout: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Home, 1: Reports, 2: Support, 3: Profile, 4: More
    var currentSubScreen by remember { mutableStateOf(DashboardSubScreen.Main) }

    // Quick Services Grid state & modals from original UI
    var selectedGridFeature by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showReportSuccessModal by remember { mutableStateOf(false) }
    var capturedPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var reportTitleInput by remember { mutableStateOf("") }

    var selectedReport by remember {
        mutableStateOf(
            WasteReportItem(
                id = "#CDO-2026-0412",
                title = "Brgy. Carmen, Purok 2",
                barangay = "Carmen",
                date = "Aug 5, 2026 • 3:00 PM",
                status = "PENDING"
            )
        )
    }

    // Quick Action Grid List (Original UI)
    val actionGridList = remember {
        listOf(
            ActionGridItem("Schedule", Icons.Outlined.Schedule, Color(0xFF334155)),
            ActionGridItem("Recycle", Icons.Outlined.Autorenew, Color(0xFF16A34A)),
            ActionGridItem("Events", Icons.Outlined.Event, Color(0xFFA855F7)),
            ActionGridItem("Tips", Icons.Outlined.Lightbulb, Color(0xFFEAB308)),
            ActionGridItem("Blog", Icons.AutoMirrored.Outlined.Article, Color(0xFF0284C7)),
            ActionGridItem("Donate", Icons.Outlined.FavoriteBorder, Color(0xFFEC4899))
        )
    }

    // Reports State List
    val reportList = remember {
        mutableStateListOf(
            WasteReportItem(
                id = "#CDO-2026-0412",
                title = "Brgy. Carmen, Purok 2",
                barangay = "Carmen",
                date = "Aug 5, 2026 • 3:00 PM",
                status = "PENDING"
            ),
            WasteReportItem(
                id = "#CDO-2026-0411",
                title = "Brgy. Lapasan, Zone 3",
                barangay = "Lapasan",
                date = "Aug 5, 2026 • 11:15 AM",
                status = "ASSIGNED"
            ),
            WasteReportItem(
                id = "#CDO-2026-0409",
                title = "Brgy. Macasandig, Tibasak",
                barangay = "Macasandig",
                date = "Aug 2, 2026 • 4:28 PM",
                status = "RESOLVED"
            ),
            WasteReportItem(
                id = "#CDO-2026-0406",
                title = "Brgy. Kauswagan, NHA",
                barangay = "Kauswagan",
                date = "Jul 28, 2026 • 11:30 AM",
                status = "PENDING"
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            if (currentSubScreen == DashboardSubScreen.Main) {
                // 5-Item Bottom Navigation Bar (Home, Reports, Support, Profile, More)
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Nav Items List
                        val navItems = listOf(
                            Triple("Home", Icons.Default.Home, 0),
                            Triple("Reports", Icons.AutoMirrored.Outlined.Assignment, 1),
                            Triple("Support", Icons.Outlined.HeadsetMic, 2),
                            Triple("Profile", Icons.Outlined.Person, 3),
                            Triple("More", Icons.Outlined.MoreHoriz, 4)
                        )

                        navItems.forEach { (label, icon, tabIndex) ->
                            val isSelected = selectedTab == tabIndex
                            Surface(
                                onClick = { selectedTab = tabIndex },
                                color = if (isSelected) DashboardHomeTabYellow else Color.Transparent,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.height(42.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(
                                        horizontal = if (isSelected) 12.dp else 8.dp,
                                        vertical = 6.dp
                                    )
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSelected) DashboardTextDark else DashboardTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DashboardTextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentSubScreen) {
                DashboardSubScreen.NewReport -> {
                    NewWasteReportScreen(
                        onBack = { currentSubScreen = DashboardSubScreen.Main },
                        onSubmit = { newReport ->
                            reportList.add(0, newReport)
                            selectedReport = newReport
                            selectedTab = 1 // Go to Reports
                            currentSubScreen = DashboardSubScreen.Main
                        }
                    )
                }

                DashboardSubScreen.TicketDetails -> {
                    TicketDetailsScreen(
                        report = selectedReport,
                        onBack = { currentSubScreen = DashboardSubScreen.Main }
                    )
                }

                DashboardSubScreen.Notifications -> {
                    NotificationsScreen(
                        onBack = { currentSubScreen = DashboardSubScreen.Main }
                    )
                }

                DashboardSubScreen.Tips -> {
                    TipsScreen(
                        onBack = { currentSubScreen = DashboardSubScreen.Main }
                    )
                }

                DashboardSubScreen.Main -> {
                    when (selectedTab) {
                        0 -> HomeScreenContent(
                            actionGridList = actionGridList,
                            onSelectGridFeature = { featureTitle ->
                                if (featureTitle == "Tips") {
                                    currentSubScreen = DashboardSubScreen.Tips
                                } else {
                                    selectedGridFeature = featureTitle
                                }
                            },
                            onOpenNewReport = { currentSubScreen = DashboardSubScreen.NewReport },
                            onOpenNotifications = { currentSubScreen = DashboardSubScreen.Notifications },
                            onSelectReport = { report ->
                                selectedReport = report
                                currentSubScreen = DashboardSubScreen.TicketDetails
                            },
                            onViewAllReports = { selectedTab = 1 }
                        )

                        1 -> MyReportsScreenContent(
                            reports = reportList,
                            onOpenNotifications = { currentSubScreen = DashboardSubScreen.Notifications },
                            onSelectReport = { report ->
                                selectedReport = report
                                currentSubScreen = DashboardSubScreen.TicketDetails
                            }
                        )

                        2 -> SupportScreenContent(
                            onOpenNotifications = { currentSubScreen = DashboardSubScreen.Notifications },
                            onOpenNewTicket = { currentSubScreen = DashboardSubScreen.NewReport },
                            onSelectReport = { report ->
                                selectedReport = report
                                currentSubScreen = DashboardSubScreen.TicketDetails
                            }
                        )

                        3 -> ProfileScreenContent(
                            onOpenNotifications = { currentSubScreen = DashboardSubScreen.Notifications },
                            onLogout = onLogout
                        )

                        4 -> MoreScreenContent(
                            onOpenNotifications = { currentSubScreen = DashboardSubScreen.Notifications },
                            onOpenTips = { currentSubScreen = DashboardSubScreen.Tips }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog when Quick Action Feature is clicked
    selectedGridFeature?.let { featureName ->
        AlertDialog(
            onDismissRequest = { selectedGridFeature = null },
            title = { Text(featureName, fontWeight = FontWeight.Bold) },
            text = { Text("Viewing $featureName for Barangay Carmen, CDO.") },
            confirmButton = {
                TextButton(onClick = { selectedGridFeature = null }) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal Dialog after Photo Report is Captured
    if (showReportDialog && capturedPhotoBitmap != null) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Submit Waste Concern Report", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Photo proof attached with GPS coordinates (8.4822° N, 124.6175° E).", fontSize = 12.sp)
                    Image(
                        bitmap = capturedPhotoBitmap!!.asImageBitmap(),
                        contentDescription = "Report Photo Proof",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    OutlinedTextField(
                        value = reportTitleInput,
                        onValueChange = { reportTitleInput = it },
                        placeholder = { Text("Describe uncollected waste concern...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        showReportSuccessModal = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DashboardGreenPrimary)
                ) {
                    Text("SUBMIT REPORT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReportSuccessModal) {
        AlertDialog(
            onDismissRequest = { showReportSuccessModal = false },
            title = { Text("Report Submitted!", fontWeight = FontWeight.Bold, color = DashboardBannerIconBg) },
            text = { Text("Your report reference #CR-2026-102 has been received by CLENRO.") },
            confirmButton = {
                Button(
                    onClick = { showReportSuccessModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DashboardGreenPrimary)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------
// 1. HOME TAB SCREEN CONTENT
// -----------------------------------------------------------------------------
@Composable
fun HomeScreenContent(
    actionGridList: List<ActionGridItem>,
    onSelectGridFeature: (String) -> Unit,
    onOpenNewReport: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSelectReport: (WasteReportItem) -> Unit,
    onViewAllReports: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Header Profile Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Hello, Marai!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )
                        Text(
                            text = "📍 Zone 3, Carmen, CDO",
                            fontSize = 11.sp,
                            color = DashboardTextMuted
                        )
                    }
                }

                // Notification Bell Icon with Red Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, DashboardCardBorder, CircleShape)
                        .clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = DashboardTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                    // Red Badge Dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .align(Alignment.TopEnd)
                            .padding(2.dp)
                    )
                }
            }
        }

        // 2. Collection Schedule Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DashboardBannerBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Megaphone Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DashboardBannerIconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CLENRO Collection Schedule for Carmen:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "MWF - Biodegradable\nTTHS - Non-Bio",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = DashboardTextMuted,
                            lineHeight = 15.sp
                        )
                    }

                    // Info Watermark
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF93C5FD),
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }

        // 3. REPORT UNCOLLECTED WASTE Large Primary Button
        item {
            Card(
                onClick = { onOpenNewReport() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DashboardGreenPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Camera Circle Icon
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "REPORT UNCOLLECTED WASTE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Snap photo & auto-attach GPS/Purok",
                        fontSize = 11.sp,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 4. Quick Status Summary Cards (2 PENDING, 1 ASSIGNED, 5 DONE)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: PENDING
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "2",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEAB308)
                        )
                        Text(
                            text = "PENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextMuted
                        )
                    }
                }

                // Card 2: ASSIGNED
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "1",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7)
                        )
                        Text(
                            text = "ASSIGNED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextMuted
                        )
                    }
                }

                // Card 3: DONE
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "5",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF16A34A)
                        )
                        Text(
                            text = "DONE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextMuted
                        )
                    }
                }
            }
        }

        // 5. Quick Services Action Feature Grid
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Quick Services",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextDark
                )

                // 2 Rows x 3 Columns Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 0..2) {
                            if (i < actionGridList.size) {
                                val item = actionGridList[i]
                                Card(
                                    onClick = { onSelectGridFeature(item.title) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.iconColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DashboardTextDark
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (i in 3..5) {
                            if (i < actionGridList.size) {
                                val item = actionGridList[i]
                                Card(
                                    onClick = { onSelectGridFeature(item.title) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title,
                                            tint = item.iconColor,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DashboardTextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Recent Reports Header & Item
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Reports",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextDark
                )
                Text(
                    text = "View All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardBannerIconBg,
                    modifier = Modifier.clickable { onViewAllReports() }
                )
            }
        }

        item {
            // Recent Report Card (Overflowing Public Bin)
            Card(
                onClick = {
                    onSelectReport(
                        WasteReportItem(
                            id = "#CDO-2026-0412",
                            title = "Brgy. Carmen, Purok 2",
                            barangay = "Carmen",
                            date = "Today, 8:45 AM",
                            status = "PENDING"
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadgeChip(status = "PENDING")

                        Text(
                            text = "Today, 8:45 AM",
                            fontSize = 11.sp,
                            color = DashboardTextMuted
                        )
                    }

                    Text(
                        text = "Overflowing Public Bin",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DashboardTextDark
                    )

                    Text(
                        text = "📍 Zone 3, Max Suniel St, Carmen",
                        fontSize = 12.sp,
                        color = DashboardTextMuted
                    )

                    // Map Placeholder Box
                    MapPreviewBox(heightDp = 100)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// 2. NEW WASTE REPORT FORM SCREEN
// -----------------------------------------------------------------------------
@Composable
fun NewWasteReportScreen(
    onBack: () -> Unit,
    onSubmit: (WasteReportItem) -> Unit
) {
    val context = LocalContext.current
    var capturedPhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedCategory by remember { mutableStateOf("Illegal Dump Site") }
    var barangayInput by remember { mutableStateOf("Carmen") }
    var purokInput by remember { mutableStateOf("Purok 2, Max Suniel St.") }
    var landmarkInput by remember { mutableStateOf("Near Barangay Health Center") }
    var remarksInput by remember { mutableStateOf("Trash has been uncollected for 3 days.") }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedPhotoBitmap = bitmap
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun safeLaunchCamera() {
        try {
            cameraLauncher.launch(null)
        } catch (_: SecurityException) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val categories = listOf(
        "Overflowing Bins",
        "Illegal Dump Site",
        "Clogged Drainage Waste",
        "Uncollected Household Trash"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DashboardTextDark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Waste Report",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextDark
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            // 1. Photo Evidence Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Photo Evidence",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DashboardTextDark
                    )

                    // Dashed Camera Capture Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { safeLaunchCamera() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedPhotoBitmap != null) {
                            Image(
                                bitmap = capturedPhotoBitmap!!.asImageBitmap(),
                                contentDescription = "Trash Evidence",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = null,
                                    tint = DashboardTextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap camera to capture trash image",
                                    fontSize = 12.sp,
                                    color = DashboardTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 2. Location Details
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DashboardGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Location Details",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashboardTextDark
                            )
                        }

                        // Map Preview Box with Pin
                        MapPreviewBox(heightDp = 110)

                        Text("Barangay", fontSize = 11.sp, color = DashboardTextMuted)
                        OutlinedTextField(
                            value = barangayInput,
                            onValueChange = { barangayInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text("Purok / Zone / Street", fontSize = 11.sp, color = DashboardTextMuted)
                        OutlinedTextField(
                            value = purokInput,
                            onValueChange = { purokInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text("Optional Landmark", fontSize = 11.sp, color = DashboardTextMuted)
                        OutlinedTextField(
                            value = landmarkInput,
                            onValueChange = { landmarkInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // 3. Incident Category
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Incident Category",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )
                        Text(
                            text = "Select one",
                            fontSize = 10.sp,
                            color = DashboardTextMuted
                        )
                    }

                    // Chips Layout
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryChip(
                                label = categories[0],
                                selected = selectedCategory == categories[0],
                                onClick = { selectedCategory = categories[0] }
                            )
                            CategoryChip(
                                label = categories[1],
                                selected = selectedCategory == categories[1],
                                onClick = { selectedCategory = categories[1] }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryChip(
                                label = categories[2],
                                selected = selectedCategory == categories[2],
                                onClick = { selectedCategory = categories[2] }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CategoryChip(
                                label = categories[3],
                                selected = selectedCategory == categories[3],
                                onClick = { selectedCategory = categories[3] }
                            )
                        }
                    }
                }
            }

            // 4. Additional Remarks
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Additional Remarks",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DashboardTextDark
                    )

                    OutlinedTextField(
                        value = remarksInput,
                        onValueChange = { remarksInput = it },
                        placeholder = { Text("Describe concern...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 5. Submit Button
            item {
                Button(
                    onClick = {
                        val newId = "#CDO-2026-04" + (13..99).random()
                        val newReport = WasteReportItem(
                            id = newId,
                            title = "Brgy. $barangayInput, $purokInput",
                            barangay = barangayInput,
                            date = "Today, Just Now",
                            status = "PENDING",
                            description = remarksInput,
                            category = selectedCategory,
                            landmark = landmarkInput,
                            imageBitmap = capturedPhotoBitmap
                        )
                        Toast.makeText(context, "Incident Report Submitted!", Toast.LENGTH_SHORT).show()
                        onSubmit(newReport)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DashboardGreenPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUBMIT INCIDENT REPORT",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. MY REPORTS TAB CONTENT
// -----------------------------------------------------------------------------
@Composable
fun MyReportsScreenContent(
    reports: List<WasteReportItem>,
    onOpenNotifications: () -> Unit,
    onSelectReport: (WasteReportItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredReports = reports.filter { report ->
        val matchesStatus = when (selectedFilter) {
            "PENDING" -> report.status == "PENDING"
            "ASSIGNED" -> report.status == "ASSIGNED"
            "RESOLVED" -> report.status == "RESOLVED"
            else -> true
        }
        val matchesSearch = report.title.contains(searchQuery, ignoreCase = true) ||
                report.id.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Row: CityCare CDO + Notification Bell
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DashboardGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Recycling,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CityCare CDO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DashboardGreenPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, DashboardCardBorder, CircleShape)
                        .clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = DashboardTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = "My Reports",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DashboardTextDark
            )
        }

        // Search Bar & Filter Button Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search street or ID...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DashboardTextMuted)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier
                        .size(52.dp)
                        .clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filter",
                            tint = DashboardTextDark
                        )
                    }
                }
            }
        }

        // Filter Tabs Row (ALL, PENDING, ASSIGNED, RESOLVED)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                listOf("ALL", "PENDING", "ASSIGNED", "RESOLVED").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isSelected) DashboardGreenPrimary else DashboardTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(2.dp)
                                .background(if (isSelected) DashboardGreenPrimary else Color.Transparent)
                        )
                    }
                }
            }
        }

        // List of Report Cards
        items(filteredReports) { report ->
            Card(
                onClick = { onSelectReport(report) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image Thumbnail Box
                    ReportImageThumbnail(
                        bitmap = report.imageBitmap,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = report.id,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashboardTextMuted
                            )
                            StatusBadgeChip(status = report.status)
                        }

                        Text(
                            text = report.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "🕒 ${report.date}",
                            fontSize = 10.sp,
                            color = DashboardTextMuted
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// -----------------------------------------------------------------------------
// 4. SUPPORT TAB CONTENT (MATCHING IMAGE 2)
// -----------------------------------------------------------------------------
@Composable
fun SupportScreenContent(
    onOpenNotifications: () -> Unit = {},
    onOpenNewTicket: () -> Unit = {},
    onSelectReport: (WasteReportItem) -> Unit = {}
) {
    val context = LocalContext.current
    var selectedYear by remember { mutableStateOf("2026") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Agent Illustration & Callout Row (Matching Image 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Support Agent Illustration Box
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        // Desk & background element
                        drawRect(color = Color(0xFF3B3B58), topLeft = Offset(w * 0.15f, h * 0.55f), size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.35f))
                        drawCircle(color = Color(0xFF2C2C3E), radius = h * 0.22f, center = Offset(w * 0.5f, h * 0.32f))
                    }
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Support Agent",
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(54.dp)
                    )
                }

                // Callout Banner Text
                Text(
                    text = "Facing issues? Create a ticket for quick resolution!",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextDark,
                    lineHeight = 19.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Action Buttons Row: Create Ticket & Call Support (Matching Image 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenNewTicket,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4B3E)),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(
                        text = "Create Ticket",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = { Toast.makeText(context, "Calling CLENRO Hotline (088) 857-3200...", Toast.LENGTH_SHORT).show() },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2D4B3E)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2D4B3E)),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                ) {
                    Text(
                        text = "Call Support",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section Header: Complaint History & Year Dropdown (Matching Image 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Complaint History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextDark
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedYear,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = DashboardTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Complaint History Card items
        item {
            Card(
                onClick = {
                    onSelectReport(
                        WasteReportItem(
                            id = "#CDO-2026-0412",
                            title = "Bin Not Collected on Time",
                            barangay = "Carmen",
                            date = "Today, 8:45 AM",
                            status = "PENDING",
                            description = "Missed pickup for household waste."
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Bin Not Collected on Time",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )
                        Text(
                            text = "Missed Pickup",
                            fontSize = 11.sp,
                            color = DashboardTextMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = DashboardTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// -----------------------------------------------------------------------------
// 5. MORE TAB CONTENT (MATCHING IMAGE 1)
// -----------------------------------------------------------------------------
@Composable
fun MoreScreenContent(
    onOpenNotifications: () -> Unit = {},
    onOpenTips: () -> Unit = {}
) {
    val context = LocalContext.current

    val moreItems = listOf(
        "Privacy Settings",
        "Notification Settings",
        "Payment Methods",
        "Language Settings",
        "Data Settings",
        "Legal Information",
        "FAQs"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))
        }

        items(moreItems) { title ->
            Card(
                onClick = {
                    if (title == "FAQs") {
                        onOpenTips()
                    } else {
                        Toast.makeText(context, "Opening $title...", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DashboardTextDark
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = DashboardTextDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // App Version Row
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "App Version",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = DashboardTextDark
                    )

                    Text(
                        text = "1.0.1",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DashboardTextDark
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// -----------------------------------------------------------------------------
// 6. TICKET DETAILS & OTHER SCREENS
// -----------------------------------------------------------------------------
@Composable
fun TicketDetailsScreen(
    report: WasteReportItem,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top App Bar
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DashboardTextDark
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ticket Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardGreenPrimary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            // Ticket Title & Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ticket ${report.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DashboardGreenPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Share link copied!", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { Toast.makeText(context, "Ticket cancellation requested.", Toast.LENGTH_SHORT).show() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                        ) {
                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Reported Issue Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Image banner with In-Progress badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            ReportImageThumbnail(
                                bitmap = report.imageBitmap,
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color(0xFF0284C7),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopStart)
                            ) {
                                Text(
                                    text = "In-Progress",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Reported Issue",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )
                        Text(
                            text = report.description,
                            fontSize = 11.sp,
                            color = DashboardTextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Location Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MapPreviewBox(heightDp = 90)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DashboardGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "District 1 - ${report.barangay}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashboardTextDark
                                )
                                Text(
                                    text = report.title,
                                    fontSize = 11.sp,
                                    color = DashboardTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Resolution Progress Stepper Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Resolution Progress",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashboardTextDark
                            )

                            Surface(
                                color = Color(0xFF0284C7),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "In Progress",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        // Stepper Timeline
                        TimelineStepItem(
                            stepNumber = 1,
                            title = "Report Submitted",
                            subtitle = "April 12, 2026 • 08:30 AM",
                            isCompleted = true,
                            isCurrent = false
                        )

                        TimelineStepItem(
                            stepNumber = 2,
                            title = "Acknowledged by Barangay/CLENRO",
                            subtitle = "April 12, 2026 • 09:15 AM",
                            isCompleted = true,
                            isCurrent = false
                        )

                        TimelineStepItem(
                            stepNumber = 3,
                            title = "Assigned to Dispatch Driver",
                            subtitle = "",
                            isCompleted = false,
                            isCurrent = true,
                            extraContent = {
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF0284C7)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocalShipping,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("Truck #4", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DashboardTextDark)
                                            Text("Driver: J. Cruz", fontSize = 10.sp, color = DashboardTextMuted)
                                        }
                                    }
                                }
                            }
                        )

                        TimelineStepItem(
                            stepNumber = 4,
                            title = "Resolution In-Progress",
                            subtitle = "Pending arrival",
                            isCompleted = false,
                            isCurrent = false
                        )

                        TimelineStepItem(
                            stepNumber = 5,
                            title = "Cleanup Verified & Closed",
                            subtitle = "Awaiting verification",
                            isCompleted = false,
                            isCurrent = false,
                            isLast = true
                        )
                    }
                }
            }

            // Contact Support Button
            item {
                OutlinedButton(
                    onClick = { Toast.makeText(context, "Calling CLENRO Hotline (088) 857-3200...", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DashboardGreenPrimary)
                ) {
                    Icon(Icons.Default.HeadsetMic, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CONTACT SUPPORT / HOTLINE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 7. NOTIFICATIONS SCREEN
// -----------------------------------------------------------------------------
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = DashboardTextDark
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DashboardGreenPrimary
                    )
                }

                Surface(
                    color = DashboardGreenPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "2 New",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("RECENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DashboardTextMuted)
                    Text("Mark all as read", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DashboardGreenPrimary)
                }
            }

            // Notification Item 1
            item {
                NotificationCardItem(
                    title = "Your report #CDO-0412 in Zone 3 Carmen was marked RESOLVED.",
                    time = "10 mins ago",
                    icon = Icons.Default.CheckCircle,
                    iconBg = Color(0xFFDCFCE7),
                    iconColor = Color(0xFF166534),
                    borderColor = Color(0xFF22C55E)
                )
            }

            // Notification Item 2
            item {
                NotificationCardItem(
                    title = "CLENRO dispatched Truck #4 to your reported location in Lapasan.",
                    time = "2 hours ago",
                    icon = Icons.Default.LocalShipping,
                    iconBg = Color(0xFFDBEAFE),
                    iconColor = Color(0xFF1E40AF),
                    borderColor = Color(0xFF0284C7)
                )
            }

            // Notification Item 3
            item {
                NotificationCardItem(
                    title = "Reminder: Schedule change for waste collection in Barangay Macasandig this holiday weekend.",
                    time = "Yesterday, 08:00 AM",
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    iconBg = Color(0xFFE0EBFF),
                    iconColor = Color(0xFF2563EB),
                    borderColor = Color.Transparent
                )
            }

            // Notification Item 4
            item {
                NotificationCardItem(
                    title = "Report #CDO-0401 collection is delayed due to heavy traffic on CM Recto Avenue.",
                    time = "Yesterday, 02:30 PM",
                    icon = Icons.Default.Warning,
                    iconBg = Color(0xFFFEF08A),
                    iconColor = Color(0xFF854D0E),
                    borderColor = Color.Transparent
                )
            }

            item {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("Load More Older Notifications", fontSize = 12.sp, color = DashboardGreenPrimary)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 8. TIPS SCREEN
// -----------------------------------------------------------------------------
@Composable
fun TipsScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val tipsList = remember {
        listOf(
            TipItem(
                title = "Waste Reduction Tips",
                description = "Reduce waste by transforming your unused items into practical solutions with these tips.",
                imageType = "reduction"
            ),
            TipItem(
                title = "Recycling Tips",
                description = "Make your own produce bags to reduce single-use plastic waste.",
                imageType = "recycling"
            ),
            TipItem(
                title = "Composting Tips",
                description = "Turn food scraps into nutrient-rich compost for your garden.",
                imageType = "composting"
            ),
            TipItem(
                title = "Reusing and Upcycling Tips",
                description = "Upcycling tips that turn the ordinary into the extraordinary.",
                imageType = "upcycling"
            ),
            TipItem(
                title = "Waste Management Awareness Tips",
                description = "Tips about awareness and ideas with regards to waste management process",
                imageType = "awareness"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Header
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DashboardTextDark
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tips",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DashboardTextDark
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Expert Courtesy Header Card
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            drawCircle(color = Color(0xFF22543D), radius = size.width * 0.45f, center = center)
                            drawCircle(color = Color(0xFF38A169), radius = size.width * 0.32f, center = Offset(center.x - 10, center.y + 10))
                            drawCircle(color = Color(0xFFF6AD55), radius = size.width * 0.12f, center = Offset(center.x + 18, center.y - 12))
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Helpful tips, courtesy of",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF16A34A)
                        )

                        Text(
                            text = "Dr. Adam Smith, Ph.D.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )

                        Text(
                            text = "Environmental Engineer & Sustainability Consultant",
                            fontSize = 10.sp,
                            color = DashboardTextMuted,
                            lineHeight = 14.sp
                        )

                        Text(
                            text = "Adam.Smith@gmail.com",
                            fontSize = 11.sp,
                            color = Color(0xFF16A34A),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, "Emailing Dr. Adam Smith...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // 2. Tip Cards List
            items(tipsList) { tip ->
                Card(
                    onClick = {
                        Toast.makeText(context, "Opening ${tip.title} guide...", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TipThumbnailBox(
                            imageType = tip.imageType,
                            modifier = Modifier
                                .width(94.dp)
                                .height(68.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = tip.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashboardTextDark
                            )

                            Text(
                                text = tip.description,
                                fontSize = 10.sp,
                                color = DashboardTextMuted,
                                lineHeight = 14.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // 3. Load More Button
            item {
                Button(
                    onClick = {
                        Toast.makeText(context, "Loading additional tips...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4E6E5D))
                ) {
                    Text(
                        text = "Load More",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TipThumbnailBox(
    imageType: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, iconVector) = when (imageType) {
        "reduction" -> Pair(Color(0xFF52796F), Icons.Outlined.Autorenew)
        "recycling" -> Pair(Color(0xFF84A98C), Icons.Default.Recycling)
        "composting" -> Pair(Color(0xFF354F52), Icons.Outlined.Park)
        "upcycling" -> Pair(Color(0xFF2F3E46), Icons.Outlined.Build)
        else -> Pair(Color(0xFF6B8A7A), Icons.Outlined.Lightbulb)
    }

    Box(
        modifier = modifier.background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(color = Color.White.copy(alpha = 0.15f), radius = size.width * 0.4f)
        }
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// Helper to decode bitmap from URI
// -----------------------------------------------------------------------------
fun loadBitmapFromUri(context: android.content.Context, uri: android.net.Uri): Bitmap? {
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// -----------------------------------------------------------------------------
// 9. PROFILE TAB CONTENT (UPDATED WITH EDITABLE PROFILE & COVER BANNER)
// -----------------------------------------------------------------------------
@Composable
fun ProfileScreenContent(
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // Editable Image Bitmaps & Pickers State
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var coverBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showProfilePicker by remember { mutableStateOf(false) }
    var showCoverPicker by remember { mutableStateOf(false) }

    // Launchers for Profile Image
    val profileGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profileBitmap = loadBitmapFromUri(context, uri)
        }
    }

    val profileCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            profileBitmap = bitmap
        }
    }

    // Launchers for Cover Image
    val coverGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coverBitmap = loadBitmapFromUri(context, uri)
        }
    }

    val coverCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            coverBitmap = bitmap
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Row: CityCare CDO + Notification Bell
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(DashboardGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Recycling,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CityCare CDO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DashboardGreenPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, DashboardCardBorder, CircleShape)
                        .clickable { onOpenNotifications() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = DashboardTextDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Profile User Card with Editable Cover Banner & Circular Profile Picture
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Cover Banner Image Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(Color(0xFFE2E8F0))
                            .clickable { showCoverPicker = true }
                    ) {
                        if (coverBitmap != null) {
                            Image(
                                bitmap = coverBitmap!!.asImageBitmap(),
                                contentDescription = "Cover Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Sage Green & Beige overlapping circles pattern
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                drawRect(color = Color(0xFFE8E5DA))
                                drawCircle(color = Color(0xFF90A997).copy(alpha = 0.85f), radius = h * 0.95f, center = Offset(w * 0.25f, h * 0.5f))
                                drawCircle(color = Color(0xFFB3C5B7).copy(alpha = 0.65f), radius = h * 0.75f, center = Offset(w * 0.35f, h * 0.3f))
                                drawCircle(color = Color(0xFFD3DEC8).copy(alpha = 0.75f), radius = h * 0.85f, center = Offset(w * 0.85f, h * 0.6f))
                                drawCircle(color = Color(0xFFE1E8D5).copy(alpha = 0.55f), radius = h * 1.15f, center = Offset(w * 0.7f, h * 0.8f))
                            }
                        }

                        // Camera / Edit Badge for Cover Photo (Bottom Right Overlay)
                        Surface(
                            onClick = { showCoverPicker = true },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.55f),
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Change Cover",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (coverBitmap != null) "Edit Cover" else "Add Cover",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Top-left Back Arrow Button (< in white circle)
                        Surface(
                            onClick = { },
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(32.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = DashboardTextDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Top-right Share Button (↗ in white circle)
                        Surface(
                            onClick = { Toast.makeText(context, "Profile link copied!", Toast.LENGTH_SHORT).show() },
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(32.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Profile",
                                    tint = DashboardTextDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Overlapping Center Circular Profile Picture with Camera Edit Badge
                    Box(
                        modifier = Modifier
                            .offset(y = (-40).dp)
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCBD5E1))
                            .border(3.dp, Color.White, CircleShape)
                            .clickable { showProfilePicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap!!.asImageBitmap(),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Photo",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Camera Badge overlay at bottom-right of avatar
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(DashboardGreenPrimary)
                                .border(2.dp, Color.White, CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Edit Profile Photo",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    // User Info Details below Avatar
                    Column(
                        modifier = Modifier
                            .offset(y = (-30).dp)
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Maria Santos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DashboardTextDark
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = DashboardTextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "maria.santos@email.com", fontSize = 12.sp, color = DashboardTextMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = DashboardTextMuted, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "+63 917 123 4567", fontSize = 12.sp, color = DashboardTextMuted)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = DashboardGreenPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Barangay Carmen, Zone 3", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DashboardGreenPrimary)
                            }
                        }
                    }
                }
            }
        }

        // ACCOUNT Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ACCOUNT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DashboardTextMuted)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileMenuRow(icon = Icons.Outlined.Edit, title = "Edit Account Details")
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuRow(icon = Icons.Outlined.Map, title = "Update Default Barangay / Purok")
                    }
                }
            }
        }

        // PREFERENCES & SECURITY Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("PREFERENCES & SECURITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DashboardTextMuted)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileMenuRow(icon = Icons.Outlined.Notifications, title = "Push Notification Preferences")
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuRow(icon = Icons.Outlined.Lock, title = "Change Password")
                    }
                }
            }
        }

        // SUPPORT Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SUPPORT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DashboardTextMuted)

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DashboardCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ProfileMenuRow(icon = Icons.Outlined.Description, title = "Terms of Service & Privacy Policy")
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuRow(icon = Icons.Outlined.HelpOutline, title = "Help & FAQ")
                    }
                }
            }
        }

        // LOGOUT Button
        item {
            TextButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFDC2626))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Logout", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Profile Photo Selection Dialog
    if (showProfilePicker) {
        AlertDialog(
            onDismissRequest = { showProfilePicker = false },
            title = { Text("Update Profile Picture", fontWeight = FontWeight.Bold) },
            text = { Text("Choose how you would like to update your profile photo.") },
            confirmButton = {
                Button(
                    onClick = {
                        showProfilePicker = false
                        profileGalleryLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DashboardGreenPrimary)
                ) {
                    Text("Choose from Gallery", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showProfilePicker = false
                        profileCameraLauncher.launch(null)
                    }
                ) {
                    Text("Take Photo")
                }
            }
        )
    }

    // Cover Photo Selection Dialog
    if (showCoverPicker) {
        AlertDialog(
            onDismissRequest = { showCoverPicker = false },
            title = { Text("Update Background Cover", fontWeight = FontWeight.Bold) },
            text = { Text("Choose how you would like to update your profile background photo.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCoverPicker = false
                        coverGalleryLauncher.launch("image/*")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DashboardGreenPrimary)
                ) {
                    Text("Choose from Gallery", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showCoverPicker = false
                        coverCameraLauncher.launch(null)
                    }
                ) {
                    Text("Take Photo")
                }
            }
        )
    }
}

// -----------------------------------------------------------------------------
// HELPER COMPOSABLES
// -----------------------------------------------------------------------------
@Composable
fun StatusBadgeChip(status: String) {
    val (bgColor, textColor) = when (status) {
        "PENDING" -> Pair(Color(0xFFFEF08A), Color(0xFF854D0E))
        "ASSIGNED" -> Pair(Color(0xFFDBEAFE), Color(0xFF1E40AF))
        "RESOLVED", "DONE" -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = status,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) DashboardGreenPrimary else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) DashboardGreenPrimary else Color(0xFFCBD5E1)
        )
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Color.White else DashboardTextDark,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun MapPreviewBox(heightDp: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFEBF3FF)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 40.dp.toPx()
            for (x in 0..size.width.toInt() step step.toInt()) {
                drawLine(
                    color = Color(0xFFD0E1FD),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = 2f
                )
            }
            for (y in 0..size.height.toInt() step step.toInt()) {
                drawLine(
                    color = Color(0xFFD0E1FD),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 2f
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = DashboardGreenPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Text(
                text = "GPS Active",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = DashboardGreenPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun ReportImageThumbnail(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFFE2E8F0)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFCBD5E1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun TimelineStepItem(
    stepNumber: Int,
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean = false,
    extraContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> DashboardGreenPrimary
                            isCurrent -> Color(0xFF0284C7)
                            else -> Color(0xFFE2E8F0)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(if (isCompleted) DashboardGreenPrimary else Color(0xFFE2E8F0))
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isCompleted || isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCompleted || isCurrent) DashboardTextDark else DashboardTextMuted
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = DashboardTextMuted
                )
            }
            extraContent?.invoke()
        }
    }
}

@Composable
fun NotificationCardItem(
    title: String,
    time: String,
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    borderColor: Color
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (borderColor != Color.Transparent) borderColor else DashboardCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DashboardTextDark,
                    lineHeight = 16.sp
                )
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = DashboardTextMuted
                )
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DashboardGreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = DashboardTextDark
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = DashboardTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CityCareDashboardScreenPreview() {
    MaterialTheme {
        CityCareDashboardScreen()
    }
}
