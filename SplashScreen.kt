package com.tictactoemaster.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tictactoemaster.ui.components.AppLogo
import com.tictactoemaster.ui.components.ScreenBackground
import com.tictactoemaster.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun SplashScreen() {
    val c = AppTheme.colors
    val scaleAnim = remember { Animatable(0.6f) }
    val alphaAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch {
            scaleAnim.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        alphaAnim.animateTo(1f, tween(durationMillis = 600))
    }

    ScreenBackground {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo(
                Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = scaleAnim.value
                        scaleY = scaleAnim.value
                        alpha = alphaAnim.value
                    }
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Tic Tac Toe Master",
                modifier = Modifier.graphicsLayer { alpha = alphaAnim.value },
                color = c.textPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Think. Tap. Win.",
                modifier = Modifier.graphicsLayer { alpha = alphaAnim.value },
                color = c.textSecondary,
                fontSize = 16.sp
            )
        }
    }
}
