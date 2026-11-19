package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.CounterStyle
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextSecondary

data class StatSpec(
    val label: String,
    val value: String,
    val accent: Color = BloomPink
)

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = BloomPink,
    onDark: Boolean = false,
    state: String? = null
) {
    if (value.isBlank()) return
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (onDark) SurfaceWhite.copy(alpha = 0.16f) else SurfaceWhite)
            .border(2.dp, accent.copy(alpha = 0.34f), shape)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .semantics { if (state != null) stateDescription = state }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = value,
                style = CounterStyle,
                color = accent,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (onDark) SurfaceWhite.copy(alpha = 0.78f) else TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatRow(
    stats: List<StatSpec>,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    val visible = stats.filter { it.value.isNotBlank() && it.value != "0" }
    if (visible.size < 2) return
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        visible.forEach { spec ->
            StatCard(
                label = spec.label,
                value = spec.value,
                accent = spec.accent,
                onDark = onDark,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
