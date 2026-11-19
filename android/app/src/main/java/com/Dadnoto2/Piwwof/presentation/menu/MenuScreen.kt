package com.Dadnoto2.Piwwof.presentation.menu

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BlushBase
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.BloomPinkDeep
import com.Dadnoto2.Piwwof.core.theme.CoinGold
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SuccessMint
import com.Dadnoto2.Piwwof.core.theme.SurfaceAlt
import com.Dadnoto2.Piwwof.core.theme.SurfaceSunken
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.CoinBadge
import com.Dadnoto2.Piwwof.core.ui.ScreenBackground
import com.Dadnoto2.Piwwof.core.ui.StatRow
import com.Dadnoto2.Piwwof.core.ui.StatSpec
import com.Dadnoto2.Piwwof.core.ui.groupedNumber

@Composable
fun MenuRoute(
    reducedMotion: Boolean,
    onPlay: (Int) -> Unit,
    onLevels: () -> Unit,
    onMissions: () -> Unit,
    onCollection: () -> Unit,
    onRewards: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    MenuScreen(
        state = state,
        reducedMotion = reducedMotion,
        onPlay = { stage ->
            viewModel.acknowledgeReset()
            onPlay(stage)
        },
        onLevels = onLevels,
        onMissions = onMissions,
        onCollection = onCollection,
        onRewards = onRewards,
        onTutorial = onTutorial,
        onSettings = onSettings
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    reducedMotion: Boolean,
    onPlay: (Int) -> Unit,
    onLevels: () -> Unit,
    onMissions: () -> Unit,
    onCollection: () -> Unit,
    onRewards: () -> Unit,
    onTutorial: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menu")
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (reducedMotion) 1f else 1.02f,
        animationSpec = infiniteRepeatable(tween(2200), repeatMode = RepeatMode.Reverse),
        label = "breathe"
    )

    ScreenBackground(
        overlay = listOf(
            BlushBase.copy(alpha = 0.86f),
            SurfaceSunken.copy(alpha = 0.78f),
            SurfaceAlt.copy(alpha = 0.92f)
        ),
        accent = BloomPink,
        animated = !reducedMotion,
        baseColor = BlushBase
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuHeader(coins = state.coins)

            PlayTile(
                stageLine = stringResource(
                    R.string.menu_stage_line,
                    state.currentStage,
                    stringResource(state.stageNameRes)
                ),
                scale = breathe,
                onPlay = { onPlay(state.currentStage) }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BentoTile(
                    label = stringResource(R.string.cta_levels),
                    value = stringResource(R.string.label_fraction, state.unlockedStages, state.totalStages),
                    iconRes = R.drawable.ic_map,
                    accent = GrapePurple,
                    onClick = onLevels,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.cta_missions),
                    value = stringResource(R.string.label_fraction, state.missionsDone, state.missionsTotal),
                    iconRes = R.drawable.ic_target,
                    accent = SuccessMint,
                    onClick = onMissions,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BentoTile(
                    label = stringResource(R.string.cta_collection),
                    value = stringResource(R.string.label_fraction, state.bloomsOwned, state.bloomSlots),
                    spriteRes = R.drawable.sprite_3,
                    accent = BloomPinkDeep,
                    onClick = onCollection,
                    modifier = Modifier.weight(1f)
                )
                BentoTile(
                    label = stringResource(R.string.cta_rewards),
                    value = stringResource(R.string.label_fraction, state.claimableChests, 3),
                    iconRes = R.drawable.ic_gift,
                    accent = CoinGold,
                    badge = state.claimableChests > 0,
                    onClick = onRewards,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ChipTile(
                    label = stringResource(R.string.cta_tutorial),
                    iconRes = R.drawable.ic_help,
                    onClick = onTutorial,
                    modifier = Modifier.weight(1f)
                )
                ChipTile(
                    label = stringResource(R.string.cta_settings),
                    iconRes = R.drawable.ic_settings,
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (state.dataWasReset) {
                Text(
                    text = stringResource(R.string.settings_data_reset),
                    style = MaterialTheme.typography.bodyMedium,
                    color = BloomPinkDeep,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (state.firstRun) {
                Text(
                    text = stringResource(R.string.menu_hint_first),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                StatRow(
                    stats = listOf(
                        StatSpec(
                            label = stringResource(R.string.label_best_score),
                            value = groupedNumber(state.bestScore),
                            accent = BloomPinkDeep
                        ),
                        StatSpec(
                            label = stringResource(R.string.label_stars),
                            value = state.stars.toString(),
                            accent = CoinGold
                        )
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun MenuHeader(coins: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.cd_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.brand_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.menu_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
        CoinBadge(coins = coins)
    }
}

@Composable
private fun PlayTile(stageLine: String, scale: Float, onPlay: () -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Button(
        onClick = onPlay,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale)
            .shadow(16.dp, shape, ambientColor = BloomPink, spotColor = BloomPink)
            .background(
                Brush.verticalGradient(listOf(SurfaceWhite, SurfaceSunken)),
                shape
            )
            .border(2.dp, BloomPink.copy(alpha = 0.32f), shape)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(62.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.cta_play),
                    style = MaterialTheme.typography.displaySmall,
                    color = BloomPinkDeep
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stageLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun BentoTile(
    label: String,
    value: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRes: Int? = null,
    spriteRes: Int? = null,
    badge: Boolean = false
) {
    val shape = RoundedCornerShape(24.dp)
    Button(
        onClick = onClick,
        shape = shape,
        contentPadding = PaddingValues(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        ),
        modifier = modifier
            .height(98.dp)
            .shadow(10.dp, shape, ambientColor = accent, spotColor = accent)
            .background(SurfaceWhite, shape)
            .border(2.dp, accent.copy(alpha = 0.3f), shape)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    spriteRes != null -> Image(
                        painter = painterResource(spriteRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
                    iconRes != null -> Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            if (badge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(BloomPinkDeep)
                )
            }
        }
    }
}

@Composable
private fun ChipTile(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onClick,
        shape = shape,
        contentPadding = PaddingValues(horizontal = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary
        ),
        modifier = modifier
            .height(54.dp)
            .background(SurfaceWhite.copy(alpha = 0.92f), shape)
            .border(2.dp, GrapePurple.copy(alpha = 0.28f), shape)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = GrapePurple,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary
        )
    }
}
