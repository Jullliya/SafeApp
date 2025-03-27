package dev.jullls.safeapp.presentation.domain.model

data class Book(
    val id: String,
    val name: String,
    val author: String,
    val posterUrl: String,
    val description: String? = null,
    val publishedDate: String? = null
)
