package dev.jullls.safeapp.presentation.ui.home_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import dev.jullls.safeapp.R
import dev.jullls.safeapp.databinding.FragmentHomeBinding
import dev.jullls.safeapp.presentation.vm.BooksViewModel
import dev.jullls.safeapp.presentation.domain.model.Book
import androidx.fragment.app.viewModels

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BooksViewModel by viewModels()
    private lateinit var adapter: BookHomeFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        viewModel.loadBooks("Harry Potter")
    }

    private fun setupUI() {
        adapter = BookHomeFragmentAdapter(emptyList()) { book ->
            showBookDetails(book)
        }

        with(binding) {
            rvBooksHome.setHasFixedSize(true)
            rvBooksHome.layoutManager = GridLayoutManager(requireContext(), 2)
            rvBooksHome.adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter = BookHomeFragmentAdapter(books) { book ->
                showBookDetails(book)
            }
            binding.rvBooksHome.adapter = adapter
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showBookDetails(book: Book) {
        Toast.makeText(requireContext(), "Selected: ${book.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}