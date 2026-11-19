package com.Dadnoto2.Piwwof.presentation.levelmap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BandBerry
import com.Dadnoto2.Piwwof.core.theme.BandCitrus
import com.Dadnoto2.Piwwof.core.theme.BandCocoa
import com.Dadnoto2.Piwwof.core.theme.CoinGold
import com.Dadnoto2.Piwwof.core.theme.CrustGrey
import com.Dadnoto2.Piwwof.core.theme.DangerRose
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.ClayCard
import com.Dadnoto2.Piwwof.core.ui.PageFrame
import com.Dadnoto2.Piwwof.core.ui.PrimaryButton
import com.Dadnoto2.Piwwof.core.ui.SectionTitle
import com.Dadnoto2.Piwwof.core.ui.spriteResFor
import com.Dadnoto2.Piwwof.domain.model.GardenBand

@Composable
fun LevelMapRoute(
    reducedMotion: Boolean,
    onPlayStage: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val viewModel: LevelMapViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    LevelMapScreen(
        state = state,
        reducedMotion = reducedMotion,
        onSelect = viewModel::select,
        onPlayStage = onPlayStage,
        onMenu = onMenu
    )
}

@Composable
fun LevelMapScreen(
    state: LevelMapUiState,
    reducedMotion: Boolean,
    onSelect: (Int) -> Unit,
    onPlayStage: (Int) -> Unit,
    onMenu: () -> Unit
) {
    PageFrame(
        title = stringResource(R.string.levels_title),
        subtitle = stringResource(R.string.levels_stars, state.totalStars, state.maxStars),
        accent = GrapePurple,
        reducedMotion = reducedMotion,
        onMenu = onMenu
    ) {
        GardenBand.entries.forEach { band ->
            val nodes = state.nodes.filter { it.band == band }
            if (nodes.isNotEmpty()) {
                SectionTitle(text = stringResource(bandNameRes(band)), accent = bandColor(band))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    nodes.forEach { node ->
                        StageNodeDot(
                            node = node,
                            accent = bandColor(band),
                            selected = node.index == state.selected,
                            onClick = { onSelect(node.index) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (state.lockedNotice != null) {
            Text(
                text = stringResource(R.string.levels_locked, state.lockedNotice - 1),
                style = MaterialTheme.typography.bodyMedium,
                color = DangerRose,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val selected = state.nodes.firstOrNull { it.index == state.selected }
        if (selected != null) {
            StageDetailCard(
                node = selected,
                accent = bandColor(selected.band),
                onPlayStage = onPlayStage
            )
        }
    }
}

@Composable
private fun StageNodeDot(
    node: StageNode,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lockState = if (node.unlocked) {
        stringResource(R.string.sd_unlocked)
    } else {
        stringResource(R.string.sd_locked)
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(if (node.unlocked) SurfaceWhite else CrustGrey.copy(alpha = 0.6f))
                .border(
                    width = if (selected) 3.dp else 2.dp,
                    color = if (node.unlocked) accent else CrustGrey,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
                .semantics { stateDescription = lockState },
            contentAlignment = Alignment.Center
        ) {
            if (node.unlocked) {
                Text(
                    text = node.index.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = stringResource(R.string.cd_locked),
                    tint = SurfaceWhite.copy(alpha = 0.85f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            for (slot in 0 until 3) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (slot < node.stars) CoinGold else CrustGrey.copy(alpha = 0.55f))
                )
            }
        }
    }
}

@Composable
private fun StageDetailCard(
    node: StageNode,
    accent: Color,
    onPlayStage: (Int) -> Unit
) {
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        accent = accent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(node.nameRes),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                node.goals.forEach { goal ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(spriteResFor(goal.bloom)),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = goal.target.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_crust),
                        contentDescription = stringResource(R.string.cd_crust),
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = node.crustGoal.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.levels_budget, node.moveBudget),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            if (node.best > 0) {
                Text(
                    text = stringResource(R.string.levels_best, node.best),
                    style = MaterialTheme.typography.labelSmall,
                    color = accent
                )
            }
            PrimaryButton(
                text = stringResource(R.string.cta_play_stage, node.index),
                onClick = { onPlayStage(node.index) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun bandNameRes(band: GardenBand): Int = when (band) {
    GardenBand.Berry -> R.string.band_berry
    GardenBand.Citrus -> R.string.band_citrus
    GardenBand.Cocoa -> R.string.band_cocoa
}

private fun bandColor(band: GardenBand): Color = when (band) {
    GardenBand.Berry -> BandBerry
    GardenBand.Citrus -> BandCitrus
    GardenBand.Cocoa -> BandCocoa
}
