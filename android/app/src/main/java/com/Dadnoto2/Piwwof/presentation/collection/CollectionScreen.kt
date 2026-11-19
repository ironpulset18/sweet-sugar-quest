package com.Dadnoto2.Piwwof.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BloomPinkDeep
import com.Dadnoto2.Piwwof.core.theme.CrustGrey
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.EmptyState
import com.Dadnoto2.Piwwof.core.ui.PageFrame
import com.Dadnoto2.Piwwof.core.ui.spriteResFor

@Composable
fun CollectionRoute(
    reducedMotion: Boolean,
    onMenu: () -> Unit
) {
    val viewModel: CollectionViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectionScreen(state = state, reducedMotion = reducedMotion, onMenu = onMenu)
}

@Composable
fun CollectionScreen(
    state: CollectionUiState,
    reducedMotion: Boolean,
    onMenu: () -> Unit
) {
    PageFrame(
        title = stringResource(R.string.collection_title),
        subtitle = stringResource(R.string.collection_count, state.owned, state.total),
        accent = BloomPinkDeep,
        reducedMotion = reducedMotion,
        onMenu = onMenu
    ) {
        Text(
            text = if (state.tier > 0) {
                stringResource(R.string.collection_tier, state.tier, state.tier)
            } else {
                stringResource(R.string.collection_tier_zero)
            },
            style = MaterialTheme.typography.labelSmall,
            color = BloomPinkDeep,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.owned == 0) {
            Spacer(modifier = Modifier.weight(1f))
            EmptyState(
                iconRes = R.drawable.ic_sprout,
                title = stringResource(R.string.collection_empty_title),
                message = stringResource(R.string.collection_empty_body),
                accent = BloomPinkDeep
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(count = state.slots.size) { index ->
                    BloomSlotCard(slot = state.slots[index])
                }
            }
        }
    }
}

@Composable
private fun BloomSlotCard(slot: BloomSlot) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .clip(shape)
            .background(if (slot.owned) SurfaceWhite else CrustGrey.copy(alpha = 0.35f))
            .border(
                2.dp,
                if (slot.owned) BloomPinkDeep.copy(alpha = 0.3f) else CrustGrey,
                shape
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(spriteResFor(slot.bloom.bloom)),
                contentDescription = if (slot.owned) stringResource(slot.bloom.nameRes) else null,
                contentScale = ContentScale.Fit,
                alpha = if (slot.owned) 1f else 0.12f,
                modifier = Modifier.size(52.dp)
            )
            if (!slot.owned) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = stringResource(R.string.cd_locked),
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (slot.owned) stringResource(slot.bloom.nameRes) else stringResource(R.string.cd_locked),
            style = MaterialTheme.typography.labelSmall,
            color = if (slot.owned) TextPrimary else TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
