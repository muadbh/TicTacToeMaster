package com.tictactoemaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.ui.theme.AppTheme

/** Navy/purple/blue gradient background with soft glows. Content is kept clear of system bars. */
@Composable
fun ScreenBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val c = AppTheme.colors
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c.backgroundTop, c.backgroundBottom)))
    ) {
        val glowAlpha = if (c.isDark) 0.35f else 0.18f
        Box(
            Modifier
                .size(300.dp)
                .offset(x = (-110).dp, y = (-90).dp)
                .background(
                    Brush.radialGradient(listOf(c.accentStart.copy(alpha = glowAlpha), Color.Transparent)),
                    CircleShape
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .size(320.dp)
                .offset(x = 120.dp, y = 110.dp)
                .background(
                    Brush.radialGradient(listOf(c.accentEnd.copy(alpha = glowAlpha), Color.Transparent)),
                    CircleShape
                )
        )
        Box(Modifier.fillMaxSize().safeDrawingPadding(), content = content)
    }
}

/** Large rounded purple-to-blue button. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val c = AppTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .heightIn(min = 56.dp)
            .shadow(8.dp, shape, ambientColor = c.accentStart, spotColor = c.accentStart)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(c.accentStart, c.accentEnd)))
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/** Secondary button: translucent card with a border. */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val c = AppTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier
            .heightIn(min = 56.dp)
            .clip(shape)
            .background(c.card)
            .border(1.5.dp, c.cardBorder, shape)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = c.textPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val c = AppTheme.colors
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier
            .clip(shape)
            .background(c.card)
            .border(1.dp, c.cardBorder, shape)
            .padding(18.dp),
        content = content
    )
}

/** Back arrow + centred title + optional trailing action (48dp slot). */
@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    val c = AppTheme.colors
    Row(
        modifier.fillMaxWidth().heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = c.textPrimary
            )
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f).semantics { heading() },
            color = c.textPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { trailing() }
    }
}
