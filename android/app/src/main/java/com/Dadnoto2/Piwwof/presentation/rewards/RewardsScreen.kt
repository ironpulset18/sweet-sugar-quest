package com.Dadnoto2.Piwwof.presentation.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.CoinGold
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SuccessMint
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.ClayCard
import com.Dadnoto2.Piwwof.core.ui.CoinBadge
import com.Dadnoto2.Piwwof.core.ui.EmptyState
import com.Dadnoto2.Piwwof.core.ui.PageFrame
import com.Dadnoto2.Piwwof.core.ui.PrimaryButton
import com.Dadnoto2.Piwwof.core.ui.spriteResFor
import com.Dadnoto2.Piwwof.presentation.dialog.RewardOpenedDialog

@Composable
fun RewardsRoute(
    reducedMotion: Boolean,
    onMenu: () -> Unit
) {
    val viewModel: RewardsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    RewardsScreen(
        state = state,
        reducedMotion = reducedMotion,
        onOpenChest = viewModel::openChest,
        onDismissOpened = viewModel::dismissOpened,
        onMenu = onMenu
    )
}

@Composable
fun RewardsScreen(
    state: RewardsUiState,
    reducedMotion: Boolean,
    onOpenChest: (Int) -> Unit,
    onDismissOpened: () -> Unit,
    onMenu: () -> Unit
) {
    PageFrame(
        title = stringResource(R.string.rewards_title),
        accent = CoinGold,
        reducedMotion = reducedMotion,
        onMenu = onMenu,
        trailing = { CoinBadge(coins = state.coins) }
    ) {
        if (state.neverEarned) {
            EmptyState(
                iconRes = R.drawable.ic_coin,
                title = stringResource(R.string.rewards_empty_title),
                message = stringResource(R.string.rewards_empty_body),
                accent = CoinGold
            )
        }
        state.chests.forEach { entry ->
            ChestRow(entry = entry, onOpenChest = onOpenChest)
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.rewards_disclaimer),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (state.openedBloomNameRes != null) {
        RewardOpenedDialog(
            bloomNameRes = state.openedBloomNameRes,
            spriteSlot = state.openedSpriteSlot,
            onClose = onDismissOpened
        )
    }
}

@Composable
private fun ChestRow(entry: ChestUi, onOpenChest: (Int) -> Unit) {
    val accent = if (entry.owned) SuccessMint else CoinGold
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
            Image(
                painter = painterResource(spriteResFor(entry.chest.spriteSlot)),
                contentDescription = stringResource(R.string.cd_chest),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(entry.chest.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(
                            if (entry.owned) R.drawable.ic_check else R.drawable.ic_coin
                        ),
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (entry.owned) {
                            stringResource(R.string.rewards_planted)
                        } else {
                            stringResource(R.string.rewards_cost, entry.chest.cost)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            if (entry.owned) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = SuccessMint,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                PrimaryButton(
                    text = stringResource(R.string.cta_open),
                    onClick = { onOpenChest(entry.chest.id) },
                    enabled = entry.affordable,
                    accentFrom = CoinGold,
                    accentTo = GrapePurple,
                    modifier = Modifier.height(56.dp)
                )
            }
        }
    }
}
