package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.NoteEntity
import com.example.ui.StudyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotesScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.allNotes.collectAsState()
    val selectedNoteId by viewModel.selectedNoteId.collectAsState()
    val selectedNote by viewModel.selectedNote.collectAsState()

    var showMobileDetail by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = maxWidth
        val isWideLayout = width >= 750.dp

        if (isWideLayout) {
            // Wide split-screen view matching "God Mode" Notes editor
            Row(modifier = Modifier.fillMaxSize()) {
                // Column 1: Left Notes List Sidebar
                LeftNotesListPane(
                    notes = notes,
                    selectedNoteId = selectedNoteId,
                    onSelect = { viewModel.selectNote(it) },
                    viewModel = viewModel,
                    modifier = Modifier.width(280.dp)
                )

                VerticalDivider(color = Color(0xFFFFFFFF).copy(alpha = 0.05f))

                // Column 2: Center Editor pane
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    selectedNote?.let { note ->
                        CenterEditorPane(
                            note = note,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Select a note to inspect dynamic layout values", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                VerticalDivider(color = Color(0xFFFFFFFF).copy(alpha = 0.05f))

                // Column 3: Right Pane metadata info representation
                selectedNote?.let { note ->
                    RightMetaInfoPane(
                        note = note,
                        modifier = Modifier.width(260.dp)
                    )
                }
            }
        } else {
            // Mobile navigation flow: list view -> click -> full screen detail
            if (showMobileDetail && selectedNote != null) {
                // Detail Edit screen
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header bar with back button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0E0E0E))
                                .padding(8.dp)
                        ) {
                            IconButton(onClick = { showMobileDetail = false }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Text(
                                text = "Back to list",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        selectedNote?.let { note ->
                            CenterEditorPane(
                                note = note,
                                viewModel = viewModel,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            } else {
                // List screen
                LeftNotesListPane(
                    notes = notes,
                    selectedNoteId = selectedNoteId,
                    onSelect = {
                        viewModel.selectNote(it)
                        showMobileDetail = true
                    },
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LeftNotesListPane(
    notes: List<NoteEntity>,
    selectedNoteId: Int,
    onSelect: (Int) -> Unit,
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var showAddNoteDialog by remember { mutableStateOf(false) }

    if (showAddNoteDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("LAYOUT") }
        var newContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Draft New Concept", color = MaterialTheme.colorScheme.primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1C1B1B),
                            unfocusedContainerColor = Color(0xFF0E0E0E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Categories Selector Grid
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("LAYOUT", "COLOR", "TYPE" , "TRENDS").forEach { cat ->
                            val isSel = newCategory == cat
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color(0xFF1C1B1B))
                                    .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(4.dp))
                                    .clickable { newCategory = cat }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(cat, style = MaterialTheme.typography.labelSmall, color = if (isSel) MaterialTheme.colorScheme.primary else Color.White)
                            }
                        }
                    }

                    TextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Write notes summary details here...") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1C1B1B),
                            unfocusedContainerColor = Color(0xFF0E0E0E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newTitle.isNotBlank()) {
                        viewModel.triggerNewNote(newTitle, newCategory, newContent)
                        showAddNoteDialog = false
                    }
                }) {
                    Text("Draft note", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("Discard", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT NOTES",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { showAddNoteDialog = true },
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Notes Draft",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            notes.forEach { note ->
                val isSelected = note.id == selectedNoteId
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onSelect(note.id) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Tag box
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (note.category) {
                                            "LAYOUT" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            "COLOR" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = note.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = when (note.category) {
                                        "LAYOUT" -> MaterialTheme.colorScheme.primary
                                        "COLOR" -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.tertiary
                                    },
                                    fontSize = 9.sp
                                )
                            }

                            Text(
                                text = note.timestampText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CenterEditorPane(
    note: NoteEntity,
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val editTitle by viewModel.editTitle.collectAsState()
    val editContent by viewModel.editContent.collectAsState()
    val isGeneratingAI by viewModel.isGeneratingAI.collectAsState()
    val aiResultText by viewModel.aiResultText.collectAsState()

    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(aiResultText) {
        if (aiResultText != null) {
            showResultDialog = true
        }
    }

    if (showResultDialog && aiResultText != null) {
        AlertDialog(
            onDismissRequest = {
                showResultDialog = false
                viewModel.clearAIResult()
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "AI Assistant",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("AI Assistant Response", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = aiResultText ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showResultDialog = false
                    viewModel.clearAIResult()
                }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Hotlinked 3D Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(note.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Cover Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp)
                    ) {
                        Text("Header Preview", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI ENHANCED tag badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "AI Enhanced",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "AI ENHANCED DRAFT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Textfield Inputs Title
            BasicTextField(
                value = editTitle,
                onValueChange = {
                    viewModel.updateEditTitle(it)
                    viewModel.saveCurrentNoteEdits()
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Textfield Editor Body
            BasicTextField(
                value = editContent,
                onValueChange = {
                    viewModel.updateEditContent(it)
                    viewModel.saveCurrentNoteEdits()
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    lineHeight = 24.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
            )

            // Dynamic layout visual preview (The Grid thumbnail block shown in the center editor)
            Spacer(modifier = Modifier.height(24.dp))
            Text("MATHEMATICAL FORMULAS & LAYOUT DESIGN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.5f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuC6dtzxWWK7G3N0ZZ5aqveEqnhOz2dtL1gXHTL3g_wi7dhSjaZPv5wVusg6YtpqKsf77Z_f6nkDyYdKW595aUNXtVZwkJ-IJ3hezkwY5fg3qsWCDh3uMRS5TJEHUOkX0NLjiXgHFtn5-U1CAQ6SQ3H84d7I8strLOP6XvM7jhkScuEJTL5CUoJk9H8zLzV_zDnOvIny7nD1FmvmU6obsmMiYB1bI5qSTu9zXAuvPlktGEeeI9KvhSjcb1_1MKa8qTVovwmwUcxtEhc")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Precision Matrix",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.5f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://lh3.googleusercontent.com/aida-public/AB6AXuA0SkIicb2W7BjxmEr-UTBJR1FD9iXdm6cDat6T04uwS1acZSdHTc_tktFZ_jS4skXUkhjcTNgvL_5DtFSr_ZEyuHBsfUX5yyQN0-BjU8WewTsU_Xxaty29u1BrfZ4lLhSR8AAMHD7UhI0Omp4PIXyy23K0PgMQZxSGCPGxilSJE6dq-QTVhbhBhBhT9Pbda7hqYhuN87S7OckBu00xSuMqxUDRqfMzPWiuj_tJAi9Joz6PnkVYrHpJwCj41edDKvpbIf34HLnjBLE")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Kerning analysis",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp)) // Extra space to let scrolling avoid the floating toolbar
        }

        // Floating AI Toolbar Overlay at the absolute bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), RoundedCornerShape(99.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "AI",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "AI ASSISTANT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                // Summarize Chip
                Text(
                    text = "Summarize",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.triggerNoteSummary() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                // Generate Quiz Chip
                Text(
                    text = "Gen Quiz",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.triggerNoteQuiz() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                // Explain Topic Vibrant Violet Chip
                Button(
                    onClick = { viewModel.triggerNoteExplanation() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(99.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Explain", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Loading Overlay
        if (isGeneratingAI) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text("AI is processing layouts...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun RightMetaInfoPane(
    note: NoteEntity,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Mastery circle display
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "STUDY STATUS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
                    .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mastery Level", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                Text("${note.masteryLevel}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("MASTERED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontSize = 8.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("REVIEWING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
                }
            }
        }

        // AI Exam relevance indicator
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.03f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "EXAM RELEVANCE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                     text = "${note.examRelevance}",
                     style = MaterialTheme.typography.displayMedium,
                     color = MaterialTheme.colorScheme.onSurface,
                     fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/10",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                )
            }

            Text(
                text = "High probability of appearing in 'Advanced Layout Theory' finals based on dynamic curriculum mapping.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Metatags block
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "METATAGS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                note.tags.split(",").forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Edit History tracker
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "EDIT HISTORY",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                note.editHistory.split(";").forEach { historyItem ->
                    val parts = historyItem.split("|")
                    val actionName = parts.getOrNull(0) ?: "Note Saved"
                    val dateText = parts.getOrNull(1) ?: "Today"

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        )
                        Column {
                            Text(
                                text = actionName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = dateText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
