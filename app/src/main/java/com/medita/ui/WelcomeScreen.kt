package com.medita.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.medita.R

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var animateOnLoad by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { animateOnLoad = true }

        val titleAlpha by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0f,
            animationSpec = tween(durationMillis = 500),
            label = "titleAlpha"
        )
        val titleOffset by animateDpAsState(
            targetValue = if (animateOnLoad) 0.dp else 12.dp,
            animationSpec = tween(durationMillis = 500),
            label = "titleOffset"
        )
        val subtitleAlpha by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0f,
            animationSpec = tween(durationMillis = 500, delayMillis = 100),
            label = "subtitleAlpha"
        )
        val subtitleOffset by animateDpAsState(
            targetValue = if (animateOnLoad) 0.dp else 12.dp,
            animationSpec = tween(durationMillis = 500, delayMillis = 100),
            label = "subtitleOffset"
        )
        val imageAlpha by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0f,
            animationSpec = tween(durationMillis = 500, delayMillis = 200),
            label = "imageAlpha"
        )
        val imageOffset by animateDpAsState(
            targetValue = if (animateOnLoad) 0.dp else 20.dp,
            animationSpec = tween(durationMillis = 500, delayMillis = 200),
            label = "imageOffset"
        )
        val welcomeAlpha by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0f,
            animationSpec = tween(durationMillis = 500, delayMillis = 300),
            label = "welcomeAlpha"
        )
        val welcomeOffset by animateDpAsState(
            targetValue = if (animateOnLoad) 0.dp else 14.dp,
            animationSpec = tween(durationMillis = 500, delayMillis = 300),
            label = "welcomeOffset"
        )
        val bodyAlpha by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0f,
            animationSpec = tween(durationMillis = 500, delayMillis = 400),
            label = "bodyAlpha"
        )
        val bodyOffset by animateDpAsState(
            targetValue = if (animateOnLoad) 0.dp else 14.dp,
            animationSpec = tween(durationMillis = 500, delayMillis = 400),
            label = "bodyOffset"
        )
        Text(
            "Meditate",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .offset(y = titleOffset)
                .graphicsLayer { alpha = titleAlpha }
        )
        Text(
            "Find peace in the present moment",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .offset(y = subtitleOffset)
                .graphicsLayer { alpha = subtitleAlpha }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.welcome_img),
            contentDescription = "Meditation illustration",
            modifier = Modifier
                .offset(y = imageOffset)
                .graphicsLayer { alpha = imageAlpha }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Welcome to Your Journey",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset(y = welcomeOffset)
                .graphicsLayer { alpha = welcomeAlpha }
        )
        Text(
            "Discover inner peace through guided meditation, mindful, breathing, and gentle awareness practices.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = bodyOffset)
                .graphicsLayer { alpha = bodyAlpha }
        )
        Spacer(modifier = Modifier.height(32.dp))
        val gradientBrush = Brush.horizontalGradient(
            colors = listOf(Color(0xFF9BF3B2), Color(0xFF57C983))
        )
        val buttonScale by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0.85f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "buttonScale"
        )
        val buttonAlpha by animateFloatAsState(
            targetValue = if (animateOnLoad) 1f else 0f,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "buttonAlpha"
        )

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .graphicsLayer {
                    scaleX = buttonScale
                    scaleY = buttonScale
                    alpha = buttonAlpha
                },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White.copy(alpha = 0.6f)
            ),
            contentPadding = PaddingValues()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Begin Your Exercise",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
