package dev.jullls.safeapp.presentation.data.shared_preference

import android.content.Context
import android.content.SharedPreferences
import dev.jullls.safeapp.presentation.exception.AuthException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SharedPrefs(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    @Throws(AuthException::class)
    fun saveUser(login: String, password: String) {
        try {
            val hashedPassword = try {
                password.sha256()
            } catch (e: Exception) {
                throw AuthException.HashingError("Failed to hash password: ${e.message}")
            }

            if (!sharedPref.edit().putString(login, hashedPassword).commit()) {
                throw AuthException.StorageError("Failed to save user data")
            }
        } catch (e: SecurityException) {
            throw AuthException.StorageError("Storage access denied")
        }
    }

    @Throws(AuthException::class)
    fun checkUser(login: String, password: String): Boolean {
        return try {
            val savedHashedPassword = sharedPref.getString(login, null)
                ?: return false

            val inputHashedPassword = try {
                password.sha256()
            } catch (e: Exception) {
                throw AuthException.HashingError("Failed to hash input password")
            }

            savedHashedPassword == inputHashedPassword
        } catch (e: SecurityException) {
            throw AuthException.StorageError("Storage access denied")
        }
    }

    private fun String.sha256(): String {
        return try {
            MessageDigest
                .getInstance("SHA-256")
                .digest(this.toByteArray(Charsets.UTF_8))
                .fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            throw AuthException.HashingError("SHA-256 algorithm not available")
        } catch (e: Exception) {
            throw AuthException.HashingError("Hashing failed: ${e.message}")
        }
    }
}