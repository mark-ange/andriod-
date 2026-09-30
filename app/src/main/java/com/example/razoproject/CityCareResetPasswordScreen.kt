package com.example.razoproject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Design System Colors matching Figma Reset Password Screen
val ResetCardBg = Color(0xFFF0F4F9)
val ResetBorderColor = Color(0xFFCBD5E1)
val ResetGreenPrimary = Color(0xFF1E3A2B)
val ResetTextDark = Color(0xFF1E293B)
val ResetTextMuted = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityCareResetPasswordScreen(
    onBackClick: () -> Unit = {},
    onResetSuccess: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var emailAddress by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ResetBorderColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Row: Back Arrow + Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEBF2FA))
                                    .clickable { onBackClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = ResetGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Reset Password",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ResetGreenPrimary
                                )
                                Text(
                                    text = "Recover your CityCare CDO account",
                                    fontSize = 12.sp,
                                    color = ResetTextMuted
                                )
                            }
                        }

                        // Top Progress Bar
                        LinearProgressIndicator(
                            progress = { 0.5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ResetGreenPrimary,
                            trackColor = Color(0xFFDCFCE7)
                        )

                        // Error Banner
                        errorMessage?.let { msg ->
                            Surface(
                                color = Color(0xFFFFE4E6),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFFE11D48),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        // Success Banner
                        successMessage?.let { msg ->
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    color = Color(0xFF059669),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        if (isLoading) {
                            CircularProgressIndicator(
                                color = ResetGreenPrimary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        // Email Input
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Registered Email Address",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ResetTextDark
                            )
                            OutlinedTextField(
                                value = emailAddress,
                                onValueChange = {
                                    emailAddress = it
                                    errorMessage = null
                                    successMessage = null
                                },
                                placeholder = {
                                    Text(
                                        text = "e.g. juan@kagayan.ph",
                                        fontSize = 12.sp,
                                        color = ResetTextMuted
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ResetGreenPrimary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = ResetCardBg,
                                    unfocusedContainerColor = ResetCardBg
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Submit Button: RESET PASSWORD (FIREBASE AUTH)
                        Button(
                            onClick = {
                                val cleanEmail = emailAddress.trim()
                                if (cleanEmail.isEmpty()) {
                                    errorMessage = "Please enter your registered Email Address!"
                                } else {
                                    errorMessage = null
                                    isLoading = true
                                    coroutineScope.launch {
                                        val result = FirebaseAuthRepository.sendPasswordResetEmail(cleanEmail)
                                        isLoading = false
                                        if (result.isSuccess) {
                                            successMessage = "Password reset email sent! Please check your inbox."
                                            onResetSuccess()
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to send password reset email."
                                        }
                                    }
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ResetGreenPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "SEND RESET EMAIL",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CityCareResetPasswordScreenPreview() {
    MaterialTheme {
        CityCareResetPasswordScreen()
    }
}
