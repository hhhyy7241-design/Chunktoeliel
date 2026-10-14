package cu.todus.download.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cu.todus.download.ui.theme.AppTheme

@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val extendedColors = AppTheme.colors
    val surfaceColor = extendedColors.surfaceCard
    val borderColor = extendedColors.surfaceBorder.copy(alpha = if (extendedColors.isDark) 0.5f else 0.8f)

    Surface(
        modifier = modifier
            .border(BorderStroke(1.dp, borderColor), shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            ),
        shape = shape,
        color = surfaceColor,
        shadowElevation = if (extendedColors.isDark) 4.dp else 2.dp,
        tonalElevation = 1.dp,
        content = content
    )
}

// Backward compatibility for GlassCard
@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ModernCard(modifier = modifier, content = content)
}

@Composable
fun GradientButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val extendedColors = AppTheme.colors
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    val bgModifier = if (enabled) {
        Modifier
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = primaryColor.copy(alpha = 0.35f))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        primaryColor,
                        if (extendedColors.isDark) MaterialTheme.colorScheme.secondary else primaryColor
                    )
                )
            )
    } else {
        Modifier.background(extendedColors.surfaceBorder)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(bgModifier)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) onPrimaryColor else extendedColors.textMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                color = if (enabled) onPrimaryColor else extendedColors.textMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StatusPill(
    text: String,
    detail: String? = null,
    active: Boolean = true,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val extendedColors = AppTheme.colors
    val statusColor = if (active) extendedColors.success else extendedColors.warning
    val bgColor = statusColor.copy(alpha = if (extendedColors.isDark) 0.15f else 0.12f)
    val borderColor = statusColor.copy(alpha = if (extendedColors.isDark) 0.35f else 0.45f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(statusColor)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = statusColor,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        detail?.let {
            Text(
                text = "  •  $it",
                color = extendedColors.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

data class NavBarItem(
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun FloatingBottomBar(
    items: List<NavBarItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = AppTheme.colors
    val barBackground = if (extendedColors.isDark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val barBorder = extendedColors.surfaceBorder.copy(alpha = 0.7f)

    Surface(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(26.dp),
        color = barBackground,
        border = BorderStroke(1.dp, barBorder),
        shadowElevation = if (extendedColors.isDark) 12.dp else 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = selectedIndex == index
                val activeBg = MaterialTheme.colorScheme.primaryContainer
                val activeTint = MaterialTheme.colorScheme.primary
                val inactiveTint = extendedColors.textMuted

                val bg by animateColorAsState(
                    if (selected) activeBg else Color.Transparent,
                    tween(250),
                    label = "nav_bg"
                )
                val tint by animateColorAsState(
                    if (selected) activeTint else inactiveTint,
                    tween(250),
                    label = "nav_tint"
                )
                val pillWidth by animateDpAsState(
                    if (selected) 24.dp else 12.dp,
                    tween(250, easing = FastOutSlowInEasing),
                    label = "nav_pill"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(bg)
                        .clickable { onSelected(index) }
                        .padding(horizontal = pillWidth, vertical = 8.dp)
                        .testTag("nav_tab_${item.label.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ) {
                                    Text(
                                        text = item.badgeCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = item.label,
                        color = tint,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val extendedColors = AppTheme.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = title.uppercase(),
                color = extendedColors.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
        ModernCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(vertical = 4.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconBgColor: Color = MaterialTheme.colorScheme.primaryContainer,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val extendedColors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (extendedColors.isDark) iconBgColor.copy(alpha = 0.25f) else iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = extendedColors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = extendedColors.textMuted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            val chevronColor = extendedColors.textMuted.copy(alpha = 0.6f)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(16.dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width * 0.35f, size.height * 0.2f)
                    lineTo(size.width * 0.65f, size.height * 0.5f)
                    lineTo(size.width * 0.35f, size.height * 0.8f)
                }
                drawPath(
                    path = path,
                    color = chevronColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    val extendedColors = AppTheme.colors
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = if (extendedColors.isDark) 0.15f else 0.1f))
                .border(
                    1.dp,
                    primaryColor.copy(alpha = if (extendedColors.isDark) 0.35f else 0.25f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = title,
            color = extendedColors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = extendedColors.textMuted,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
        action?.let {
            Spacer(Modifier.height(18.dp))
            it()
        }
    }
}
