package com.example.razoproject

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Palette matching Eco UI Design
val EcoAuthGreen = Color(0xFF1E7A38)
val EcoAuthDarkGreen = Color(0xFF0F382C)
val EcoAuthTextDark = Color(0xFF1E293B)
val EcoAuthTextMuted = Color(0xFF64748B)
val EcoAuthTabBg = Color(0xFFE2E8F0)
val EcoAuthInputBg = Color(0xFFF8FAFC)

// Aliases for compatibility
val FigmaGreenButton = EcoAuthGreen
val FigmaTextDark = EcoAuthTextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityCareLoginScreen(
    onLoginSuccess: () -> Unit = {},
    onAnonymousReport: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign in, 1: Register

    // Sign In State
    var emailAddress by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register State
    var regFullName by remember { mutableStateOf("") }
    var regPhoneNumber by remember { mutableStateOf("") }
    var regEmailOptional by remember { mutableStateOf("") }
    var regBarangay by remember { mutableStateOf("Iponan") }
    var regPurokZone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regAgreeTerms by remember { mutableStateOf(false) }
    var isBarangayDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val barangayList = listOf("Iponan", "Bulua", "Canitoan", "Carmen", "Patag", "Kauswagan")

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Eco Welcome Hero Header (Matching Image)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(EcoAuthDarkGreen, EcoAuthGreen)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "CityCare CDO",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Let's get started",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF80ED99)
                        )

                        Text(
                            text = if (selectedTab == 0) "Welcome Back" else "Create Resident Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Text(
                            text = "We are here to help our environment through proper waste management & recycling.",
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // 2. Main Form Card
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Segmented Pill Tab Bar ("Sign in" vs "Register")
                    Surface(
                        color = EcoAuthTabBg,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        selectedTab = 0
                                        errorMessage = null
                                    },
                                color = if (selectedTab == 0) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = if (selectedTab == 0) 2.dp else 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Sign In",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoAuthTextDark
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        selectedTab = 1
                                        errorMessage = null
                                    },
                                color = if (selectedTab == 1) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = if (selectedTab == 1) 2.dp else 0.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Register",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoAuthTextDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Error / Success Banners
                    errorMessage?.let { msg ->
                        Surface(
                            color = Color(0xFFFFE4E6),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFFE11D48),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    successMessage?.let { msg ->
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFF059669),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // TAB 0: SIGN IN FORM
                    if (selectedTab == 0) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Email Input
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Email address or Resident ID",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EcoAuthTextDark
                                )
                                OutlinedTextField(
                                    value = emailAddress,
                                    onValueChange = {
                                        emailAddress = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("Enter your email or ID", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EcoAuthGreen,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            // Password Input
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Password",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EcoAuthTextDark
                                )
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("••••••••", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle password visibility",
                                                tint = EcoAuthTextMuted,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EcoAuthGreen,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            // Forgot Password Link
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EcoAuthGreen,
                                    modifier = Modifier.clickable { onForgotPasswordClick() }
                                )
                            }

                            // Primary Sign In Button
                            Button(
                                onClick = {
                                    val cleanEmail = emailAddress.trim()
                                    val cleanPass = password.trim()

                                    if (cleanEmail.isEmpty()) {
                                        errorMessage = "Please enter your Email address or Resident ID!"
                                    } else if (cleanPass.isEmpty()) {
                                        errorMessage = "Please enter your Password!"
                                    } else {
                                        errorMessage = null
                                        onLoginSuccess()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EcoAuthGreen,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "SIGN IN",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Other Sign In Options
                            Text(
                                text = "Or continue with",
                                fontSize = 11.sp,
                                color = EcoAuthTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    OutlinedButton(
                                        onClick = { onLoginSuccess() },
                                        modifier = Modifier.height(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text("Facebook", fontSize = 12.sp, color = Color(0xFF1877F2), fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onLoginSuccess() },
                                        modifier = Modifier.height(42.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Text("Google", fontSize = 12.sp, color = Color(0xFFEA4335), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        // TAB 1: FULL REGISTRATION FORM
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Personal Information",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoAuthTextDark
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Full Name", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoAuthTextMuted)
                                    OutlinedTextField(
                                        value = regFullName,
                                        onValueChange = {
                                            regFullName = it
                                            errorMessage = null
                                        },
                                        placeholder = { Text("Juan Dela Cruz", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoAuthGreen, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Phone Number", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoAuthTextMuted)
                                    OutlinedTextField(
                                        value = regPhoneNumber,
                                        onValueChange = {
                                            regPhoneNumber = it
                                            errorMessage = null
                                        },
                                        placeholder = { Text("0912 345 6789", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoAuthGreen, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                Text(
                                    text = "Location",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoAuthTextDark
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Select Barangay", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoAuthTextMuted)
                                    ExposedDropdownMenuBox(
                                        expanded = isBarangayDropdownExpanded,
                                        onExpandedChange = { isBarangayDropdownExpanded = !isBarangayDropdownExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = regBarangay,
                                            onValueChange = { },
                                            readOnly = true,
                                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoAuthGreen, unfocusedBorderColor = Color(0xFFCBD5E1))
                                        )
                                        ExposedDropdownMenu(
                                            expanded = isBarangayDropdownExpanded,
                                            onDismissRequest = { isBarangayDropdownExpanded = false }
                                        ) {
                                            barangayList.forEach { bgy ->
                                                DropdownMenuItem(
                                                    text = { Text(bgy, fontSize = 13.sp) },
                                                    onClick = {
                                                        regBarangay = bgy
                                                        isBarangayDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Default Purok / Zone / Street", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoAuthTextMuted)
                                    OutlinedTextField(
                                        value = regPurokZone,
                                        onValueChange = { regPurokZone = it },
                                        placeholder = { Text("e.g., Zone 3, Acacia St.", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoAuthGreen, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                Text(
                                    text = "Security",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoAuthTextDark
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Password", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoAuthTextMuted)
                                    OutlinedTextField(
                                        value = regPassword,
                                        onValueChange = {
                                            regPassword = it
                                            errorMessage = null
                                        },
                                        placeholder = { Text("••••••••", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoAuthGreen, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = "Confirm Password", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = EcoAuthTextMuted)
                                    OutlinedTextField(
                                        value = regConfirmPassword,
                                        onValueChange = {
                                            regConfirmPassword = it
                                            errorMessage = null
                                        },
                                        placeholder = { Text("••••••••", fontSize = 12.sp, color = EcoAuthTextMuted) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EcoAuthGreen, unfocusedBorderColor = Color(0xFFCBD5E1))
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { regAgreeTerms = !regAgreeTerms }
                                ) {
                                    Checkbox(
                                        checked = regAgreeTerms,
                                        onCheckedChange = { regAgreeTerms = it },
                                        colors = CheckboxDefaults.colors(checkedColor = EcoAuthGreen)
                                    )
                                    Text(
                                        text = "I agree to the Terms of Service and Privacy Policy.",
                                        fontSize = 11.sp,
                                        color = EcoAuthTextDark,
                                        lineHeight = 15.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        when {
                                            regFullName.trim().isEmpty() -> errorMessage = "Please enter your Full Name!"
                                            regPhoneNumber.trim().isEmpty() -> errorMessage = "Please enter your Phone Number!"
                                            regPassword.trim().isEmpty() -> errorMessage = "Please enter a Password!"
                                            regPassword != regConfirmPassword -> errorMessage = "Passwords do not match!"
                                            !regAgreeTerms -> errorMessage = "Please agree to the Terms of Service and Privacy Policy!"
                                            else -> {
                                                errorMessage = null
                                                onRegisterClick()
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(25.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoAuthGreen, contentColor = Color.White)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("CREATE ACCOUNT", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CityCareLoginScreenPreview() {
    MaterialTheme {
        CityCareLoginScreen()
    }
}
