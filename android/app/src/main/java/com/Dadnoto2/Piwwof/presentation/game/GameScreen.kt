package com.Dadnoto2.Piwwof.presentation.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.BloomPinkDeep
import com.Dadnoto2.Piwwof.core.theme.CrustGrey
import com.Dadnoto2.Piwwof.core.theme.DangerRose
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SplashDeep
import com.Dadnoto2.Piwwof.core.theme.SplashMid
import com.Dadnoto2.Piwwof.core.theme.SuccessMint
import com.Dadnoto2.Piwwof.core.theme.SurfaceSunken
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextMutedOnDark
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.domain.model.StageResult
import com.Dadnoto2.Piwwof.core.ui.BloomMeterBar
import com.Dadnoto2.Piwwof.core.ui.BloomTile
import com.Dadnoto2.Piwwof.core.ui.PrimaryButton
import com.Dadnoto2.Piwwof.core.ui.ScreenBackground
import com.Dadnoto2.Piwwof.core.ui.SecondaryButton
import com.Dadnoto2.Piwwof.core.ui.StatCard
import com.Dadnoto2.Piwwof.core.ui.bloomColor
import com.Dadnoto2.Piwwof.core.ui.groupedNumber
import com.Dadnoto2.Piwwof.core.ui.spriteResFor
import com.Dadnoto2.Piwwof.presentation.dialog.PauseDialog
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun GameRoute(
    stageIndex: Int,
    attempt: Int,
    onFinished: (StageResult) -> Unit,
    onMenu: () -> Unit,
    onRestart: () -> Unit
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(stageIndex, attempt) {
        viewModel.startStage(stageIndex, attempt)
    }
    LaunchedEffect(Unit) {
        viewModel.roundFinished.collect { result -> onFinished(result) }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.pause() }
    }

    GameScreen(
        state = state,
        onPressCell = viewModel::pressCell,
        onSweep = viewModel::sweep,
        onReroll = viewModel::reroll,
        onPause = viewModel::requestPause,
        onResume = viewModel::resume,
        onRestart = {
            viewModel.abandon()
            onRestart()
        },
        onMenu = {
            viewModel.abandon()
            onMenu()
        }
    )
}

@Composable
fun GameScreen(
    state: GameUiState,
    onPressCell: (Int) -> Unit,
    onSweep: () -> Unit,
    onReroll: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            SplashDeep.copy(alpha = 0.72f),
            SplashMid.copy(alpha = 0.52f),
            SurfaceSunken.copy(alpha = 0.72f)
        ),
        accent = GrapePurple,
        animated = !state.reducedMotion,
        baseColor = SplashDeep
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            GameHeader(
                stageIndex = state.stageIndex,
                stageNameRes = state.stageNameRes,
                onMenu = onMenu,
                onPause = onPause
            )
            Spacer(modifier = Modifier.height(8.dp))
            GoalStrip(state = state)
            Spacer(modifier = Modifier.height(8.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val frame = GameConfig.BOARD_FRAME_DP.dp
                val widthLimit = if (maxWidth < GameConfig.BOARD_MAX_WIDTH_DP.dp) {
                    maxWidth
                } else {
                    GameConfig.BOARD_MAX_WIDTH_DP.dp
                }
                val tileByWidth = floor(((widthLimit - frame * 2) / GameConfig.COLS).value)
                val tileByHeight = floor(((maxHeight - frame * 2) / GameConfig.ROWS).value)
                val tile = (if (tileByWidth < tileByHeight) tileByWidth else tileByHeight).dp

                if (state.loading || state.tiles.isEmpty()) {
                    CircularProgressIndicator(
                        color = BloomPink,
                        modifier = Modifier.size(56.dp)
                    )
                } else {
                    BoardSurface(
                        state = state,
                        tile = tile,
                        frame = frame,
                        onPressCell = onPressCell
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite },
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    label = stringResource(R.string.label_moves),
                    value = state.movesLeft.toString(),
                    accent = if (state.lowOnMoves) DangerRose else BloomPink,
                    onDark = true,
                    state = if (state.lowOnMoves) stringResource(R.string.sd_moves_low) else null,
                    modifier = Modifier.weight(1f)
                )
                ScoreStat(
                    score = state.score,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.label_bloom),
                    value = state.meter.toString(),
                    accent = if (state.meterReady) SuccessMint else GrapePurple,
                    onDark = true,
                    state = if (state.meterReady) {
                        stringResource(R.string.sd_meter_ready)
                    } else {
                        stringResource(R.string.sd_meter_charging)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            ControlPanel(
                state = state,
                onSweep = onSweep,
                onReroll = onReroll
            )
        }

        if (state.paused) {
            PauseDialog(
                seed = state.seed,
                onResume = onResume,
                onRestart = onRestart,
                onMenu = onMenu
            )
        }
    }
}

