package com.golemreader.ui.reading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.golemreader.highlight.HighlightState
import com.golemreader.highlight.HighlightStateEmitter
import com.golemreader.theme.GolemTheme
import com.golemreader.theme.GolemThemeValueSets
import com.golemreader.text.SentenceIndex
import com.golemreader.text.SentenceRecord
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class ReadingRow(
    val index: SentenceIndex,
    val text: String,
    val isHighlighted: Boolean,
)

fun readingRows(
    sentences: List<SentenceRecord>,
    highlightState: HighlightState?,
): List<ReadingRow> =
    sentences.map { sentence ->
        ReadingRow(
            index = sentence.index,
            text = sentence.display,
            isHighlighted = sentence.index == highlightState?.sentenceIndex,
        )
    }

fun highlightedSentenceIndex(rows: List<ReadingRow>): SentenceIndex? =
    rows.firstOrNull { it.isHighlighted }?.index

@Composable
fun ReadingViewScreen(
    bookTitle: String,
    sentences: List<SentenceRecord>,
    highlightEmitter: HighlightStateEmitter,
    modifier: Modifier = Modifier,
    pollingIntervalMillis: Long = GolemThemeValueSets.dark.motion.pollingIntervalMillis,
) {
    val tokens = GolemTheme.tokens
    var highlightState by remember(highlightEmitter) {
        mutableStateOf(highlightEmitter.currentState())
    }
    LaunchedEffect(highlightEmitter, pollingIntervalMillis) {
        while (isActive) {
            highlightState = highlightEmitter.currentState()
            delay(pollingIntervalMillis)
        }
    }

    val rows = remember(sentences, highlightState) {
        readingRows(sentences, highlightState)
    }
    val highlightedListIndex = rows.indexOfFirst { it.isHighlighted }
    val listState = rememberLazyListState()
    LaunchedEffect(highlightedListIndex) {
        if (highlightedListIndex >= 0) {
            listState.animateScrollToItem(highlightedListIndex)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(tokens.spacing.md),
    ) {
        Text(
            text = bookTitle,
            style = tokens.typography.screenTitle,
            color = tokens.colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("reading-view-list"),
            contentPadding = PaddingValues(vertical = tokens.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(tokens.spacing.sm),
        ) {
            itemsIndexed(rows, key = { _, row -> row.index.toString() }) { _, row ->
                val textColor =
                    if (row.isHighlighted) tokens.colors.onHighlight else tokens.colors.textPrimary
                val rowModifier = if (row.isHighlighted) {
                    Modifier.background(
                        color = tokens.colors.highlightSoft,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(tokens.shapes.highlight),
                    )
                } else {
                    Modifier
                }
                Text(
                    text = row.text,
                    style = tokens.typography.reading,
                    color = textColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(rowModifier)
                        .testTag(if (row.isHighlighted) "reading-highlight" else "reading-row"),
                )
            }
        }
    }
}
