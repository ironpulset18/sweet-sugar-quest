package com.Dadnoto2.Piwwof.presentation.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.CrustGrey
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.BloomTile
import com.Dadnoto2.Piwwof.core.ui.PageFrame
import com.Dadnoto2.Piwwof.core.ui.PrimaryButton
import com.Dadnoto2.Piwwof.core.ui.SecondaryButton

@Composable
fun TutorialRoute(
    reducedMotion: Boolean,
    onPlayNow: () -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: TutorialViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    TutorialScreen(
        state = state,
        reducedMotion = reducedMotion,
        onNext = viewModel::next,
        onBack = viewModel::back,
        onPlayNow = {
            viewModel.markSeen()
            onPlayNow()
        },
        onMenu = onMenu
    )
}

@Composable
fun TutorialScreen(
    state: TutorialUiState,
    reducedMotion: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onPlayNow: () -> Unit,
    onMenu: () -> Unit
) {
    PageFrame(
        title = stringResource(R.string.tutorial_title),
        subtitle = stringResource(R.string.tutorial_step, state.step + 1, state.total),
        accent = BloomPink,
        reducedMotion = reducedMotion,
        onMenu = onMenu
    ) {
        val step = state.current
        if (step != null) {
            Spacer(modifier = Modifier.height(4.dp))
            MiniBoard(cells = step.cells, reducedMotion = reducedMotion)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(step.titleRes),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(step.bodyRes),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (dot in 0 until state.total) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (dot == state.step) 10.dp else 7.dp)
                        .clip(CircleShape)
                        .background(if (dot == state.step) BloomPink else CrustGrey)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecondaryButton(
                text = stringResource(R.string.cta_back),
                onClick = onBack,
                enabled = state.step > 0,
                modifier = Modifier.weight(1f)
            )
            if (state.isLast) {
                PrimaryButton(
                    text = stringResource(R.string.cta_play_now),
                    onClick = onPlayNow,
                    modifier = Modifier.weight(1.3f)
                )
            } else {
                PrimaryButton(
                    text = stringResource(R.string.cta_next),
                    onClick = onNext,
                    modifier = Modifier.weight(1.3f)
                )
            }
        }
    }
}

@Composable
private fun MiniBoard(cells: List<MiniCell>, reducedMotion: Boolean) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(SurfaceWhite.copy(alpha = 0.94f))
                .border(2.dp, BloomPink.copy(alpha = 0.3f), shape)
                .padding(8.dp)
        ) {
            for (row in 0 until 3) {
                Row {
                    for (col in 0 until 3) {
                        val cell = cells.getOrNull(row * 3 + col)
                        if (cell == null) {
                            Spacer(modifier = Modifier.size(54.dp))
                        } else {
                            BloomTile(
                                id = (row * 3 + col).toLong(),
                                bloom = cell.bloom,
                                crustLayers = cell.crustLayers,
                                tileSize = 54.dp,
                                selected = cell.highlighted,
                                hinted = false,
                                bursting = false,
                                wilting = false,
                                colourBlindMarkers = false,
                                reducedMotion = reducedMotion
                            )
                        }
                    }
                }
            }
        }
    }
}
