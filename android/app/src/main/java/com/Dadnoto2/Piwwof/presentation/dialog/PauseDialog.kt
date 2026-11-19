package com.Dadnoto2.Piwwof.presentation.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.theme.BloomPinkDeep
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary
import com.Dadnoto2.Piwwof.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    seed: Long,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        properties = DialogProperties(dismissOnClickOutside = true, dismissOnBackPress = true),
        shape = RoundedCornerShape(26.dp),
        containerColor = SurfaceWhite,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.pause_seed, seed),
                    style = MaterialTheme.typography.labelSmall
                )
                SecondaryButton(
                    text = stringResource(R.string.cta_restart),
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.cta_resume),
                    style = MaterialTheme.typography.labelLarge,
                    color = BloomPinkDeep
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.cta_menu),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
            }
        }
    )
}