@Composable
private fun GameHeader(
    stageIndex: Int,
    stageNameRes: Int,
    onMenu: () -> Unit,
    onPause: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onMenu, modifier = Modifier.height(48.dp)) {
            Text(
                text = stringResource(R.string.cta_menu),
                style = MaterialTheme.typography.labelLarge,
                color = SurfaceWhite
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.game_stage, stageIndex),
                style = MaterialTheme.typography.titleMedium,
                color = SurfaceWhite
            )
            Text(
                text = stringResource(stageNameRes),
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedOnDark,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SurfaceWhite.copy(alpha = 0.16f))
                .clickable(onClick = onPause),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.cd_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun GoalStrip(state: GameUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceWhite.copy(alpha = 0.14f))
            .border(1.dp, SurfaceWhite.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .padding(horizontal = 10.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        state.goals.forEach { goal ->
            GoalChip(
                iconRes = spriteResFor(goal.bloom),
                isSprite = true,
                done = goal.done,
                target = goal.target,
                complete = goal.complete,
                accent = bloomColor(goal.bloom)
            )
        }
        if (state.crustTarget > 0) {
            GoalChip(
                iconRes = R.drawable.ic_crust,
                isSprite = false,
                done = state.crustDone,
                target = state.crustTarget,
                complete = state.crustDone >= state.crustTarget,
                accent = CrustGrey
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            for (pip in 0 until GameConfig.WILT_LIMIT) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            if (pip < state.wilt) DangerRose else SurfaceWhite.copy(alpha = 0.26f)
                        )
                )
            }
        }
    }
}

@Composable
private fun GoalChip(
    iconRes: Int,
    isSprite: Boolean,
    done: Int,
    target: Int,
    complete: Boolean,
    accent: Color
) {
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (complete) SuccessMint.copy(alpha = 0.3f) else accent.copy(alpha = 0.2f))
            .padding(horizontal = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSprite) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(R.string.cd_crust),
                tint = SurfaceWhite,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = stringResource(R.string.label_fraction, done, target),
            style = MaterialTheme.typography.labelSmall,
            color = SurfaceWhite
        )
        if (complete) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = stringResource(R.string.cd_goal_done),
                tint = SuccessMint,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ScoreStat(score: Int, modifier: Modifier = Modifier) {
    val shown by animateIntAsState(targetValue = score, label = "score")
    StatCard(
        label = stringResource(R.string.label_score),
        value = groupedNumber(shown),
        accent = SurfaceWhite,
        onDark = true,
        modifier = modifier
    )
}

@Composable
private fun BoardSurface(
    state: GameUiState,
    tile: Dp,
    frame: Dp,
    onPressCell: (Int) -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    val shakeTarget = if (state.wiltCell >= 0) {
        if (state.shakeToken % 2 == 0) 7f else -7f
    } else {
        0f
    }
    val density = LocalDensity.current
    val shakeX by animateFloatAsState(
        targetValue = with(density) { shakeTarget.dp.toPx() },
        animationSpec = tween(90),
        label = "shake"
    )
    Column(
        modifier = Modifier
            .offset { IntOffset(shakeX.roundToInt(), 0) }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        SurfaceWhite.copy(alpha = 0.95f),
                        SurfaceSunken.copy(alpha = 0.92f)
                    )
                )
            )
            .border(2.dp, BloomPink.copy(alpha = 0.34f), shape)
            .padding(frame)
    ) {
        for (row in 0 until GameConfig.ROWS) {
            Row {
                for (col in 0 until GameConfig.COLS) {
                    val index = row * state.cols + col
                    val cell = state.tiles.getOrNull(index)
                    if (cell == null) {
                        Spacer(modifier = Modifier.size(tile))
                    } else {
                        BloomTile(
                            id = cell.id,
                            bloom = cell.bloom,
                            crustLayers = cell.crustLayers,
                            tileSize = tile,
                            selected = index in state.selected,
                            hinted = index in state.hinted,
                            bursting = index in state.bursting,
                            wilting = index == state.wiltCell,
                            colourBlindMarkers = state.colourBlindMarkers,
                            reducedMotion = state.reducedMotion,
                            onPress = { onPressCell(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ControlPanel(
    state: GameUiState,
    onSweep: () -> Unit,
    onReroll: () -> Unit
) {
    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceWhite.copy(alpha = 0.93f))
            .border(2.dp, BloomPink.copy(alpha = 0.25f), shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.label_meter, state.meter, GameConfig.METER_MAX),
            style = MaterialTheme.typography.labelSmall,
            color = TextPrimary
        )
        BloomMeterBar(
            progress = state.meterFraction,
            instant = state.reducedMotion
        )
        if (state.usedFallback) {
            Text(
                text = stringResource(R.string.game_fallback),
                style = MaterialTheme.typography.labelSmall,
                color = BloomPinkDeep
            )
        }
        Text(
            text = stringResource(R.string.game_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary.copy(alpha = 0.75f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrimaryButton(
                text = stringResource(R.string.cta_bloom_sweep),
                onClick = onSweep,
                accentFrom = if (state.meterReady) SuccessMint else BloomPink,
                accentTo = if (state.meterReady) GrapePurple else BloomPinkDeep,
                state = if (state.meterReady) {
                    stringResource(R.string.sd_meter_ready)
                } else {
                    stringResource(R.string.sd_meter_charging)
                },
                modifier = Modifier.weight(1.4f)
            )
            SecondaryButton(
                text = stringResource(R.string.cta_reroll, state.rerollsLeft),
                onClick = onReroll,
                enabled = state.rerollsLeft > 0,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
