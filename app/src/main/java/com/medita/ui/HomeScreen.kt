package com.medita.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.SystemClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.max

private enum class TimerState { Idle, Running, Paused, Completed }
private enum class HomeTab { Music, Timer, Themes }

private data class ThemeOption(
    val title: String,
    val subtitle: String,
    val background: Brush,
    val accent: Color
)

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val alertUri = remember { RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) }
    val mediaPlayer = remember {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(context, alertUri)
            prepare()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.stopSafely()
            mediaPlayer.release()
        }
    }

    val themes = remember {
        listOf(
            ThemeOption(
                "Ocean",
                "Calm swells",
                Brush.verticalGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))),
                Color(0xFF00838F)
            ),
            ThemeOption(
                "Forest",
                "Gentle breeze",
                Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))),
                Color(0xFF2E7D32)
            ),
            ThemeOption(
                "Starry Sky",
                "Midnight hush",
                Brush.verticalGradient(listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9))),
                Color(0xFF512DA8)
            )
        )
    }

    var selectedTheme by remember { mutableStateOf(themes.first()) }
    val accentColor = selectedTheme.accent
    val primaryGradient = Brush.horizontalGradient(
        listOf(Color(0xFF9BF3B2), Color(0xFF57C983))
    )
    var selectedTab by remember { mutableStateOf(HomeTab.Timer) }
    var totalDurationMillis by remember { mutableLongStateOf(5 * 60_000L) }
    var remainingMillis by remember { mutableLongStateOf(totalDurationMillis) }
    var timerState by remember { mutableStateOf(TimerState.Idle) }
    var showCustomDialog by remember { mutableStateOf(false) }
    val presets = listOf(1, 3, 5, 10, 15)

    fun startTimer(resetIfNeeded: Boolean = false) {
        if (resetIfNeeded || timerState == TimerState.Completed) {
            remainingMillis = totalDurationMillis
        } else if (timerState == TimerState.Idle) {
            remainingMillis = totalDurationMillis
        }
        timerState = TimerState.Running
    }

    fun pauseTimer() {
        timerState = TimerState.Paused
    }

    fun resetTimer() {
        timerState = TimerState.Idle
        remainingMillis = totalDurationMillis
        mediaPlayer.stopSafely()
    }

    fun completeTimer() {
        timerState = TimerState.Completed
        remainingMillis = 0L
        mediaPlayer.seekTo(0)
        mediaPlayer.start()
    }

    val timerStateUpdated by rememberUpdatedState(timerState)
    LaunchedEffect(timerStateUpdated, totalDurationMillis) {
        if (timerStateUpdated == TimerState.Running) {
            var lastTick = SystemClock.elapsedRealtime()
            while (isActive && timerStateUpdated == TimerState.Running && remainingMillis > 0) {
                delay(200L)
                val now = SystemClock.elapsedRealtime()
                val delta = now - lastTick
                lastTick = now
                remainingMillis = max(0L, remainingMillis - delta)
            }
            if (remainingMillis <= 0 && timerState == TimerState.Running) {
                completeTimer()
            }
        }
    }

    var backgroundTimestamp by remember { mutableStateOf<Long?>(null) }
    DisposableEffect(timerState == TimerState.Running) {
        val lifecycleOwner = ProcessLifecycleOwner.get()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (timerState == TimerState.Running) {
                        backgroundTimestamp = SystemClock.elapsedRealtime()
                    }
                }

                Lifecycle.Event.ON_START -> {
                    val last = backgroundTimestamp ?: return@LifecycleEventObserver
                    val elapsed = SystemClock.elapsedRealtime() - last
                    backgroundTimestamp = null
                    if (timerState == TimerState.Running) {
                        remainingMillis = max(0L, remainingMillis - elapsed)
                        if (remainingMillis == 0L) {
                            completeTimer()
                        }
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF7FFFB),
                            Color.White,
                            accentColor.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            if (selectedTab == HomeTab.Timer) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ToggleTabs(
                        selectedTab = selectedTab,
                        accentColor = accentColor,
                        onTabSelected = { selectedTab = it }
                    )
                    TimerTabContent(
                        remainingMillis = remainingMillis,
                        timerState = timerState,
                        selectedDurationMillis = totalDurationMillis,
                        presets = presets,
                        onPresetSelected = {
                            totalDurationMillis = it * 60_000L
                            remainingMillis = totalDurationMillis
                            timerState = TimerState.Idle
                        },
                        onCustomTime = { showCustomDialog = true },
                        onStart = { startTimer(resetIfNeeded = timerState != TimerState.Running) },
                        onPause = ::pauseTimer,
                        onReset = ::resetTimer,
                        accentColor = accentColor,
                        primaryGradient = primaryGradient,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ToggleTabs(
                        selectedTab = selectedTab,
                        accentColor = accentColor,
                        onTabSelected = { selectedTab = it }
                    )
                    when (selectedTab) {
                        HomeTab.Themes -> ThemeTabContent(
                            themes = themes,
                            selectedTheme = selectedTheme,
                            onThemeSelected = { selectedTheme = it },
                            accentColor = accentColor
                        )
                        HomeTab.Music -> MusicTabContent(accentColor = accentColor)
                        else -> Unit
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        CustomDurationDialog(
            initialMinutes = (totalDurationMillis / 60_000L).toInt(),
            initialSeconds = ((totalDurationMillis / 1000L) % 60).toInt(),
            onDismiss = { showCustomDialog = false },
            onConfirm = { minutes, seconds ->
                val total = (minutes * 60L + seconds) * 1000L
                if (total > 0) {
                    totalDurationMillis = total
                    remainingMillis = total
                    timerState = TimerState.Idle
                }
                showCustomDialog = false
            }
        )
    }
}

@Composable
private fun ToggleTabs(
    selectedTab: HomeTab,
    accentColor: Color,
    onTabSelected: (HomeTab) -> Unit
) {
    val tabs = listOf(
        HomeTab.Music to "MUSIC",
        HomeTab.Timer to "TIMER",
        HomeTab.Themes to "THEMES"
    )
    Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
        tabs.forEach { (tab, label) ->
            val isActive = selectedTab == tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTabSelected(tab) }
            ) {
                Text(
                    text = label,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isActive) accentColor else Color(0xFF9AA5A0)
                )
                AnimatedVisibility(
                    visible = isActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .height(2.dp)
                            .width(36.dp)
                            .background(accentColor, shape = RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeCarousel(
    themes: List<ThemeOption>,
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(themes) { theme ->
            val isActive = theme == selectedTheme
            Card(
                modifier = Modifier
                    .size(width = 160.dp, height = 200.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                elevation = CardDefaults.cardElevation(if (isActive) 10.dp else 4.dp),
                border = BorderStroke(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) theme.accent else Color.White.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(theme.background)
                        .clip(RoundedCornerShape(28.dp))
                        .padding(20.dp)
                        .clickable { onThemeSelected(theme) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            theme.title,
                            fontWeight = FontWeight.Bold,
                            color = theme.accent,
                            fontSize = 18.sp
                        )
                        Text(theme.subtitle, color = Color.Black.copy(alpha = 0.6f))
                        Text("ABOUT", fontWeight = FontWeight.SemiBold, color = theme.accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerTabContent(
    remainingMillis: Long,
    timerState: TimerState,
    selectedDurationMillis: Long,
    presets: List<Int>,
    onPresetSelected: (Int) -> Unit,
    onCustomTime: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    accentColor: Color,
    primaryGradient: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        CountdownDisplay(
            remainingMillis = remainingMillis,
            timerState = timerState,
            accentColor = accentColor,
            modifier = Modifier.weight(1f, fill = true)
        )
        PresetChipsRow(
            presets = presets,
            selectedDurationMillis = selectedDurationMillis,
            timerState = timerState,
            onPresetSelected = onPresetSelected,
            onCustomTime = onCustomTime,
            accentColor = accentColor
        )
        ControlButtons(
            timerState = timerState,
            onStart = onStart,
            onPause = onPause,
            onReset = onReset,
            primaryGradient = primaryGradient,
            accentColor = accentColor
        )
    }
}

@Composable
private fun PresetChipsRow(
    presets: List<Int>,
    selectedDurationMillis: Long,
    timerState: TimerState,
    onPresetSelected: (Int) -> Unit,
    onCustomTime: () -> Unit,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Duration presets", fontWeight = FontWeight.SemiBold, color = Color(0xFF1C1F1D))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            items(presets) { minutes ->
                val isActive = selectedDurationMillis == minutes * 60_000L && timerState != TimerState.Running
                ChipButton(
                    label = "$minutes min",
                    isActive = isActive,
                    onClick = { onPresetSelected(minutes) },
                    enabled = timerState != TimerState.Running,
                    accentColor = accentColor
                )
            }
            item {
                OutlinedButton(
                    onClick = onCustomTime,
                    enabled = timerState != TimerState.Running,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
                ) {
                    Text("Custom")
                }
            }
        }
    }
}

@Composable
private fun ChipButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    accentColor: Color
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) accentColor else Color(0xFFF0F9F2),
            contentColor = if (isActive) Color.White else accentColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(label)
    }
}

@Composable
private fun ThemeTabContent(
    themes: List<ThemeOption>,
    selectedTheme: ThemeOption,
    onThemeSelected: (ThemeOption) -> Unit,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Choose your vibe",
            fontWeight = FontWeight.SemiBold,
            color = accentColor
        )
        ThemeCarousel(
            themes = themes,
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected
        )
        Text(
            "Each theme adapts gradients, typography, and button accents for the entire experience.",
            color = Color(0xFF5F6461)
        )
    }
}

