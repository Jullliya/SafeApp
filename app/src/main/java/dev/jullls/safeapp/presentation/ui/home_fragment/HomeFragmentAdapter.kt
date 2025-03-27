package dev.jullls.safeapp.presentation.ui.home_fragment

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dev.jullls.safeapp.R
import dev.jullls.safeapp.databinding.ItemBookBinding
import dev.jullls.safeapp.presentation.domain.model.Book

class BookHomeFragmentAdapter(
    private val bookList: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookHomeFragmentAdapter.BookViewHolder>() {

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemBookBinding.bind(view)

        fun bind(book: Book) {
            with(binding) {
                val imageUrl = book.posterUrl.replace("http://", "https://")

                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.book)
                    .error(R.drawable.book)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Log.e("Glide", "Failed to load image: $imageUrl", e)
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(ivItemBookCard)

                tvItemBookCardTitle.text = book.name
                tvItemBookCardAuthor.text = book.author ?: "Unknown Author"

                root.setOnClickListener { onItemClick(book) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_book, parent, false
        )
        return BookViewHolder(view)
    }
    override fun getItemCount(): Int = bookList.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(bookList[position])
    }
}