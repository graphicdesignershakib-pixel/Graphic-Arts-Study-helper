package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.StudyViewModel

@Composable
fun SupportScreen(viewModel: StudyViewModel, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var feedbackType by remember { mutableStateOf("SYSTEM BUG") }
    var comments by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "INTELLIGENCE ENGINE FEEDBACK",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        if (submitted) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("FEEDBACK LOGGED IN DATABASE", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = "A cognitive engineer will analyze your study session telemetry curves. Keep learning!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        submitted = false
                        comments = ""
                        email = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) {
                    Text("Submit Another Log")
                }
            }
        } else {
            Text(
                text = "Report bugs or request study path adjustments. Feedback is immediately saved to the core engineer tracking stack.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Your Email") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("SYSTEM BUG", "STUDY PATH SUGGESTION").forEach { type ->
                    val isSel = feedbackType == type
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { feedbackType = type }
                            .padding(12.dp)
                    ) {
                        Text(type, style = MaterialTheme.typography.labelSmall, color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            TextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Describe the interface parameters details or suggestions...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0E0E0E),
                    unfocusedContainerColor = Color(0xFF0E0E0E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { if (email.isNotBlank() && comments.isNotBlank()) submitted = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = email.isNotBlank() && comments.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Secure Diagnostic Feedback", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}
