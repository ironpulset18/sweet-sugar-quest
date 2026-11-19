package com.Dadnoto2.Piwwof.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.Dadnoto2.Piwwof.core.theme.BloomPink
import com.Dadnoto2.Piwwof.core.theme.BloomPinkDeep
import com.Dadnoto2.Piwwof.core.theme.GrapePurple
import com.Dadnoto2.Piwwof.core.theme.SurfaceWhite
import com.Dadnoto2.Piwwof.core.theme.TextDisabled
import com.Dadnoto2.Piwwof.core.theme.TextOnAccent
import com.Dadnoto2.Piwwof.core.theme.TextPrimary
import com.Dadnoto2.Piwwof.core.theme.TrackMuted

private val PrimaryShape = RoundedCornerShape(20.dp)
private val SecondaryShape = RoundedCornerShape(18.dp)

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIconRes: Int? = null,
    leadingIsImage: Boolean = false,
    accentFrom: Color = BloomPink,
    accentTo: Color = BloomPinkDeep,
    state: String? = null
) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 900f),
        label = "primaryScale"
    )
    val brush = if (enabled) {
        Brush.horizontalGradient(listOf(accentFrom, accentTo))
    } else {
        Brush.horizontalGradient(listOf(TrackMuted, TrackMuted))
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactions,
        shape = PrimaryShape,
        contentPadding = PaddingValues(horizontal = 18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            contentColor = TextOnAccent,
            disabledContentColor = TextDisabled
        ),
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = 56.dp)
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = PrimaryShape,
                ambientColor = accentFrom,
                spotColor = accentFrom
            )
            .background(brush = brush, shape = PrimaryShape)
            .semantics { if (state != null) stateDescription = state }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIconRes != null) {
                if (leadingIsImage) {
                    Image(
                        painter = painterResource(leadingIconRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(leadingIconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIconRes: Int? = null,
    leadingIsImage: Boolean = false
) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "secondaryScale"
    )
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactions,
        shape = SecondaryShape,
        border = BorderStroke(2.dp, if (enabled) GrapePurple.copy(alpha = 0.45f) else TrackMuted),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SurfaceWhite.copy(alpha = 0.82f),
            contentColor = TextPrimary,
            disabledContainerColor = TrackMuted.copy(alpha = 0.5f),
            disabledContentColor = TextDisabled
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
            .scale(scale)
            .defaultMinSize(minHeight = 48.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIconRes != null) {
                if (leadingIsImage) {
                    Image(
                        painter = painterResource(leadingIconRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(leadingIconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}
