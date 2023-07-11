package com.practicum.playlistmaker.search.view_model

import android.os.Build
import android.os.Handler
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.utils.ResourceProvider
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.api.SearchInteractor

class SearchViewModel(
    private val resourceProvider: ResourceProvider,
    private val interactor: SearchInteractor,
    private val handler: Handler
) : ViewModel() {

    //todo save search state when rotate screen

    private var lastUnsuccessfulSearch: String = ""
    private val stateLiveData = MutableLiveData<SearchState>()
    private val clearTextState = MutableLiveData<ClearTextState>(ClearTextState.None)
    private var latestSearchText: String? = null

    public override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun observeState(): LiveData<SearchState> = stateLiveData
    fun observeClearTextState(): LiveData<ClearTextState> = clearTextState
    fun textCleared() {
        clearTextState.value = ClearTextState.None
    }

    fun onClearTextPressed() {
        clearTextState.value = ClearTextState.ClearText
        showSearchHistory()
    }

    fun onTextChanged(changedText: String) {
        if (changedText == "") {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
            showSearchHistory()
        } else {
            searchDebounce(changedText)
        }
    }

    fun onClearSearchHistoryPressed() {
        interactor.clearSearchHistory()
        renderState(SearchState.EmptyScreen)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRefreshSearchButtonPressed() {
        searchRequest(lastUnsuccessfulSearch)
    }

    fun onTrackPressed(track: Track) {
        interactor.addTrackToSearchHistory(track)
    }

    fun onFocusChanged(hasFocus: Boolean, searchText: String) {
        if (hasFocus && searchText.isEmpty() && interactor.getSearchHistory().isNotEmpty()) {
            renderState(SearchState.HistoryContent(interactor.getSearchHistory()))
        }
    }

    fun onResume() {
        if (stateLiveData.value is SearchState.HistoryContent) renderState(
            SearchState.HistoryContent(
                interactor.getSearchHistory()
            )
        )
    }

    private fun showSearchHistory() {
        if (interactor.getSearchHistory().isNotEmpty()) renderState(
            SearchState.HistoryContent(
                interactor.getSearchHistory()
            )
        ) else renderState(SearchState.EmptyScreen)
    }


    private fun searchDebounce(searchText: String) {
        if (latestSearchText == searchText) {
            return
        }
        latestSearchText = searchText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(searchText) }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            interactor.searchTracks(searchText, object : SearchInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    if (foundTracks != null) {
                        if (foundTracks.isNotEmpty()) {
                            renderState(SearchState.SearchContent(foundTracks))
                        } else {
                            renderState(
                                SearchState.EmptySearch(
                                    resourceProvider.getString(
                                        R.string.nothing_is_found
                                    )
                                )
                            )
                        }
                    }
                    if (errorMessage != null) {
                        lastUnsuccessfulSearch = searchText
                        renderState(SearchState.Error(errorMessage))
                    }
                }
            })
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2_000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}