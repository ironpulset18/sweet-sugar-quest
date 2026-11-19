package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    accent: Color = BloomPink,
    fill: Color = SurfaceWhite,
    corner: Dp = 28.dp,
    elevation: Dp = 14.dp,
    borderAlpha: Float = 0.3f,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Surface(
        modifier = modifier.shadow(elevation, shape, ambientColor = accent, spotColor = accent),
        shape = shape,
        color = fill,
        border = BorderStroke(2.dp, accent.copy(alpha = borderAlpha))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            listOf(SurfaceWhite.copy(alpha = 0.55f), Color.Transparent)
                        )
                    )
            )
            content()
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    corner: Dp = 18.dp,
    fillAlpha: Float = 0.16f,
    borderAlpha: Float = 0.24f,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .clip(shape)
            .background(SurfaceWhite.copy(alpha = fillAlpha))
            .background(
                Brush.verticalGradient(
                    listOf(SurfaceWhite.copy(alpha = 0.14f), Color.Transparent)
                )
            )
            .border(1.dp, SurfaceWhite.copy(alpha = borderAlpha), shape),
        content = content
    )
}
