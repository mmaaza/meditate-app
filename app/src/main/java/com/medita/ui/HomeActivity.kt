package com.medita.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.medita.R
import com.medita.databinding.ActivityHomeBinding
import com.medita.databinding.ContentThemesBinding
import com.medita.databinding.ContentTimerBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var timerBinding: ContentTimerBinding
    private lateinit var themesBinding: ContentThemesBinding

    private var countDownTimer: CountDownTimer? = null
    private var totalDurationMillis: Long = 5 * 60_000L
    private var remainingMillis: Long = totalDurationMillis
    private var timerState: TimerState = TimerState.IDLE
    private var selectedChip: TextView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var selectedThemeCard: CardView? = null

    enum class TimerState {
        IDLE, RUNNING, PAUSED, COMPLETED
    }

    enum class Tab {
        MUSIC, TIMER, THEMES
    }

    enum class Theme {
        OCEAN, FOREST, STARRY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerBinding = ContentTimerBinding.bind(binding.timerContent.root)
        themesBinding = ContentThemesBinding.bind(binding.themesContent.root)

        setupMediaPlayer()
        setupTabs()
        setupTimerChips()
        setupTimerControls()
        setupThemeSelection()
        updateTimerDisplay()

        // Select default chip
        selectChip(timerBinding.chip5min, 5)
    }

    private fun setupMediaPlayer() {
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(this@HomeActivity, alertUri)
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupTabs() {
        binding.tabMusic.setOnClickListener { switchTab(Tab.MUSIC) }
        binding.tabTimer.setOnClickListener { switchTab(Tab.TIMER) }
        binding.tabThemes.setOnClickListener { switchTab(Tab.THEMES) }
    }

    private fun switchTab(tab: Tab) {
        // Reset all tabs
        binding.tvTabMusic.setTextColor(ContextCompat.getColor(this, R.color.tab_inactive))
        binding.tvTabTimer.setTextColor(ContextCompat.getColor(this, R.color.tab_inactive))
        binding.tvTabThemes.setTextColor(ContextCompat.getColor(this, R.color.tab_inactive))
        binding.tvTabMusic.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.tvTabTimer.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.tvTabThemes.setTypeface(null, android.graphics.Typeface.NORMAL)
        binding.indicatorMusic.visibility = View.INVISIBLE
        binding.indicatorTimer.visibility = View.INVISIBLE
        binding.indicatorThemes.visibility = View.INVISIBLE

        // Hide all content
        binding.timerContent.root.visibility = View.GONE
        binding.musicContent.root.visibility = View.GONE
        binding.themesContent.root.visibility = View.GONE

        // Show selected tab
        when (tab) {
            Tab.MUSIC -> {
                binding.tvTabMusic.setTextColor(ContextCompat.getColor(this, R.color.primary_green))
                binding.tvTabMusic.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.indicatorMusic.visibility = View.VISIBLE
                binding.musicContent.root.visibility = View.VISIBLE
            }
            Tab.TIMER -> {
                binding.tvTabTimer.setTextColor(ContextCompat.getColor(this, R.color.primary_green))
                binding.tvTabTimer.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.indicatorTimer.visibility = View.VISIBLE
                binding.timerContent.root.visibility = View.VISIBLE
            }
            Tab.THEMES -> {
                binding.tvTabThemes.setTextColor(ContextCompat.getColor(this, R.color.primary_green))
                binding.tvTabThemes.setTypeface(null, android.graphics.Typeface.BOLD)
                binding.indicatorThemes.visibility = View.VISIBLE
                binding.themesContent.root.visibility = View.VISIBLE
            }
        }
    }

    private fun setupTimerChips() {
        timerBinding.chip1min.setOnClickListener { selectChip(it as TextView, 1) }
        timerBinding.chip3min.setOnClickListener { selectChip(it as TextView, 3) }
        timerBinding.chip5min.setOnClickListener { selectChip(it as TextView, 5) }
        timerBinding.chip10min.setOnClickListener { selectChip(it as TextView, 10) }
        timerBinding.chip15min.setOnClickListener { selectChip(it as TextView, 15) }
        timerBinding.chipCustom.setOnClickListener { showCustomTimeDialog() }
    }

    private fun selectChip(chip: TextView, minutes: Int) {
        if (timerState == TimerState.RUNNING) return

        // Reset previous selection
        selectedChip?.let {
            it.background = ContextCompat.getDrawable(this, R.drawable.bg_chip_inactive)
            it.setTextColor(ContextCompat.getColor(this, R.color.primary_green))
        }

        // Set new selection
        chip.background = ContextCompat.getDrawable(this, R.drawable.bg_chip_active)
        chip.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        selectedChip = chip

        // Update duration
        totalDurationMillis = minutes * 60_000L
        remainingMillis = totalDurationMillis
        timerState = TimerState.IDLE
        updateTimerDisplay()
    }

    private fun showCustomTimeDialog() {
        if (timerState == TimerState.RUNNING) return

        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_time, null)
        val etMinutes = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etMinutes)
        val etSeconds = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSeconds)

        AlertDialog.Builder(this)
            .setTitle("Custom Duration")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                val minutes = etMinutes.text.toString().toIntOrNull() ?: 0
                val seconds = etSeconds.text.toString().toIntOrNull()?.coerceIn(0, 59) ?: 0
                val total = (minutes * 60L + seconds) * 1000L
                if (total > 0) {
                    totalDurationMillis = total
                    remainingMillis = total
                    timerState = TimerState.IDLE
                    updateTimerDisplay()

                    // Reset chip selection
                    selectedChip?.let {
                        it.background = ContextCompat.getDrawable(this, R.drawable.bg_chip_inactive)
                        it.setTextColor(ContextCompat.getColor(this, R.color.primary_green))
                    }
                    selectedChip = null
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupTimerControls() {
        timerBinding.btnStart.setOnClickListener {
            when (timerState) {
                TimerState.IDLE, TimerState.PAUSED, TimerState.COMPLETED -> startTimer()
                TimerState.RUNNING -> pauseTimer()
            }
        }

        timerBinding.btnReset.setOnClickListener {
            resetTimer()
        }
    }

    private fun setupThemeSelection() {
        themesBinding.cardOcean.setOnClickListener { selectTheme(Theme.OCEAN, themesBinding.cardOcean) }
        themesBinding.cardForest.setOnClickListener { selectTheme(Theme.FOREST, themesBinding.cardForest) }
        themesBinding.cardStarry.setOnClickListener { selectTheme(Theme.STARRY, themesBinding.cardStarry) }
        
        // Select Ocean as default
        selectTheme(Theme.OCEAN, themesBinding.cardOcean)
    }

    private fun selectTheme(theme: Theme, card: CardView) {
        // Reset all cards elevation
        themesBinding.cardOcean.cardElevation = 4f.dpToPx()
        themesBinding.cardForest.cardElevation = 4f.dpToPx()
        themesBinding.cardStarry.cardElevation = 4f.dpToPx()

        // Highlight selected card
        card.cardElevation = 12f.dpToPx()
        selectedThemeCard = card

        // Show selection feedback
        val themeName = when (theme) {
            Theme.OCEAN -> "Ocean"
            Theme.FOREST -> "Forest"
            Theme.STARRY -> "Starry Sky"
        }
        Toast.makeText(this, "$themeName theme selected", Toast.LENGTH_SHORT).show()
    }

    private fun Float.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }

    private fun startTimer() {
        if (timerState == TimerState.COMPLETED || timerState == TimerState.IDLE) {
            remainingMillis = totalDurationMillis
        }

        countDownTimer = object : CountDownTimer(remainingMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMillis = millisUntilFinished
                updateTimerDisplay()
            }

            override fun onFinish() {
                timerState = TimerState.COMPLETED
                updateTimerDisplay()
                playCompletionSound()
            }
        }.start()

        timerState = TimerState.RUNNING
        updateTimerDisplay()
        timerBinding.btnStart.text = getString(R.string.btn_pause)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerState = TimerState.PAUSED
        updateTimerDisplay()
        timerBinding.btnStart.text = getString(R.string.btn_resume)
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        remainingMillis = totalDurationMillis
        timerState = TimerState.IDLE
        updateTimerDisplay()
        timerBinding.btnStart.text = getString(R.string.btn_start)
    }

    private fun updateTimerDisplay() {
        val totalSeconds = remainingMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        timerBinding.tvMinutes.text = String.format("%02d", minutes)
        timerBinding.tvSeconds.text = String.format("%02d", seconds)

        timerBinding.tvStatus.text = when (timerState) {
            TimerState.IDLE -> "Ready"
            TimerState.RUNNING -> "Counting down"
            TimerState.PAUSED -> "Paused"
            TimerState.COMPLETED -> "Complete"
        }
    }

    private fun playCompletionSound() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                    it.prepare()
                }
                it.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
