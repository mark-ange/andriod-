package com.example.razoproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // Navigation Screen State:
                // 0 = Login Screen
                // 1 = Full Registration Screen
                // 2 = Profile Picture Setup Screen
                // 3 = Reset Password Screen
                // 4 = Main Dashboard Screen
                var currentScreen by remember { mutableIntStateOf(0) }

                when (currentScreen) {
                    0 -> CityCareLoginScreen(
                        onLoginSuccess = { currentScreen = 4 },
                        onAnonymousReport = { currentScreen = 4 },
                        onRegisterClick = { currentScreen = 1 },
                        onForgotPasswordClick = { currentScreen = 3 }
                    )
                    1 -> CityCareRegisterScreen(
                        onRegisterSuccess = { currentScreen = 2 },
                        onLoginClick = { currentScreen = 0 }
                    )
                    2 -> CityCareProfileSetupScreen(
                        onComplete = { currentScreen = 4 },
                        onSkip = { currentScreen = 4 }
                    )
                    3 -> CityCareResetPasswordScreen(
                        onBackClick = { currentScreen = 0 },
                        onResetSuccess = { currentScreen = 0 }
                    )
                    4 -> CityCareDashboardScreen(
                        onLogout = { currentScreen = 0 }
                    )
                }
            }
        }
    }
}
