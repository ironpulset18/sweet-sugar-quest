package com.Dadnoto2.Piwwof.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.SplashDeep
import com.Dadnoto2.Piwwof.core.theme.SplashGlow
import com.Dadnoto2.Piwwof.core.theme.SplashMid
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextMutedOnDark
import kotlin.math.cos
import kotlin.math.sin

private val GRAD_FROM = SplashDeep
private val GRAD_MID = SplashMid
private val GRAD_TO = SplashGlow
private const val RING_DOTS = 8

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.factory)
    val finished by viewModel.finished.collectAsStateWithLifecycle()
    SplashScreen(finished = finished, onFinished = onFinished)
}

@Composable
fun SplashScreen(finished: Boolean, onFinished: () -> Unit) {
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }
    val entrance = remember { Animatable(0.82f) }
    LaunchedEffect(Unit) {
        entrance.animateTo(1f, spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessLow))
    }
    val transition = rememberInfiniteTransition(label = "splash")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "ring"
    )
    val pulse by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(GRAD_FROM, GRAD_MID, GRAD_TO)))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(BloomPink.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.42f),
                    radius = 540f
                ),
                radius = 540f,
                center = Offset(w * 0.5f, h * 0.42f)
            )
            for (i in 0 until 14) {
                val seed = (i * 29 % 100) / 100f
                val x = ((seed * 1.9f + sin((i + 2) * 0.9f) * 0.4f) % 1f) * w
                val y = h - ((pulse * 0.6f + seed) % 1f) * h
                drawCircle(
                    color = SurfaceWhite.copy(alpha = 0.08f + (i % 4) * 0.025f),
                    radius = 3f + (i % 5) * 0.9f,
                    center = Offset(if (x < 0f) -x else x, y)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(228.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension * 0.46f
                    val centre = Offset(size.width / 2f, size.height / 2f)
                    for (i in 0 until RING_DOTS) {
                        val angle = Math.toRadians((spin + i * (360f / RING_DOTS)).toDouble())
                        drawCircle(
                            color = SurfaceWhite.copy(alpha = 0.14f),
                            radius = 6f,
                            center = Offset(
                                centre.x + (cos(angle) * radius).toFloat(),
                                centre.y + (sin(angle) * radius).toFloat()
                            )
                        )
                    }
                }
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(168.dp)
                        .scale(entrance.value)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.brand_title),
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(entrance.value)
                    .offset(y = ((1f - entrance.value) * 90f).dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.brand_sub),
                style = MaterialTheme.typography.titleMedium,
                color = TextMutedOnDark.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))
            LinearProgressIndicator(
                color = BloomPink,
                trackColor = SurfaceWhite.copy(alpha = 0.18f),
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .width(148.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.loading_caption),
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedOnDark.copy(alpha = 0.7f * pulse),
                textAlign = TextAlign.Center
            )
        }
    }
}
