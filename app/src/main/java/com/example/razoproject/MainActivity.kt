package com.example.razoproject

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {

    // Callback handler for Dashboard internal back navigation
    private var dashboardBackHandler: (() -> Boolean)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        actionBar?.hide()
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                // Navigation Screen State:
                // 0 = Login Screen
                // 1 = Full Registration Screen
                // 2 = Profile Picture Setup Screen
                // 3 = Reset Password Screen
                // 4 = Main Dashboard Screen
                var currentScreen by remember { mutableIntStateOf(0) }
                var showExitDialog by remember { mutableStateOf(false) }

                // ATTACH DIRECT SYSTEM BACK BUTTON & BACK GESTURE CALLBACK
                DisposableEffect(currentScreen) {
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            when (currentScreen) {
                                1, 2, 3 -> {
                                    // Return to Login screen (0)
                                    currentScreen = 0
                                }
                                4 -> {
                                    // On Dashboard: handle subscreen or tab back first
                                    val isHandledByDashboard = dashboardBackHandler?.invoke() ?: false
                                    if (!isHandledByDashboard) {
                                        showExitDialog = true
                                    }
                                }
                                0 -> {
                                    // On Login screen: prompt exit dialog / exit app
                                    showExitDialog = true
                                }
                            }
                        }
                    }

                    onBackPressedDispatcher.addCallback(this@MainActivity, callback)

                    onDispose {
                        callback.remove()
                    }
                }

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
                        onLogout = { currentScreen = 0 },
                        onRegisterBackHandler = { handler ->
                            dashboardBackHandler = handler
                        }
                    )
                }

                // Exit Confirmation Dialog on Root Screens
                if (showExitDialog) {
                    AlertDialog(
                        onDismissRequest = { showExitDialog = false },
                        title = { Text("Exit CityCare CDO?", fontWeight = FontWeight.Bold) },
                        text = { Text("Are you sure you want to exit the application?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showExitDialog = false
                                    (context as? Activity)?.finishAffinity()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                            ) {
                                Text("EXIT APP", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = { showExitDialog = false }
                            ) {
                                Text("CANCEL")
                            }
                        }
                    )
                }
            }
        }
    }
}
