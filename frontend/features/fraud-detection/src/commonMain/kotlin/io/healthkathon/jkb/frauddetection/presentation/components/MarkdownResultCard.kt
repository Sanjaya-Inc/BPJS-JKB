package io.healthkathon.jkb.frauddetection.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MarkdownResultCard(
    markdown: String,
    feedbackGiven: Boolean,
    onFeedback: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            MarkdownContent(markdown = markdown)
            AnimatedContent(feedbackGiven, modifier = Modifier.padding(top = 32.dp)) {
                if (!feedbackGiven) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FeedbackButtons(onFeedback = onFeedback)
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "✅ Terima kasih atas feedback Anda!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MarkdownContent(markdown: String) {
    val lines = markdown.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        when {
            line.startsWith("# ") -> {
                Text(
                    text = line.removePrefix("# "),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            line.startsWith("## ") -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = line.removePrefix("## "),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            line.startsWith("### ") -> {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = line.removePrefix("### "),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            line.trim() == "---" -> {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            line.startsWith("- ") || line.startsWith("* ") -> {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = parseInlineMarkdown(line.removePrefix("- ").removePrefix("* ")),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            line.matches(Regex("^\\d+\\.\\s.*")) -> {
                val number = line.substringBefore(".").trim()
                val content = line.substringAfter(".").trim()
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "$number.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = parseInlineMarkdown(content),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            line.startsWith("| ") && line.endsWith(" |") -> {
                val tableLines = persistentListOf<String>()
                var j = i
                while (j < lines.size && lines[j].startsWith("|")) {
                    tableLines.add(lines[j])
                    j++
                }

                if (tableLines.size >= 2) {
                    MarkdownTable(tableLines)
                    Spacer(modifier = Modifier.height(16.dp))
                    i = j - 1
                }
            }

            line.startsWith("```") -> {
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }

                if (codeLines.isNotEmpty()) {
                    CodeBlock(code = codeLines.joinToString("\n"))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            line.isNotBlank() -> {
                Text(
                    text = parseInlineMarkdown(line),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            else -> {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        i++
    }
}

@Composable
private fun parseInlineMarkdown(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        val boldRegex = Regex("\\*\\*(.+?)\\*\\*")
        val codeRegex = Regex("`(.+?)`")

        var processedText = text

        boldRegex.findAll(text).forEach { match ->
            val beforeMatch = processedText.substring(0, match.range.first - currentIndex)
            append(beforeMatch)

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }

            processedText = processedText.substring(match.range.last + 1 - currentIndex)
            currentIndex = match.range.last + 1
        }

        codeRegex.findAll(processedText).forEach { match ->
            val beforeMatch = processedText.substring(0, match.range.first)
            append(beforeMatch)

            withStyle(
                style = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    background = MaterialTheme.colorScheme.surfaceVariant,
                    fontSize = 14.sp
                )
            ) {
                append(match.groupValues[1])
            }

            processedText = processedText.substring(match.range.last + 1)
        }

        append(processedText)
    }
}

@Composable
private fun MarkdownTable(tableLines: PersistentList<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        tableLines.forEachIndexed { index, line ->
            if (index == 1) return@forEachIndexed

            val cells = line.split("|")
                .filter { it.isNotBlank() }
                .map { it.trim() }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                cells.forEach { cell ->
                    Text(
                        text = cell,
                        style = if (index == 0) {
                            MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        } else {
                            MaterialTheme.typography.bodyMedium
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }

            if (index < tableLines.size - 1 && index != 0) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
private fun CodeBlock(code: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FeedbackButtons(
    onFeedback: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Apakah analisis ini benar?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        FilledTonalButton(
            onClick = { onFeedback(true) },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "👍",
                style = MaterialTheme.typography.titleLarge
            )
        }

        FilledTonalButton(
            onClick = { onFeedback(false) },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "👎",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
