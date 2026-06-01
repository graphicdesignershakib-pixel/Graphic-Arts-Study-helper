package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.StudyViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: StudyViewModel = viewModel()
                MainStudyContainer(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainStudyContainer(viewModel: StudyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val streak by viewModel.activeStreak.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showMobileDrawer by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth
        val isWide = width >= 750.dp

        Scaffold(
            topBar = {
                // Header Topbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isWide) {
                            IconButton(onClick = { showMobileDrawer = !showMobileDrawer }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu Menu", tint = MaterialTheme.colorScheme.onBackground)
                            }
                        }

                        Column {
                            Text(
                                "GOD MODE",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                "STUDY OS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Search input & metrics bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isWide) {
                            // Search Box Widget
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                placeholder = { Text("Search topics index...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp)) },
                                modifier = Modifier
                                    .width(240.dp)
                                    .height(44.dp)
                                    .testTag("search_field_bar"),
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        // Hot streak tracker label badge representation
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🔥", fontSize = 12.sp)
                                Text(
                                    text = "$streak DAYS",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Profile badge Initial SD (Shakib Designer)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Text(
                                "SD",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (!isWide) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 4.dp
                    ) {
                        NavigationBarItem(
                            selected = currentScreen == AppScreen.DASHBOARD,
                            onClick = { viewModel.setScreen(AppScreen.DASHBOARD) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                            label = { Text("Hub") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.NOTES,
                            onClick = { viewModel.setScreen(AppScreen.NOTES) },
                            icon = { Icon(Icons.Default.List, contentDescription = "Notes") },
                            label = { Text("Notes") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.QUIZ_ENGINE,
                            onClick = { viewModel.setScreen(AppScreen.QUIZ_ENGINE) },
                            icon = { Icon(Icons.Default.Check, contentDescription = "Quiz") },
                            label = { Text("Quiz") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen == AppScreen.AI_TUTOR,
                            onClick = { viewModel.setScreen(AppScreen.AI_TUTOR) },
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Tutor") },
                            label = { Text("Tutor") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Persistent Side Bar layout on Desktop splits
                if (isWide) {
                    SidebarMenuPane(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.setScreen(it) },
                        viewModel = viewModel,
                        modifier = Modifier
                            .width(220.dp)
                            .fillMaxHeight()
                    )
                    VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }

                // Render Active body screen
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        AppScreen.NOTES -> NotesScreen(viewModel = viewModel)
                        AppScreen.FLASHCARDS -> FlashcardsScreen(viewModel = viewModel)
                        AppScreen.QUIZ_ENGINE -> QuizScreen(viewModel = viewModel)
                        AppScreen.AI_TUTOR -> TutorScreen(viewModel = viewModel)
                        AppScreen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                        AppScreen.SUPPORT -> SupportScreen(viewModel = viewModel)
                    }
                }
            }
        }

        // Drawer slider layout for mobile screens
        if (!isWide && showMobileDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showMobileDrawer = false }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.7f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = false) {}
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CORE CONSOLE", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showMobileDrawer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close Menu", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        SidebarMenuPane(
                            currentScreen = currentScreen,
                            onNavigate = {
                                viewModel.setScreen(it)
                                showMobileDrawer = false
                            },
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarMenuPane(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Group 1: CORE SPACES
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "CORE SPACES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            SidebarMenuItem(
                icon = Icons.Default.Home,
                label = "Command Center",
                isSelected = currentScreen == AppScreen.DASHBOARD,
                onClick = { onNavigate(AppScreen.DASHBOARD) }
            )

            SidebarMenuItem(
                icon = Icons.Default.List,
                label = "Lecture Notes",
                isSelected = currentScreen == AppScreen.NOTES,
                onClick = { onNavigate(AppScreen.NOTES) }
            )

            SidebarMenuItem(
                icon = Icons.Default.Star,
                label = "Active Flashcards",
                isSelected = currentScreen == AppScreen.FLASHCARDS,
                onClick = { onNavigate(AppScreen.FLASHCARDS) }
            )

            SidebarMenuItem(
                icon = Icons.Default.Check,
                label = "Quiz Engine",
                isSelected = currentScreen == AppScreen.QUIZ_ENGINE,
                onClick = { onNavigate(AppScreen.QUIZ_ENGINE) }
            )

            SidebarMenuItem(
                icon = Icons.Default.AccountCircle,
                label = "AI Tutor",
                isSelected = currentScreen == AppScreen.AI_TUTOR,
                onClick = { onNavigate(AppScreen.AI_TUTOR) }
            )
        }

        // Group 2: SYSTEM CONSOLE
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "CONSOLES & LOGS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            SidebarMenuItem(
                icon = Icons.Default.Star,
                label = "Cognitive Analytics",
                isSelected = currentScreen == AppScreen.ANALYTICS,
                onClick = { onNavigate(AppScreen.ANALYTICS) }
            )

            SidebarMenuItem(
                icon = Icons.Default.Settings,
                label = "System Parameters",
                isSelected = currentScreen == AppScreen.SETTINGS,
                onClick = { onNavigate(AppScreen.SETTINGS) }
            )

            SidebarMenuItem(
                icon = Icons.Default.Info,
                label = "Support Center",
                isSelected = currentScreen == AppScreen.SUPPORT,
                onClick = { onNavigate(AppScreen.SUPPORT) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large violet custom NEW SESSION calling button
        Button(
            onClick = { viewModel.createNewSession() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("new_study_session_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Text(
                    "NEW SESSION",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun SidebarMenuItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            ),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
