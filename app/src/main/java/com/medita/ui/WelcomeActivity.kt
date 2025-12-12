package com.medita.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.medita.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupClickListeners()
    }

    private fun setupAnimations() {
        // Initial state - invisible and offset
        val views = listOf(
            binding.tvTitle,
            binding.tvSubtitle,
            binding.ivWelcome,
            binding.tvWelcomeTitle,
            binding.tvWelcomeDesc,
            binding.btnBegin
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 20f
        }

        // Animate in with staggered delay
        views.forEachIndexed { index, view ->
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((index * 100).toLong())
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Special scale animation for button
        binding.btnBegin.scaleX = 0.85f
        binding.btnBegin.scaleY = 0.85f
        binding.btnBegin.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setStartDelay(500)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setupClickListeners() {
        binding.btnBegin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}

