package dev.jullls.safeapp.presentation.di.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jullls.safeapp.presentation.data.network.GoogleBooksApiService
import dev.jullls.safeapp.presentation.domain.model.Book
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    private val apiService = GoogleBooksApiService.create()

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> get() = _books

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.searchBooks(query)
                val booksList = response.items?.map { item ->
                    Book(
                        id = item.id,
                        name = item.volumeInfo.title,
                        author = item.volumeInfo.authors?.joinToString(", ") ?: "Unknown Author",
                        posterUrl = item.volumeInfo.imageLinks?.thumbnail ?: "",
                        description = item.volumeInfo.description,
                        publishedDate = item.volumeInfo.publishedDate
                    )
                } ?: emptyList()
                _books.postValue(booksList)
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Error loading books", e)
                _books.postValue(emptyList())
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}