package dev.jullls.safeapp.presentation.ui.home_fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.jullls.safeapp.R
import dev.jullls.safeapp.databinding.ItemBookBinding
import dev.jullls.safeapp.presentation.domain.model.Book

class BookHomeFragmentAdapter(
    private val bookList: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookHomeFragmentAdapter.BookViewHolder>() {

    inner class BookViewHolder(
        view: View,
        private val onItemClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBookBinding.bind(view)

        fun bind(book: Book) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(book.posterUrl)
                    .placeholder(R.drawable.book)
                    .into(ivItemBookCard)

                tvItemBookCardTitle.text = book.name
                tvItemBookCardAuthor.text = book.author ?: "Unknown Author"

                root.setOnClickListener {
                    onItemClick(book)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_book, parent, false
        )
        return BookViewHolder(view, onItemClick)
    }

    override fun getItemCount(): Int = bookList.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(bookList[position])
    }
}