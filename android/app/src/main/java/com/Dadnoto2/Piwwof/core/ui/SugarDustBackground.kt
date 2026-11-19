package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import kotlin.math.absoluteValue
import kotlin.math.sin

private const val MOTE_COUNT = 14

@Composable
fun SugarDustBackground(
    accent: Color,
    animated: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "dust")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6200, easing = LinearEasing)),
        label = "dustPhase"
    )
    val drift = if (animated) phase else 0.35f
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accent.copy(alpha = 0.16f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.32f),
                radius = w * 0.85f
            ),
            radius = w * 0.85f,
            center = Offset(w * 0.5f, h * 0.32f)
        )
        for (i in 0 until MOTE_COUNT) {
            val seed = (i * 37 % 100) / 100f
            val speed = 0.55f + seed * 0.45f
            val x = ((seed * 1.7f + sin((i + 1) * 1.3f) * 0.5f) % 1f).absoluteValue * w
            val raw = (drift * speed + seed) % 1f
            val y = h - raw * h
            val radius = 3f + (i % 5) * 1.1f
            val alpha = 0.08f + ((i % 4) * 0.03f)
            drawCircle(
                color = SurfaceWhite.copy(alpha = alpha),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun ScreenBackground(
    overlay: List<Color>,
    accent: Color,
    animated: Boolean,
    modifier: Modifier = Modifier,
    imageAlpha: Float = 1f,
    baseColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(baseColor)) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = imageAlpha,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        SugarDustBackground(accent = accent, animated = animated)
        content()
    }
}
