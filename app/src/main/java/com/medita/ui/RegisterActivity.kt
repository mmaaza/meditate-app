package com.medita.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.medita.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupClickListeners()
    }

    private fun setupAnimations() {
        // Initial state
        val formViews = listOf(
            binding.tvTitle,
            binding.tilName,
            binding.tilEmail,
            binding.tilPassword,
            binding.tilConfirmPassword
        )

        formViews.forEach { view ->
            view.alpha = 0f
            view.translationY = 20f
        }

        binding.btnSignUp.alpha = 0f
        binding.btnSignUp.scaleX = 0.85f
        binding.btnSignUp.scaleY = 0.85f

        // Animate form fields
        formViews.forEachIndexed { index, view ->
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay((index * 100).toLong())
                .setInterpolator(DecelerateInterpolator())
                .start()
        }

        // Animate button
        binding.btnSignUp.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setStartDelay(500)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            // Navigate to home screen after registration
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity() // Clear back stack
        }

        binding.tvSignIn.setOnClickListener {
            finish() // Go back to login
        }
    }
}

