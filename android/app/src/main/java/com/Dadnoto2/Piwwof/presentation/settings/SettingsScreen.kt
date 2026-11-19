package com.Dadnoto2.Piwwof.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.DangerRose
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.theme.TrackMuted
import com.Dadnoto2.Piwwof.core.ui.ClayCard
import com.Dadnoto2.Piwwof.core.ui.PageFrame
import com.Dadnoto2.Piwwof.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsRoute(
    reducedMotion: Boolean,
    onMenu: () -> Unit
) {
    val viewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        reducedMotion = reducedMotion,
        onColourBlind = viewModel::setColourBlindMarkers,
        onReducedMotion = viewModel::setReducedMotion,
        onHintGlow = viewModel::setHintGlow,
        onRequestReset = viewModel::requestReset,
        onCancelReset = viewModel::cancelReset,
        onConfirmReset = {
            viewModel.confirmReset()
            onMenu()
        },
        onMenu = onMenu
    )
}

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    reducedMotion: Boolean,
    onColourBlind: (Boolean) -> Unit,
    onReducedMotion: (Boolean) -> Unit,
    onHintGlow: (Boolean) -> Unit,
    onRequestReset: () -> Unit,
    onCancelReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onMenu: () -> Unit
) {
    PageFrame(
        title = stringResource(R.string.settings_title),
        accent = GrapePurple,
        reducedMotion = reducedMotion,
        onMenu = onMenu
    ) {
        SettingRow(
            title = stringResource(R.string.settings_colourblind_title),
            description = stringResource(R.string.settings_colourblind_desc),
            checked = state.options.colourBlindMarkers,
            onCheckedChange = onColourBlind
        )
        SettingRow(
            title = stringResource(R.string.settings_motion_title),
            description = stringResource(R.string.settings_motion_desc),
            checked = state.options.reducedMotion,
            onCheckedChange = onReducedMotion
        )
        SettingRow(
            title = stringResource(R.string.settings_hint_title),
            description = stringResource(R.string.settings_hint_desc),
            checked = state.options.hintGlow,
            onCheckedChange = onHintGlow
        )

        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            accent = DangerRose,
            corner = 22.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_reset_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.settings_reset_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                TextButton(
                    onClick = onRequestReset,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cta_reset),
                        style = MaterialTheme.typography.labelLarge,
                        color = DangerRose
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_about_title),
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.settings_about_age),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = stringResource(R.string.settings_about_offline),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }

    if (state.confirmingReset) {
        ConfirmResetDialog(
            onConfirm = onConfirmReset,
            onCancel = onCancelReset
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val stateText = if (checked) {
        stringResource(R.string.sd_on)
    } else {
        stringResource(R.string.sd_off)
    }
    ClayCard(
        modifier = Modifier.fillMaxWidth(),
        accent = BloomPink,
        corner = 22.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = BloomPink,
                    uncheckedThumbColor = SurfaceWhite,
                    uncheckedTrackColor = TrackMuted
                ),
                modifier = Modifier.semantics { stateDescription = stateText }
            )
        }
    }
}
