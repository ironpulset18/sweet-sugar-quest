package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.core.theme.CoinGold
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextPrimary

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    val shown by animateIntAsState(targetValue = coins, label = "coinCount")
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 96.dp, minHeight = 40.dp)
            .height(40.dp)
            .clip(shape)
            .background(if (onDark) SurfaceWhite.copy(alpha = 0.18f) else SurfaceWhite)
            .border(2.dp, CoinGold.copy(alpha = 0.55f), shape)
            .padding(horizontal = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_coin),
            contentDescription = stringResource(R.string.label_coins),
            tint = CoinGold,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = groupedNumber(shown),
            style = MaterialTheme.typography.titleMedium,
            color = if (onDark) SurfaceWhite else TextPrimary
        )
    }
}

@Composable
fun SectionTitle(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (onDark) SurfaceWhite.copy(alpha = 0.86f) else TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent)
        )
    }
}
