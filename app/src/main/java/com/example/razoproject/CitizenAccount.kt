package com.example.razoproject

data class CitizenAccount(
    val name: String,
    val purok: String,
    val identifier: String,
    val password: String,
    val isOfficial: Boolean = false
)
