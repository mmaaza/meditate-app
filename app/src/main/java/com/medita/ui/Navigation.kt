package com.medita.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("home") {
            HomeScreen()
        }
    }
}