@Composable
private fun MusicTabContent(accentColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Soundscapes", fontWeight = FontWeight.SemiBold, color = accentColor)
        Text(
            "Curated breathing playlists are coming soon. Save your favorite ocean, forest, or starry sky ambience here.",
            color = Color(0xFF5F6461)
        )
    }
}

@Composable
private fun CountdownDisplay(
    remainingMillis: Long,
    timerState: TimerState,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val totalSeconds = remainingMillis / 1000
    val minutesText = "%02d".format(totalSeconds / 60)
    val secondsText = "%02d".format(totalSeconds % 60)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = minutesText,
            fontWeight = FontWeight.Black,
            color = accentColor,
            fontSize = 80.sp
        )
        Text(
            text = secondsText,
            fontWeight = FontWeight.Black,
            color = accentColor,
            fontSize = 80.sp
        )
        Text(
            text = when (timerState) {
                TimerState.Running -> "Counting down"
                TimerState.Paused -> "Paused"
                TimerState.Completed -> "Complete"
                else -> "Ready"
            },
            color = Color(0xFF6F7975)
        )
    }
}

@Composable
private fun ControlButtons(
    timerState: TimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    primaryGradient: Brush,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (timerState == TimerState.Running) onPause() else onStart()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues()
            ) {
                if (timerState == TimerState.Running) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(
                                width = 1.dp,
                                color = accentColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Pause", color = accentColor, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primaryGradient, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Start", fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
            ) {
                Text("Reset")
            }
        }
        Text("Timer continues even if you leave the app", color = Color(0xFF6F7975), fontSize = 12.sp)
    }
}

@Composable
private fun CustomDurationDialog(
    initialMinutes: Int,
    initialSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var minutesInput by remember { mutableStateOf(initialMinutes.toString()) }
    var secondsInput by remember { mutableStateOf(initialSeconds.toString().padStart(2, '0')) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val minutes = minutesInput.toIntOrNull() ?: 0
                val seconds = secondsInput.toIntOrNull()?.coerceIn(0, 59) ?: 0
                onConfirm(minutes, seconds)
            }) {
                Text("Set")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Custom duration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minutesInput,
                    onValueChange = { minutesInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Minutes") }
                )
                OutlinedTextField(
                    value = secondsInput,
                    onValueChange = { secondsInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Seconds") }
                )
            }
        }
    )
}

private fun MediaPlayer.stopSafely() {
    if (isPlaying) {
        stop()
        prepare()
    }
}