package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.theme.CrustGrey
import com.Dadnoto2.Piwwof.core.theme.DangerRose
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.BloomBadge
import kotlin.math.sin

@Composable
fun BloomTile(
    id: Long,
    bloom: Bloom?,
    crustLayers: Int,
    tileSize: Dp,
    selected: Boolean,
    hinted: Boolean,
    bursting: Boolean,
    wilting: Boolean,
    colourBlindMarkers: Boolean,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier,
    onPress: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(tileSize * 0.32f)
    val appear = remember(id) { Animatable(if (reducedMotion) 1f else 0.55f) }
    LaunchedEffect(id, reducedMotion) {
        if (reducedMotion) {
            appear.snapTo(1f)
        } else {
            appear.animateTo(1f, spring(dampingRatio = 0.72f, stiffness = 420f))
        }
    }
    val burst by animateFloatAsState(
        targetValue = if (bursting) 1f else 0f,
        animationSpec = tween(if (reducedMotion) 110 else 220),
        label = "burst"
    )
    val hintAlpha by animateFloatAsState(
        targetValue = if (hinted) 0.62f else 0f,
        animationSpec = tween(if (reducedMotion) 120 else 420),
        label = "hint"
    )
    val press by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessHigh),
        label = "tilePress"
    )
    val burstScale = 1f + 0.18f * sin(burst * 3.14159f)
    val content = Modifier
        .size(tileSize)
        .padding(2.dp)
        .scale(appear.value * press * (if (bursting) burstScale * (1f - burst) else 1f))
        .alpha(1f - burst)

    val tint = bloom?.let { bloomColor(it) } ?: SurfaceWhite
    Box(
        modifier = modifier
            .size(tileSize)
            .then(if (onPress != null) Modifier.clickable(onClick = onPress) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = content, contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(
                        Brush.verticalGradient(
                            listOf(tint.copy(alpha = 0.26f), tint.copy(alpha = 0.10f))
                        )
                    )
                    .border(2.dp, tint.copy(alpha = if (selected) 0.95f else 0.38f), shape)
            )
            if (hintAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .border(3.dp, SurfaceWhite.copy(alpha = hintAlpha), shape)
                )
            }
            if (bloom != null) {
                Image(
                    painter = painterResource(spriteResFor(bloom)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alpha = if (crustLayers > 0) 0.35f else 1f,
                    modifier = Modifier
                        .size(tileSize - 8.dp)
                        .padding(2.dp)
                )
            }
            if (crustLayers > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(CrustGrey.copy(alpha = if (crustLayers > 1) 0.88f else 0.58f))
                        .border(2.dp, SurfaceWhite.copy(alpha = 0.45f), shape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_crust),
                        contentDescription = stringResource(R.string.cd_crust),
                        tint = SurfaceWhite.copy(alpha = 0.9f),
                        modifier = Modifier.size(tileSize * 0.42f)
                    )
                }
            }
            if (wilting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape)
                        .background(DangerRose.copy(alpha = 0.42f))
                )
            }
            if (colourBlindMarkers && bloom != null && crustLayers == 0) {
                BadgeMark(
                    badge = bloom.badge,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun BadgeMark(badge: BloomBadge, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val paint = Color.White.copy(alpha = 0.92f)
        when (badge) {
            BloomBadge.Circle -> drawCircle(color = paint, radius = w * 0.42f)
            BloomBadge.Square -> drawRect(color = paint, topLeft = Offset(w * 0.16f, h * 0.16f), size = androidx.compose.ui.geometry.Size(w * 0.68f, h * 0.68f))
            BloomBadge.Triangle -> {
                val path = Path()
                path.moveTo(w * 0.5f, h * 0.12f)
                path.lineTo(w * 0.92f, h * 0.86f)
                path.lineTo(w * 0.08f, h * 0.86f)
                path.close()
                drawPath(path, paint)
            }
            BloomBadge.Diamond -> {
                val path = Path()
                path.moveTo(w * 0.5f, h * 0.06f)
                path.lineTo(w * 0.94f, h * 0.5f)
                path.lineTo(w * 0.5f, h * 0.94f)
                path.lineTo(w * 0.06f, h * 0.5f)
                path.close()
                drawPath(path, paint)
            }
            BloomBadge.Star -> {
                val path = Path()
                path.moveTo(w * 0.5f, h * 0.04f)
                path.lineTo(w * 0.63f, h * 0.38f)
                path.lineTo(w * 0.98f, h * 0.4f)
                path.lineTo(w * 0.7f, h * 0.62f)
                path.lineTo(w * 0.8f, h * 0.96f)
                path.lineTo(w * 0.5f, h * 0.76f)
                path.lineTo(w * 0.2f, h * 0.96f)
                path.lineTo(w * 0.3f, h * 0.62f)
                path.lineTo(w * 0.02f, h * 0.4f)
                path.lineTo(w * 0.37f, h * 0.38f)
                path.close()
                drawPath(path, paint)
            }
        }
    }
}
