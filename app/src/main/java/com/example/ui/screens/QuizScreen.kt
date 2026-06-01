package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.StudyViewModel

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Composable
fun QuizScreen(viewModel: StudyViewModel, modifier: Modifier = Modifier) {
    val questions = listOf(
        QuizQuestion(
            text = "What is the primary aesthetic ratio Φ utilized in typographic hierarchical configurations?",
            options = listOf("1.414 (Pythagorean constant)", "1.618 (The Golden Ratio)", "3.141 (Pi constant)", "2.718 (Euler constant)"),
            correctIndex = 1,
            explanation = "The Phi constant (1.618) generates perfectly scaled, natural-feeling typography segments (e.g. 16sp scaled to 26sp)."
        ),
        QuizQuestion(
            text = "Which base grid spacing coefficient is recommended by Material 3 and Swiss layout architectures?",
            options = listOf("5dp", "8dp", "12dp", "15dp"),
            correctIndex = 1,
            explanation = "The 8dp grid acts as the uniform coefficient regulating margins, paddings, and column offsets globally."
        ),
        QuizQuestion(
            text = "Electric Indigo acts as a semantic brand color marker for which feature in modern dashboard design?",
            options = listOf("Critical alert errors", "Offline cache states", "Advanced cognitive intelligence or AI assistance", "User permissions access logs"),
            correctIndex = 2,
            explanation = "Modern interfaces assign electric indigo to represent AI agency or intelligent assistance, separating it from utility tools."
        )
    )

    val selectedAnswers by viewModel.selectedAnswers.collectAsState()
    val quizChecked by viewModel.quizChecked.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "COGNITIVE QUIZ ENGINE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Test your layout precision and design theoretical parameters.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Render questions
        questions.forEachIndexed { qIdx, question ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${qIdx + 1}. ${question.text}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    question.options.forEachIndexed { oIdx, option ->
                        val isSelected = selectedAnswers[qIdx] == oIdx
                        val isCorrectOption = oIdx == question.correctIndex
                        
                        val borderColor = when {
                            quizChecked && isSelected && isCorrectOption -> MaterialTheme.colorScheme.secondary
                            quizChecked && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error
                            quizChecked && !isSelected && isCorrectOption -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }

                        val containerColor = when {
                            quizChecked && isSelected && isCorrectOption -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            quizChecked && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
                            isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(containerColor)
                                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                .clickable(enabled = !quizChecked) {
                                    viewModel.selectQuizAnswer(qIdx, oIdx)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { if (!quizChecked) viewModel.selectQuizAnswer(qIdx, oIdx) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (quizChecked) {
                    val wasCorrect = selectedAnswers[qIdx] == question.correctIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (wasCorrect) MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                                else MaterialTheme.colorScheme.error.copy(alpha = 0.05f)
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = if (wasCorrect) Icons.Default.Star else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (wasCorrect) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (wasCorrect) "CORRECT" else "INCORRECT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wasCorrect) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!quizChecked) {
            Button(
                onClick = { viewModel.checkQuiz() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = selectedAnswers.size == questions.size
            ) {
                Text("Lock in Answers & Grade Test", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        } else {
            val score = questions.filterIndexed { idx, q -> selectedAnswers[idx] == q.correctIndex }.size
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("COGNITIVE DRILL SUMMARY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(
                    text = "You scored $score / ${questions.size} correct answers",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "A streak multiplier is credited to your active command center logs. Streak level updated!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Button(
                        onClick = { viewModel.resetQuiz() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Retake Drill")
                    }

                    Button(
                        onClick = { viewModel.setScreen(com.example.ui.AppScreen.DASHBOARD) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) {
                        Text("Return to Hub")
                    }
                }
            }
        }
    }
}
