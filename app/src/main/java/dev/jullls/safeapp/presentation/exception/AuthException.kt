package dev.jullls.safeapp.presentation.exception

sealed class AuthException(message: String) : Exception(message) {
    class HashingError(message: String) : AuthException(message)
    class StorageError(message: String) : AuthException(message)
}