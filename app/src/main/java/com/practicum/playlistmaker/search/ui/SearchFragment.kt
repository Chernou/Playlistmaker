package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.view_model.ClearTextState
import com.practicum.playlistmaker.search.view_model.SearchState
import com.practicum.playlistmaker.search.view_model.SearchViewModel
import com.practicum.playlistmaker.utils.NavigationRouter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SearchFragment : Fragment() {

    private val mainThreadHandler: Handler by inject()
    private var isClickAllowed = true
    private var trackList = ArrayList<Track>()
    private val viewModel: SearchViewModel by viewModel()

    private val router: NavigationRouter by inject {
        parametersOf(requireActivity())
    }

    @SuppressLint("NotifyDataSetChanged")
    private val searchResultAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackPressed(it) //todo notifyItemInserted when appropriate
            router.openTrack(OPEN_TRACK_INTENT, it)
        }
    }

    private val searchHistoryAdapter = TrackAdapter {
        if (clickDebounce()) {
            viewModel.onTrackPressed(it)
            router.openTrack(OPEN_TRACK_INTENT, it)
        }
    }

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        if (savedInstanceState != null) {
            binding.searchEditText.text =
                savedInstanceState.getCharSequence(SEARCH_TEXT) as Editable
        }

        binding.searchHistoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchHistoryAdapter
        }

        binding.searchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchResultAdapter
        }

        binding.searchEditText.addTextChangedListener(searchTextWatcher)

        viewModel.observeClearTextState().observe(viewLifecycleOwner) { clearTextState ->
            if (clearTextState is ClearTextState.ClearText) {
                clearSearchText()
                hideKeyboard()
                viewModel.textCleared()
            }
        }

        binding.clearTextImage.setOnClickListener {
            viewModel.onClearTextPressed()
        }

        binding.refreshSearchButton.setOnClickListener {
            viewModel.onRefreshSearchButtonPressed()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.onFocusChanged(hasFocus, binding.searchEditText.text.toString())
        }

        binding.clearSearchHistoryButton.setOnClickListener {
            viewModel.onClearSearchHistoryPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(SEARCH_TEXT, binding.searchEditText.text)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchEditText.removeTextChangedListener(searchTextWatcher)
        viewModel.onCleared()
    }

    override fun onDestroy() {
        super.onDestroy()
        router.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showProgressBar()
            is SearchState.SearchContent -> showSearchResult(state.tracks)
            is SearchState.HistoryContent -> showSearchHistoryLayout(state.tracks)
            is SearchState.EmptySearch -> showEmptySearch(state.emptySearchMessage)
            is SearchState.Error -> showSearchError(state.errorMessage)
            is SearchState.EmptyScreen -> showEmptyScreen()
        }
    }

    private fun clearSearchText() {
        binding.searchEditText.text.clear()
        binding.clearTextImage.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private val searchTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            viewModel.onTextChanged(p0.toString() ?: "")
            //todo hide search result when editText is empty
        }

        override fun afterTextChanged(editable: Editable?) {
            if (editable?.isNotEmpty() == true) {
                binding.clearTextImage.visibility = View.VISIBLE
            } else {
                binding.clearTextImage.visibility = View.GONE
            }
        }
    }

    private fun showEmptyScreen() {
        binding.searchHistoryRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun showSearchResult(tracks: List<Track>) {
        binding.searchRecyclerView.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
        trackList.clear()
        trackList.addAll(tracks)
        searchResultAdapter.trackList = trackList
        searchResultAdapter.notifyDataSetChanged()
    }

    private fun showSearchHistoryLayout(searchHistory: List<Track>) {
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.VISIBLE
        trackList.clear()
        trackList.addAll(searchHistory)
        searchHistoryAdapter.trackList = trackList
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun showEmptySearch(emptySearchMessage: String) {
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        binding.searchErrorText.text = emptySearchMessage
        binding.searchErrorImage.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.nothing_is_found
            )
        )
    }

    private fun showSearchError(errorMessage: String) {
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.searchErrorLayout.visibility = View.VISIBLE
        binding.searchHistoryLayout.visibility = View.GONE
        searchResultAdapter.trackList.clear()
        searchResultAdapter.notifyDataSetChanged()
        binding.searchErrorText.text = errorMessage
        binding.searchErrorImage.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.no_internet_connection
            )
        )
    }

    private fun showProgressBar() {
        binding.searchRecyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.searchErrorLayout.visibility = View.GONE
        binding.searchHistoryLayout.visibility = View.GONE
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val CLICK_DEBOUNCE_DELAY = 1_000L
        const val OPEN_TRACK_INTENT = "TRACK INTENT"
    }
}