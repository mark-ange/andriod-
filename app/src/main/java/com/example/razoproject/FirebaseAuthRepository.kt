package com.example.razoproject

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository handling Firebase Authentication (Email/Password & Google Sign-In)
 * and Cloud Firestore User Profile persistence for CityCare CDO (`citycarecdo`).
 */
object FirebaseAuthRepository {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Sign in user with Email and Password.
     */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Authentication failed: User is null."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign up user with Email, Password, and Personal Details saved to Cloud Firestore `users` collection.
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        barangay: String,
        purokZone: String
    ): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                // Save user profile data to Firestore
                saveUserProfile(
                    userId = user.uid,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    email = email,
                    barangay = barangay,
                    purokZone = purokZone
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: Could not create user account."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign in or register using Google OAuth ID Token.
     */
    suspend fun signInWithGoogleToken(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                // Save/update basic user info in Firestore if missing
                val existingProfile = getUserProfile(user.uid)
                if (existingProfile.getOrNull() == null) {
                    saveUserProfile(
                        userId = user.uid,
                        fullName = user.displayName ?: "CityCare Resident",
                        phoneNumber = user.phoneNumber ?: "",
                        email = user.email ?: "",
                        barangay = "Iponan",
                        purokZone = ""
                    )
                }
                Result.success(user)
            } else {
                Result.failure(Exception("Google Sign-In failed."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send password reset link to user's registered email.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Store or update Resident Profile in Firestore `users` collection.
     */
    suspend fun saveUserProfile(
        userId: String,
        fullName: String,
        phoneNumber: String,
        email: String,
        barangay: String,
        purokZone: String
    ): Result<Unit> {
        return try {
            val userData = hashMapOf(
                "uid" to userId,
                "fullName" to fullName,
                "phoneNumber" to phoneNumber,
                "email" to email,
                "barangay" to barangay,
                "purokZone" to purokZone,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("users").document(userId).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieve Resident Profile from Firestore `users` collection.
     */
    suspend fun getUserProfile(userId: String): Result<Map<String, Any>?> {
        return try {
            val snapshot = db.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                Result.success(snapshot.data)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sign out current user.
     */
    fun signOut() {
        auth.signOut()
    }
}
