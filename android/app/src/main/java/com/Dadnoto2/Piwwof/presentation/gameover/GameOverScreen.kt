package com.Dadnoto2.Piwwof.presentation.gameover

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BloomPinkDeep
import com.Dadnoto2.Piwwof.core.theme.CoinGold
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SplashMid
import com.Dadnoto2.Piwwof.core.theme.SuccessMint
import com.Dadnoto2.Piwwof.core.theme.SurfaceSunken
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.ui.PrimaryButton
import com.Dadnoto2.Piwwof.core.ui.ScreenBackground
import com.Dadnoto2.Piwwof.core.ui.SecondaryButton
import com.Dadnoto2.Piwwof.core.ui.StatRow
import com.Dadnoto2.Piwwof.core.ui.StatSpec
import com.Dadnoto2.Piwwof.core.ui.groupedNumber
import com.Dadnoto2.Piwwof.core.ui.spriteResFor
import com.Dadnoto2.Piwwof.domain.model.StageResult

@Composable
fun GameOverRoute(
    result: StageResult,
    reducedMotion: Boolean,
    onPlayAgain: () -> Unit,
    onLevels: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(result) {
        viewModel.bind(result)
    }
    GameOverScreen(
        state = state,
        reducedMotion = reducedMotion,
        onPlayAgain = onPlayAgain,
        onLevels = onLevels,
        onMenu = onMenu
    )
}

@Composable
fun GameOverScreen(
    state: GameOverUiState,
    reducedMotion: Boolean,
    onPlayAgain: () -> Unit,
    onLevels: () -> Unit,
    onMenu: () -> Unit
) {
    val hero = remember(state.heroSpriteSlot) { Animatable(if (reducedMotion) 1f else 0.6f) }
    LaunchedEffect(state.heroSpriteSlot, reducedMotion) {
        if (reducedMotion) {
            hero.snapTo(1f)
        } else {
            hero.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        }
    }
    val shownCoins by animateIntAsState(targetValue = state.coinsEarned, label = "coins")
    val tint = if (state.isWin) SuccessMint else SplashMid

    ScreenBackground(
        overlay = listOf(
            tint.copy(alpha = if (state.isWin) 0.34f else 0.66f),
            SurfaceSunken.copy(alpha = 0.86f)
        ),
        accent = if (state.isWin) SuccessMint else BloomPinkDeep,
        animated = !reducedMotion,
        baseColor = SurfaceSunken
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(state.headlineRes),
                style = MaterialTheme.typography.displaySmall,
                color = SurfaceWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite.copy(alpha = 0.22f))
                )
                Image(
                    painter = painterResource(spriteResFor(state.heroSpriteSlot)),
                    contentDescription = stringResource(state.bloomNameRes),
                    modifier = Modifier
                        .size(118.dp)
                        .scale(hero.value)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (slot in 0 until 3) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = if (slot < state.stars) {
                            stringResource(R.string.cd_star)
                        } else {
                            null
                        },
                        tint = if (slot < state.stars) CoinGold else SurfaceWhite.copy(alpha = 0.26f),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            StatRow(
                stats = listOf(
                    StatSpec(
                        label = stringResource(R.string.label_score),
                        value = groupedNumber(state.score),
                        accent = BloomPinkDeep
                    ),
                    StatSpec(
                        label = stringResource(R.string.label_blooms_burst),
                        value = state.bloomsBurst.toString(),
                        accent = GrapePurple
                    ),
                    StatSpec(
                        label = stringResource(R.string.label_crust_cracked),
                        value = state.crustCracked.toString(),
                        accent = SuccessMint
                    )
                )
            )

            if (state.coinsEarned > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.result_coins, shownCoins),
                    style = MaterialTheme.typography.titleMedium,
                    color = CoinGold
                )
            }
            if (state.missionTitleRes != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(
                        R.string.result_mission,
                        stringResource(state.missionTitleRes)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(R.string.cta_play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_levels),
                onClick = onLevels,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                leadingIconRes = R.drawable.icon_home,
                leadingIsImage = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
