package com.Dadnoto2.Piwwof.presentation.missions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.CoinGold
import com.Dadnoto2.Piwwof.core.theme.SuccessMint
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.ClayCard
import com.Dadnoto2.Piwwof.core.ui.CoinBadge
import com.Dadnoto2.Piwwof.core.ui.EmptyState
import com.Dadnoto2.Piwwof.core.ui.PageFrame
import com.Dadnoto2.Piwwof.core.ui.PrimaryButton
import com.Dadnoto2.Piwwof.core.ui.ProgressBarThin
import com.Dadnoto2.Piwwof.core.ui.spriteResFor
import com.Dadnoto2.Piwwof.domain.model.MissionCard

@Composable
fun MissionsRoute(
    reducedMotion: Boolean,
    onMenu: () -> Unit
) {
    val viewModel: MissionsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    MissionsScreen(
        state = state,
        reducedMotion = reducedMotion,
        onClaim = viewModel::claim,
        onNewSet = viewModel::drawNewSet,
        onMenu = onMenu
    )
}

@Composable
fun MissionsScreen(
    state: MissionsUiState,
    reducedMotion: Boolean,
    onClaim: (Int) -> Unit,
    onNewSet: () -> Unit,
    onMenu: () -> Unit
) {
    PageFrame(
        title = stringResource(R.string.missions_title),
        accent = SuccessMint,
        reducedMotion = reducedMotion,
        onMenu = onMenu,
        trailing = { CoinBadge(coins = state.coins) }
    ) {
        if (state.allClaimed) {
            Spacer(modifier = Modifier.weight(1f))
            EmptyState(
                iconRes = R.drawable.ic_gift,
                title = stringResource(R.string.missions_empty_title),
                message = stringResource(R.string.missions_empty_body),
                accent = SuccessMint,
                actionText = stringResource(R.string.cta_new_set),
                onAction = onNewSet
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            state.cards.forEach { card ->
                MissionRow(card = card, onClaim = onClaim)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MissionRow(card: MissionCard, onClaim: (Int) -> Unit) {
    val accent = if (card.complete) SuccessMint else CoinGold
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        accent = accent,
        corner = 24.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                val bloom = card.mission.bloom
                if (bloom != null) {
                    Image(
                        painter = painterResource(spriteResFor(bloom)),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_target),
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(card.mission.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(card.mission.descRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                ProgressBarThin(
                    progress = card.progress.toFloat() / card.mission.target.toFloat(),
                    accent = accent
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.label_fraction,
                            card.progress,
                            card.mission.target
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.missions_reward, card.mission.reward),
                        style = MaterialTheme.typography.labelSmall,
                        color = CoinGold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (card.claimed) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = SuccessMint,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.missions_claimed),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessMint
                    )
                }
            } else {
                PrimaryButton(
                    text = stringResource(R.string.cta_claim),
                    onClick = { onClaim(card.mission.id) },
                    enabled = card.complete,
                    accentFrom = SuccessMint,
                    accentTo = CoinGold,
                    modifier = Modifier.width(104.dp)
                )
            }
        }
    }
}
