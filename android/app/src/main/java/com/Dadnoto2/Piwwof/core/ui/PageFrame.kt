package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.theme.BlushBase
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.SurfaceAlt
import com.Dadnoto2.Piwwof.core.theme.SurfaceSunken
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TextSecondary

@Composable
fun PageFrame(
    title: String,
    reducedMotion: Boolean,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    accent: Color = BloomPink,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ScreenBackground(
        overlay = listOf(
            BlushBase.copy(alpha = 0.9f),
            SurfaceSunken.copy(alpha = 0.84f),
            SurfaceAlt.copy(alpha = 0.94f)
        ),
        accent = accent,
        animated = !reducedMotion,
        imageAlpha = 0.14f,
        baseColor = BlushBase,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                if (trailing != null) {
                    trailing()
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
            Spacer(modifier = Modifier.height(10.dp))
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
