package me.huizengek.kpninteractievetv.ui.items

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceBorder
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceGlow
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.ClickableSurfaceShape
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import me.huizengek.kpninteractievetv.util.alpha

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ItemContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.surface,
    focusColor: Color = color.alpha(0.5f),
    glow: ClickableSurfaceGlow = ClickableSurfaceDefaults.glow(
        glow = Glow(
            elevation = 4.dp,
            elevationColor = focusColor
        )
    ),
    border: ClickableSurfaceBorder = ClickableSurfaceDefaults.border(
        focusedBorder = Border(
            BorderStroke(
                width = 8.dp,
                color = focusColor
            )
        )
    ),
    shape: ClickableSurfaceShape = ClickableSurfaceDefaults.shape(),
    scale: ClickableSurfaceScale = ClickableSurfaceDefaults.scale(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusRequester: FocusRequester? = null,
    content: @Composable (BoxScope.() -> Unit)
) = Surface(
    onClick = onClick,
    modifier = modifier
        .let { if (focusRequester == null) it else it.focusRequester(focusRequester = focusRequester) },
    onLongClick = onLongClick,
    enabled = enabled,
    glow = glow,
    border = border,
    shape = shape,
    scale = scale,
    colors = ClickableSurfaceDefaults.colors(containerColor = color),
    interactionSource = interactionSource,
    content = content
)
