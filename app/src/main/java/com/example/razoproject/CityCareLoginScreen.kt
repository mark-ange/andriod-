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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Figma Color Palette
val FigmaGreenButton = Color(0xFF1E3A2B)
val FigmaTextDark = Color(0xFF1E293B)
val FigmaTextMuted = Color(0xFF64748B)
val FigmaTabBg = Color(0xFFE2E8F0)
val FigmaInputBg = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityCareLoginScreen(
    onLoginSuccess: () -> Unit = {},
    onAnonymousReport: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign in, 1: Full Register

    // Sign In State
    var emailAddress by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register State (Full Personal Info, Location, Security)
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
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(28.dp))

                // 1. App Title (CityCare / CityCare CDO)
                Text(
                    text = if (selectedTab == 1) "CityCare CDO" else "CityCare",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FigmaGreenButton,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                if (selectedTab == 1) {
                    Text(
                        text = "Register to report issues and improve our community.",
                        fontSize = 12.sp,
                        color = FigmaTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Segmented Pill Tab Bar ("Sign up" vs "Register")
                Surface(
                    color = FigmaTabBg,
                    shape = RoundedCornerShape(12.dp),
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
                                    text = "Sign up",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FigmaTextDark
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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FigmaTextDark
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
                        shape = RoundedCornerShape(8.dp),
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
                        shape = RoundedCornerShape(8.dp),
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
                        // Email Address Input
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Email address",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FigmaTextDark
                            )
                            OutlinedTextField(
                                value = emailAddress,
                                onValueChange = {
                                    emailAddress = it
                                    errorMessage = null
                                },
                                placeholder = { Text("Your email or resident ID", fontSize = 13.sp, color = FigmaTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FigmaGreenButton,
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        // Password Input
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Password",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FigmaTextDark
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    errorMessage = null
                                },
                                placeholder = { Text("Password", fontSize = 13.sp, color = FigmaTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility",
                                            tint = FigmaTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FigmaGreenButton,
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                ),
                                shape = RoundedCornerShape(8.dp)
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
                                color = FigmaTextDark,
                                modifier = Modifier.clickable {
                                    onForgotPasswordClick()
                                }
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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FigmaGreenButton,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Other Sign In Options
                        Text(
                            text = "Other sign in options",
                            fontSize = 12.sp,
                            color = FigmaTextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1877F2))
                                        .clickable { onLoginSuccess() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "f",
                                        color = Color.White,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                        .background(Color.White)
                                        .clickable { onLoginSuccess() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "G",
                                        color = Color(0xFFEA4335),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(FigmaGreenButton)
                                        .clickable { onLoginSuccess() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Barangay Seal",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // TAB 1: FULL REGISTRATION FORM (MATCHING FIGMA IMAGE 100%)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // SECTION 1: Personal Information
                            Text(
                                text = "Personal Information",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FigmaTextDark
                            )

                            // Full Name
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Full Name", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                OutlinedTextField(
                                    value = regFullName,
                                    onValueChange = {
                                        regFullName = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("Juan Dela Cruz", fontSize = 12.sp, color = FigmaTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FigmaGreenButton,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedContainerColor = FigmaInputBg
                                    )
                                )
                            }

                            // Phone Number
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Phone Number", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                OutlinedTextField(
                                    value = regPhoneNumber,
                                    onValueChange = {
                                        regPhoneNumber = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("0912 345 6789", fontSize = 12.sp, color = FigmaTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FigmaGreenButton,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedContainerColor = FigmaInputBg
                                    )
                                )
                            }

                            // Email (Optional)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Email (Optional)", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                OutlinedTextField(
                                    value = regEmailOptional,
                                    onValueChange = { regEmailOptional = it },
                                    placeholder = { Text("juan@example.com", fontSize = 12.sp, color = FigmaTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FigmaGreenButton,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedContainerColor = FigmaInputBg
                                    )
                                )
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            // SECTION 2: Location
                            Text(
                                text = "Location",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FigmaTextDark
                            )

                            // Select Barangay
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Select Barangay", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                ExposedDropdownMenuBox(
                                    expanded = isBarangayDropdownExpanded,
                                    onExpandedChange = { isBarangayDropdownExpanded = !isBarangayDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = regBarangay,
                                        onValueChange = { },
                                        readOnly = true,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Place,
                                                contentDescription = null,
                                                tint = FigmaGreenButton,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                tint = FigmaTextMuted
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = FigmaGreenButton,
                                            unfocusedBorderColor = Color(0xFFCBD5E1),
                                            unfocusedContainerColor = FigmaInputBg
                                        )
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

                            // Default Purok / Zone / Street
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Default Purok / Zone / Street", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                OutlinedTextField(
                                    value = regPurokZone,
                                    onValueChange = { regPurokZone = it },
                                    placeholder = { Text("e.g., Zone 3, Acacia St.", fontSize = 12.sp, color = FigmaTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FigmaGreenButton,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedContainerColor = FigmaInputBg
                                    )
                                )
                            }

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            // SECTION 3: Security
                            Text(
                                text = "Security",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FigmaTextDark
                            )

                            // Password
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Password", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                OutlinedTextField(
                                    value = regPassword,
                                    onValueChange = {
                                        regPassword = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("••••••••", fontSize = 12.sp, color = FigmaTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FigmaGreenButton,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedContainerColor = FigmaInputBg
                                    )
                                )
                            }

                            // Confirm Password
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = "Confirm Password", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = FigmaTextMuted)
                                OutlinedTextField(
                                    value = regConfirmPassword,
                                    onValueChange = {
                                        regConfirmPassword = it
                                        errorMessage = null
                                    },
                                    placeholder = { Text("••••••••", fontSize = 12.sp, color = FigmaTextMuted) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FigmaGreenButton,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        unfocusedContainerColor = FigmaInputBg
                                    )
                                )
                            }

                            // Terms & Privacy Checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { regAgreeTerms = !regAgreeTerms }
                            ) {
                                Checkbox(
                                    checked = regAgreeTerms,
                                    onCheckedChange = { regAgreeTerms = it },
                                    colors = CheckboxDefaults.colors(checkedColor = FigmaGreenButton)
                                )
                                Text(
                                    text = "I agree to the Terms of Service and Privacy Policy.",
                                    fontSize = 11.sp,
                                    color = FigmaTextDark,
                                    lineHeight = 15.sp
                                )
                            }

                            // CREATE ACCOUNT BUTTON
                            Button(
                                onClick = {
                                    when {
                                        regFullName.trim().isEmpty() -> {
                                            errorMessage = "Please enter your Full Name!"
                                        }
                                        regPhoneNumber.trim().isEmpty() -> {
                                            errorMessage = "Please enter your Phone Number!"
                                        }
                                        regPassword.trim().isEmpty() -> {
                                            errorMessage = "Please enter a Password!"
                                        }
                                        regPassword != regConfirmPassword -> {
                                            errorMessage = "Passwords do not match!"
                                        }
                                        !regAgreeTerms -> {
                                            errorMessage = "Please agree to the Terms of Service & Privacy Policy!"
                                        }
                                        else -> {
                                            errorMessage = null
                                            onRegisterClick()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FigmaGreenButton,
                                    contentColor = Color.White
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "CREATE ACCOUNT",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Already have an account? Log in
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have an account? ",
                            fontSize = 13.sp,
                            color = FigmaTextMuted
                        )
                        Text(
                            text = "Log in",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FigmaGreenButton,
                            modifier = Modifier.clickable { selectedTab = 0 }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
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
