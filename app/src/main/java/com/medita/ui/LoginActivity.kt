package com.medita.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.medita.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimations()
        setupClickListeners()
    }

    private fun setupAnimations() {
        // Initial state
        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = 12f
        binding.tilEmail.alpha = 0f
        binding.tilEmail.translationY = 20f
        binding.tilPassword.alpha = 0f
        binding.tilPassword.translationY = 20f
        binding.btnLogin.alpha = 0f
        binding.btnLogin.scaleX = 0.85f
        binding.btnLogin.scaleY = 0.85f

        // Animate title
        binding.tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animate form fields
        binding.tilEmail.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(200)
            .setInterpolator(DecelerateInterpolator())
            .start()

        binding.tilPassword.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setStartDelay(300)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Animate button
        binding.btnLogin.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setStartDelay(400)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            // Navigate to home screen
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            // Handle forgot password - placeholder
        }
    }
}

